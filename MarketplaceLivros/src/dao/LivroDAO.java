package dao;

import java.math.BigDecimal;
import java.sql.*;
import util.Tabela;

/** Operações da loja sobre os próprios livros (cadastro de produtos do marketplace). */
public class LivroDAO {

    public void inserir(int codLoja, String titulo, String autor, BigDecimal preco,
                        int estoque, String condicao, int codCategoria) throws SQLException {
        String sql = "INSERT INTO produto (titulo, autor, preco, estoque, condicao, cod_loja, cod_categoria) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ps.setString(2, autor);
            ps.setBigDecimal(3, preco);
            ps.setInt(4, estoque);
            ps.setString(5, condicao);
            ps.setInt(6, codLoja);
            ps.setInt(7, codCategoria);
            ps.executeUpdate();
        }
    }

    /** produto + categoria, só da loja informada. */
    public Tabela listarDaLoja(int codLoja) throws SQLException {
        String sql = "SELECT p.codigo, p.titulo, p.autor, c.nome AS categoria, p.condicao, p.preco, p.estoque "
                   + "FROM produto p JOIN categoria c ON c.codigo = p.cod_categoria "
                   + "WHERE p.cod_loja = ? ORDER BY p.titulo";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, codLoja);
            try (ResultSet rs = ps.executeQuery()) {
                return Tabela.de(rs);
            }
        }
    }

    /** Só altera livro que pertence à loja (cod_loja na cláusula WHERE). */
    public boolean atualizar(int codLoja, int codProduto, BigDecimal preco, int estoque) throws SQLException {
        String sql = "UPDATE produto SET preco = ?, estoque = ? WHERE codigo = ? AND cod_loja = ?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, preco);
            ps.setInt(2, estoque);
            ps.setInt(3, codProduto);
            ps.setInt(4, codLoja);
            return ps.executeUpdate() > 0;
        }
    }
}
