package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Categoria;
import model.Produto;

public class ProdutoDAO {

    public List<Categoria> listarCategorias() throws SQLException {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT codigo, nome FROM categoria ORDER BY nome";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Categoria(rs.getInt("codigo"), rs.getString("nome")));
            }
        }
        return lista;
    }

    // Catálogo com junção (produto + loja + categoria) e filtros opcionais.
    public List<Produto> buscar(String busca, Integer codCategoria,
                                BigDecimal precoMin, BigDecimal precoMax) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT p.codigo, p.titulo, p.autor, p.preco, p.estoque, p.condicao, "
          + "l.nome AS loja, c.nome AS categoria "
          + "FROM produto p "
          + "JOIN loja l ON l.codigo = p.cod_loja "
          + "JOIN categoria c ON c.codigo = p.cod_categoria "
          + "WHERE p.estoque > 0 ");
        List<Object> params = new ArrayList<>();

        if (busca != null && !busca.isBlank()) {
            sql.append("AND (p.titulo LIKE ? OR p.autor LIKE ?) ");
            params.add("%" + busca + "%");
            params.add("%" + busca + "%");
        }
        if (codCategoria != null) {
            sql.append("AND p.cod_categoria = ? ");
            params.add(codCategoria);
        }
        if (precoMin != null) {
            sql.append("AND p.preco >= ? ");
            params.add(precoMin);
        }
        if (precoMax != null) {
            sql.append("AND p.preco <= ? ");
            params.add(precoMax);
        }
        sql.append("ORDER BY p.titulo");

        List<Produto> lista = new ArrayList<>();
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Produto(
                        rs.getInt("codigo"), rs.getString("titulo"), rs.getString("autor"),
                        rs.getBigDecimal("preco"), rs.getInt("estoque"),
                        rs.getString("condicao"), rs.getString("loja"),
                        rs.getString("categoria")));
                }
            }
        }
        return lista;
    }
}