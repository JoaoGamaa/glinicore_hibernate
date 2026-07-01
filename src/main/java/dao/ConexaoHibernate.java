package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ConexaoHibernate {

    static final String DB_HOST = "localhost";
    static final String DB_PORT = "5432";
    static final String DB_NAME = "glinicore";
    static final String DB_USER = "postgres";
    static final String DB_PASSWORD = "123456";

    private static SessionFactory sessionFactory;

    private ConexaoHibernate() {
    }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            try {
                Configuration configuration = new Configuration().configure("hibernate.cfg.xml");
                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                configuration.setProperty("hibernate.connection.url", getJdbcUrl());
                configuration.setProperty("hibernate.connection.username", DB_USER);
                configuration.setProperty("hibernate.connection.password", DB_PASSWORD);
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                sessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                throw new IllegalStateException(mensagemErroConexao(ex), ex);
            }
        }
        return sessionFactory;
    }

    public static Session getSession() {
        return getSessionFactory().openSession();
    }

    public static synchronized void fechar() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    static String getJdbcUrl() {
        return "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
    }

    private static String mensagemErroConexao(Throwable ex) {
        Throwable causa = causaRaiz(ex);
        String detalhe = causa.getMessage() == null ? ex.toString() : causa.getMessage();
        String detalheLower = detalhe.toLowerCase();

        if (detalheLower.contains("connection")
                || detalheLower.contains("conex")
                || detalheLower.contains("localhost:5432")
                || detalheLower.contains("password authentication failed")
                || detalheLower.contains("database \"glinicore\" does not exist")
                || detalheLower.contains("org.postgresql")) {
            return "Nao foi possivel conectar ao PostgreSQL. "
                    + "Verifique se o servico do PostgreSQL esta rodando e se o banco glinicore existe.";
        }

        return "Falha ao iniciar o Hibernate: " + detalhe;
    }

    private static Throwable causaRaiz(Throwable ex) {
        Throwable causa = ex;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa;
    }
}
