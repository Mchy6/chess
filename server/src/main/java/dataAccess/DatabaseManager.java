package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }

             conn.setCatalog("chess");

            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS userData (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

            var createAuthTable = """
            CREATE TABLE  IF NOT EXISTS authData (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )""";

            var createGameTable = """
            CREATE TABLE  IF NOT EXISTS gameData (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255) NOT NULL,
                blackUsername VARCHAR(255) NOT NULL,
                gameName VARCHAR(255) NOT NULL,
                game JSON NOT NULL,
                PRIMARY KEY (gameID)
            )""";

//            try (var conn = DatabaseManager.getConnection()) {
//                for (var statement : createStatements) {
//                    try (var preparedStatement = conn.prepareStatement(statement)) {
//                        preparedStatement.executeUpdate(); // modify to call each dao
//                    }
//                }
//            } catch (SQLException ex) {
//                throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
//            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}