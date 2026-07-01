package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoPostgres {

    private ConexaoPostgres() {
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(
                ConexaoHibernate.getJdbcUrl(),
                ConexaoHibernate.DB_USER,
                ConexaoHibernate.DB_PASSWORD);
    }
}
