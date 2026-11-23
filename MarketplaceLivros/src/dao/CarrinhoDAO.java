package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ItemCarrinho;

public class CarrinhoDAO {

    private int obterOuCriarCarrinho(Connection conn, int codCliente) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT codigo FROM carrinho WHERE cod_cliente = ?")) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO carrinho (cod_cliente) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, codCliente);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** Adiciona (ou soma) um livro no carrinho, respeitando o estoque. */
    public void adicionar(int codCliente, int codProduto, int qtd) throws SQLException {
        if (qtd <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        try (Connection conn = Conectar.getConnection()) {
            int codCarrinho = obterOuCriarCarrinho(conn, codCliente);

            int estoque;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT estoque FROM produto WHERE codigo = ?")) {
                ps.setInt(1, codProduto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new IllegalArgumentException("Livro não encontrado.");
                    estoque = rs.getInt(1);
                }
            }

            int atual = 0;
            boolean existe = false;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT quantidade FROM item_carrinho WHERE cod_carrinho = ? AND cod_produto = ?")) {
                ps.setInt(1, codCarrinho);
                ps.setInt(2, codProduto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) { existe = true; atual = rs.getInt(1); }
                }
            }

            if (atual + qtd > estoque) {
                throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + estoque
                        + " (já no carrinho: " + atual + ").");
            }

            String sql = existe
                ? "UPDATE item_carrinho SET quantidade = ? WHERE cod_carrinho = ? AND cod_produto = ?"
                : "INSERT INTO item_carrinho (quantidade, cod_carrinho, cod_produto) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, atual + qtd);
                ps.setInt(2, codCarrinho);
                ps.setInt(3, codProduto);
                ps.executeUpdate();
            }
        }
    }

    /** carrinho + item_carrinho + produto + loja. */
    public List<ItemCarrinho> listar(int codCliente) throws SQLException {
        String sql = "SELECT p.codigo, p.titulo, p.autor, l.nome AS loja, p.preco, "
                   + "ic.quantidade, p.estoque "
                   + "FROM carrinho c "
                   + "JOIN item_carrinho ic ON ic.cod_carrinho = c.codigo "
                   + "JOIN produto p ON p.codigo = ic.cod_produto "
                   + "JOIN loja l ON l.codigo = p.cod_loja "
                   + "WHERE c.cod_cliente = ? ORDER BY p.titulo";
        List<ItemCarrinho> lista = new ArrayList<>();
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ItemCarrinho(rs.getInt("codigo"), rs.getString("titulo"),
                            rs.getString("autor"), rs.getString("loja"), rs.getBigDecimal("preco"),
                            rs.getInt("quantidade"), rs.getInt("estoque")));
                }
            }
        }
        return lista;
    }

    /**SUM do valor do carrinho.*/
    public BigDecimal total(int codCliente) throws SQLException {
        String sql = "SELECT COALESCE(SUM(p.preco * ic.quantidade), 0) "
                   + "FROM carrinho c "
                   + "JOIN item_carrinho ic ON ic.cod_carrinho = c.codigo "
                   + "JOIN produto p ON p.codigo = ic.cod_produto "
                   + "WHERE c.cod_cliente = ?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getBigDecimal(1);
            }
        }
    }

    public void remover(int codCliente, int codProduto) throws SQLException {
        String sql = "DELETE FROM item_carrinho WHERE cod_produto = ? AND cod_carrinho IN "
                   + "(SELECT codigo FROM carrinho WHERE cod_cliente = ?)";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codProduto);
            ps.setInt(2, codCliente);
            ps.executeUpdate();
        }
    }

    public void limpar(int codCliente) throws SQLException {
        String sql = "DELETE FROM item_carrinho WHERE cod_carrinho IN "
                   + "(SELECT codigo FROM carrinho WHERE cod_cliente = ?)";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codCliente);
            ps.executeUpdate();
        }
    }
}
