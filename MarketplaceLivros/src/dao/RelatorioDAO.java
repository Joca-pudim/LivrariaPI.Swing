package dao;

import java.sql.*;
import util.Tabela;

/** Consultas de relatório: agregação (SUM, COUNT, AVG, MIN, MAX) e junções. */
public class RelatorioDAO {

    /** SUM + COUNT + GROUP BY (ranking de lojas). */
    public Tabela totalVendidoPorLoja() throws SQLException {
        return consultar(
            "SELECT l.nome AS loja, COUNT(DISTINCT pe.codigo) AS pedidos, "
          + "SUM(ip.quantidade) AS livros_vendidos, "
          + "SUM(ip.quantidade * ip.preco_unitario) AS total_vendido "
          + "FROM loja l "
          + "JOIN produto p ON p.cod_loja = l.codigo "
          + "JOIN item_pedido ip ON ip.cod_produto = p.codigo "
          + "JOIN pedido pe ON pe.codigo = ip.cod_pedido "
          + "WHERE pe.status <> 'Cancelado' "
          + "GROUP BY l.codigo, l.nome ORDER BY total_vendido DESC");
    }

    /** COUNT + SUM + GROUP BY. */
    public Tabela pedidosPorStatus() throws SQLException {
        return consultar(
            "SELECT status, COUNT(*) AS quantidade, SUM(valor_total) AS valor_total "
          + "FROM pedido GROUP BY status ORDER BY quantidade DESC");
    }

    /** AVG, MIN, MAX, COUNT. */
    public Tabela ticketMedio() throws SQLException {
        return consultar(
            "SELECT COUNT(*) AS pedidos, AVG(valor_total) AS ticket_medio, "
          + "MIN(valor_total) AS menor_pedido, MAX(valor_total) AS maior_pedido "
          + "FROM pedido WHERE status <> 'Cancelado'");
    }

    /** WHERE estoque <= limite (com junção em categoria). */
    public Tabela estoqueBaixo(int codLoja, int limite) throws SQLException {
        return consultar(
            "SELECT p.codigo, p.titulo, c.nome AS categoria, p.estoque "
          + "FROM produto p JOIN categoria c ON c.codigo = p.cod_categoria "
          + "WHERE p.cod_loja = ? AND p.estoque <= ? ORDER BY p.estoque, p.titulo", codLoja, limite);
    }

    /** MIN, MAX, AVG, COUNT por categoria. */
    public Tabela extremosPorCategoria() throws SQLException {
        return consultar(
            "SELECT c.nome AS categoria, COUNT(*) AS livros, MIN(p.preco) AS mais_barato, "
          + "MAX(p.preco) AS mais_caro, AVG(p.preco) AS preco_medio "
          + "FROM categoria c JOIN produto p ON p.cod_categoria = c.codigo "
          + "GROUP BY c.codigo, c.nome ORDER BY c.nome");
    }

    /**item_pedido + pedido + produto + cliente (vendas da loja). */
    public Tabela vendasDaLoja(int codLoja) throws SQLException {
        return consultar(
            "SELECT pe.codigo AS pedido, pe.data_pedido, p.titulo AS livro, cl.nome AS comprador, "
          + "ip.quantidade, ip.preco_unitario, (ip.quantidade * ip.preco_unitario) AS subtotal, "
          + "pe.status AS status_pedido "
          + "FROM item_pedido ip "
          + "JOIN pedido pe ON pe.codigo = ip.cod_pedido "
          + "JOIN produto p ON p.codigo = ip.cod_produto "
          + "JOIN cliente cl ON cl.codigo = pe.cod_cliente "
          + "WHERE p.cod_loja = ? ORDER BY pe.data_pedido DESC, pe.codigo DESC", codLoja);
    }

    private Tabela consultar(String sql, int... params) throws SQLException {
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setInt(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return Tabela.de(rs);
            }
        }
    }
}
