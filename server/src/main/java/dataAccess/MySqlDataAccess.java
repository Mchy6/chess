package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.sql.*;

import static dataAccess.DatabaseManager.createDatabase;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException, DataAccessException {
        createDatabase();

    }

    @Override
    public void clearDB() throws DataAccessException {
        executeUpdate("TRUNCATE TABLE userData");
        executeUpdate("TRUNCATE TABLE authData");
        executeUpdate("TRUNCATE TABLE gameData");
    }

    @Override
    public UserData getUser(UserData userData) throws DataAccessException {
        String sql = "SELECT username, password, email FROM userData WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userData.username());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming UserData constructor or a method to instantiate it from these fields
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to read userData: " + e.getMessage());
        }
        return null;
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        String sql = "INSERT INTO userData (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sql, userData.username(), userData.password(), userData.email());
        return userData;
    }

    @Override
    public AuthData createAuthToken(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        executeUpdate(sql, authData.authToken(), authData.username());
        return authData;
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM authData WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming AuthData constructor or a method to instantiate it from these fields
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Error: Unable to read authData: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAuthToken(AuthData authData) throws DataAccessException {
        String sql = "DELETE FROM authData WHERE authToken = ?";
        executeUpdate(sql, authData.authToken());
    }

    @Override
    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        Collection<GameData> games = new ArrayList<>();
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Assuming GameData can be instantiated from these fields, possibly needing JSON conversion
                ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                int newGameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");

                games.add(new GameData(newGameID, whiteUsername, blackUsername, gameName, chessGame));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to read gameData: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return games;

    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        String sql = "INSERT INTO gameData (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        var json = new Gson().toJson(gameData); // Assuming gameData has a method to convert itself to JSON
        executeUpdate(sql, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM gameData WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Assuming GameData can be instantiated from these fields, possibly needing JSON conversion
                    ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);

                    int newGameID = rs.getInt("gameID");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");

                    return new GameData(newGameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to read gameData: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        String sql = "UPDATE gameData SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";
        var json = new Gson().toJson(gameData); // Assuming gameData has a method to convert itself to JSON
        executeUpdate(sql, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), json, gameData.gameID());
    }


    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

}
