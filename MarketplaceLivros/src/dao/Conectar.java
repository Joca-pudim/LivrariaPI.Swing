package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectar {
    private static final String URL =
        "jdbc:sqlserver://localhost:1433;databaseName=LivrariaPI;"
        + "encrypt=true;trustServerCertificate=true";
    private static final String USUARIO = "ProjetoPI";
    private static final String SENHA = "123";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}
