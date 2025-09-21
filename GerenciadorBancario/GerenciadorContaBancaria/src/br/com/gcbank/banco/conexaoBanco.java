package br.com.gcbank.banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexaoBanco {
    // Ajuste conforme seu ambiente:
    private static final String URL = "jdbc:mysql://localhost:3306/banco_digital";
    private static final String USER = "root";
    private static final String PASS = "senha";

    // Força o carregamento do driver (evita "No suitable driver found" se o .jar não estiver resolvendo sozinho)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // driver novo
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC do MySQL não encontrado no classpath.", e);
        }
    }

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
