package dao;

import java.sql.*;
import util.Seguranca;

public class ClienteDAO {

    public void inserir(String nome, String cpf, Date dataAniv, String email,
                        String telefone, String cep, String endereco,
                        String numeroEnd, String complemento, String bairro,
                        String cidade, String senha) throws SQLException {

        String sql = "INSERT INTO cliente "
                   + "(nome, cpf, data_aniv, email, telefone, cep, endereco, "
                   + "numero_end, complemento, bairro, cidade, senha) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setDate(3, dataAniv);
            ps.setString(4, email);
            ps.setString(5, telefone);
            ps.setString(6, cep);
            ps.setString(7, endereco);
            ps.setString(8, numeroEnd);
            if (complemento == null || complemento.isBlank()) {
                ps.setNull(9, Types.VARCHAR);
            } else {
                ps.setString(9, complemento);
            }
            ps.setString(10, bairro);
            ps.setString(11, cidade);
            ps.setString(12, Seguranca.hash(senha));
            ps.executeUpdate();
        }
    }

    // true se já existe cliente com esse e-mail ou CPF
    public boolean existe(String email, String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE email = ? OR cpf = ?";
        try (Connection conn = Conectar.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}