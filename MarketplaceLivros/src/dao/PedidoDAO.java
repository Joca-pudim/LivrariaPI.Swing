package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ItemCarrinho;
import util.Tabela;

public class PedidoDAO {

    public static final String PIX = "PIX", CARTAO = "CARTAO", BOLETO = "BOLETO";

    /**
     * Finaliza a compra numa única transação: cria pedido + itens, baixa o estoque,
     * registra o pagamento e esvazia o carrinho. Retorna o código do pedido.
     */
    public int finalizarCompra(int codCliente, String forma, int parcelas) throws SQLException {
        try (Connection conn = Conectar.getConnection()) {
            conn.setAutoCommit(false);
            try {
                //itens do carrinho com preço atual
                List<ItemCarrinho> itens = new ArrayList<>();
                String sqlItens = "SELECT p.codigo, p.titulo, p.autor, l.nome AS loja, p.preco, "
                                + "ic.quantidade, p.estoque "
                                + "FROM carrinho c "
                                + "JOIN item_carrinho ic ON ic.cod_carrinho = c.codigo "
                                + "JOIN produto p ON p.codigo = ic.cod_produto "
                                + "JOIN loja l ON l.codigo = p.cod_loja "
                                + "WHERE c.cod_cliente = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlItens)) {
                    ps.setInt(1, codCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            itens.add(new ItemCarrinho(rs.getInt("codigo"), rs.getString("titulo"),
                                    rs.getString("autor"), rs.getString("loja"), rs.getBigDecimal("preco"),
                                    rs.getInt("quantidade"), rs.getInt("estoque")));
                        }
                    }
                }
                if (itens.isEmpty()) throw new IllegalArgumentException("O carrinho está vazio.");

                BigDecimal total = BigDecimal.ZERO;
                for (ItemCarrinho it : itens) total = total.add(it.getSubtotal());

                boolean aprovado = !BOLETO.equals(forma);
                int nParcelas = CARTAO.equals(forma) ? Math.max(1, Math.min(12, parcelas)) : 1;

                int codPedido;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO pedido (cod_cliente, status, valor_total) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, codCliente);
                    ps.setString(2, aprovado ? "Pago" : "Aguardando pagamento");
                    ps.setBigDecimal(3, total);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        codPedido = rs.getInt(1);
                    }
                }

                for (ItemCarrinho it : itens) {
                    // baixa de estoque protegida contra venda acima do disponível
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE produto SET estoque = estoque - ? WHERE codigo = ? AND estoque >= ?")) {
                        ps.setInt(1, it.getQuantidade());
                        ps.setInt(2, it.getCodProduto());
                        ps.setInt(3, it.getQuantidade());
                        if (ps.executeUpdate() == 0) {
                            throw new IllegalArgumentException(
                                "Estoque insuficiente para: " + it.getTitulo());
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO item_pedido (cod_pedido, cod_produto, quantidade, preco_unitario) "
                          + "VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, codPedido);
                        ps.setInt(2, it.getCodProduto());
                        ps.setInt(3, it.getQuantidade());
                        ps.setBigDecimal(4, it.getPreco());
                        ps.executeUpdate();
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO pagamento (cod_pedido, forma, status, valor, parcelas, data_pagamento) "
                      + "VALUES (?, ?, ?, ?, ?, " + (aprovado ? "GETDATE()" : "NULL") + ")")) {
                    ps.setInt(1, codPedido);
                    ps.setString(2, forma);
                    ps.setString(3, aprovado ? "Aprovado" : "Pendente");
                    ps.setBigDecimal(4, total);
                    ps.setInt(5, nParcelas);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM item_carrinho WHERE cod_carrinho IN "
                      + "(SELECT codigo FROM carrinho WHERE cod_cliente = ?)")) {
                    ps.setInt(1, codCliente);
                    ps.executeUpdate();
                }

                conn.commit();
                return codPedido;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    /**pedido + pagamento + cliente (pedidos do cliente com status do pagamento). */
    public Tabela pedidosComPagamento(int codCliente) throws SQLException {
        String sql = "SELECT pe.codigo AS pedido, pe.data_pedido, pe.valor_total, "
                   + "pg.forma, pg.parcelas, pg.status AS status_pagamento, pe.status AS status_pedido "
                   + "FROM pedido pe "
                   + "JOIN pagamento pg ON pg.cod_pedido = pe.codigo "
                   + "JOIN cliente c ON c.codigo = pe.cod_cliente "
                   + "WHERE c.codigo = ? ORDER BY pe.data_pedido DESC, pe.codigo DESC";
        return consultar(sql, codCliente);
    }

    /**pedido + item_pedido + produto + loja (+ pagamento). */
    public Tabela historicoItens(int codCliente) throws SQLException {
        String sql = "SELECT pe.codigo AS pedido, pe.data_pedido, p.titulo AS livro, l.nome AS loja, "
                   + "ip.quantidade, ip.preco_unitario, (ip.quantidade * ip.preco_unitario) AS subtotal, "
                   + "pe.status AS status_pedido, pg.status AS status_pagamento "
                   + "FROM pedido pe "
                   + "JOIN item_pedido ip ON ip.cod_pedido = pe.codigo "
                   + "JOIN produto p ON p.codigo = ip.cod_produto "
                   + "JOIN loja l ON l.codigo = p.cod_loja "
                   + "LEFT JOIN pagamento pg ON pg.cod_pedido = pe.codigo "
                   + "WHERE pe.cod_cliente = ? ORDER BY pe.data_pedido DESC, pe.codigo DESC, p.titulo";
        return consultar(sql, codCliente);
    }

    /** Confirma o pagamento pendente (ex.: boleto) de um pedido do próprio cliente. */
    public boolean confirmarPagamento(int codPedido, int codCliente) throws SQLException {
        try (Connection conn = Conectar.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int n;
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE pagamento SET status = 'Aprovado', data_pagamento = GETDATE() "
                      + "WHERE cod_pedido = ? AND status = 'Pendente' "
                      + "AND cod_pedido IN (SELECT codigo FROM pedido WHERE cod_cliente = ?)")) {
                    ps.setInt(1, codPedido);
                    ps.setInt(2, codCliente);
                    n = ps.executeUpdate();
                }
                if (n > 0) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE pedido SET status = 'Pago' WHERE codigo = ?")) {
                        ps.setInt(1, codPedido);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
                return n > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private Tabela consultar(String sql, int codCliente) throws SQLException {
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                return Tabela.de(rs);
            }
        }
    }
}
