package dao;

import java.sql.*;
import model.Usuario;
import util.Seguranca;

public class LoginDAO {

    // Retorna o usuário se e-mail e senha conferem, ou null se não.
    public Usuario autenticar(String email, String senha, String tipo) throws SQLException {
        String tabela = tipo.equals("Loja") ? "loja" : "cliente";
        String sql = "SELECT codigo, nome FROM " + tabela + " WHERE email = ? AND senha = ?";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, Seguranca.hash(senha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getInt("codigo"), rs.getString("nome"), tipo);
                }
            }
        }
        return null;
    }
}