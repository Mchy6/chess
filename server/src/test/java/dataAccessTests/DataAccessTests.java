package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTests {
    private static MySqlDataAccess dataAccess;

    @BeforeEach
    void setup() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clearDB();
    }

    @Test
    public void clearDBSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        assertDoesNotThrow(dataAccess::clearDB);
    }

    @Test
    public void getUserFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        UserData nonExistentUser = new UserData("nonExistentUser", "noPassword", "noemail@example.com");

        UserData result = dataAccess.getUser(nonExistentUser);

        assertNull(result, "getUser should return null for non-existent user");
    }

    @Test
    public void getUserSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        UserData user = new UserData("username", "password", "email@example.com");
        dataAccess.createUser(user);
        UserData returnedUser = dataAccess.getUser(user);

        assertNotNull(returnedUser);
    }

    @Test
    public void createUserSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        UserData user = new UserData("username", "password", "email@example.com");
        dataAccess.createUser(user);
        UserData returnedUser = dataAccess.getUser(user);
        assert (user.equals(returnedUser));
    }

    @Test
    public void createUserFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        UserData user = new UserData("username", "password", "email@example.com");
        UserData sameUser = new UserData("username", "password", "email@example.com");

        dataAccess.createUser(user);
        assertThrows(DataAccessException.class, () -> dataAccess.createUser(sameUser));

    }

    @Test
    public void createAuthTokenSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username", "authToken");
        assertNotNull(dataAccess.createAuthToken(authData));
    }

    @Test
    public void createAuthTokenFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username", "authToken");
        dataAccess.createAuthToken(authData);
        assertThrows(DataAccessException.class, () -> dataAccess.createAuthToken(authData));
    }

    @Test
    public void getAuthTokenSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username", "authToken");
        dataAccess.createAuthToken(authData);
        assertNotNull(dataAccess.getAuthToken(authData.authToken()));
    }

    @Test
    public void getAuthTokenFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username12341", "authToken1234123");
//        dataAccess.getAuthToken(authData.authToken());
        assertNull(dataAccess.getAuthToken(authData.authToken()));
    }

    @Test
    public void deleteAuthTokenSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username", "authToken");
        dataAccess.createAuthToken(authData);
        dataAccess.deleteAuthToken(authData);
        assertNull(dataAccess.getAuthToken(authData.authToken()));
    }

    @Test
    public void deleteAuthTokenFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        AuthData authData = new AuthData("username", "authToken");
        assertDoesNotThrow(() -> dataAccess.deleteAuthToken(authData));
    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        // Assume createGame method is working correctly and use it to insert a game
        GameData game = new GameData(1, "whiteUser", "blackUser", "GameName", new ChessGame());
        dataAccess.createGame(game);

        Collection<GameData> games = dataAccess.listGames("authToken");
        assertFalse(games.isEmpty(), "listGames should return a non-empty collection when games exist");
    }

    @Test
    public void listGamesFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        Collection<GameData> games = dataAccess.listGames("authToken");
        assertTrue(games.isEmpty());
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        GameData game = new GameData(2, "whiteUser", "blackUser", "GameName2", new ChessGame());
        GameData returnedGame = dataAccess.createGame(game);

        assertNotNull(returnedGame, "createGame should successfully create a game and return it");
    }

    @Test
    public void createGameFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        GameData game = new GameData(2, "whiteUser", "blackUser", "GameName2", new ChessGame());
        dataAccess.createGame(game);
        assertThrows(DataAccessException.class, () -> dataAccess.createGame(game));
    }

    @Test
    public void getGameSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        // Use createGame to ensure there's a game to fetch
        GameData game = new GameData(3, "whiteUsername", "blackUsername", "GameName3", new ChessGame());
        dataAccess.createGame(game);

        GameData fetchedGame = dataAccess.getGame(3);
        assertNotNull(fetchedGame, "getGame should return a game when given a valid ID");
    }

    @Test
    public void getGameFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        GameData game = dataAccess.getGame(999); // Assuming ID 999 does not exist

        assertNull(game, "getGame should return null when the game ID does not exist");
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        // First, create a game to update
        GameData game = new GameData(4, "whiteUser", "blackUser", "GameBeforeUpdate", new ChessGame());
        dataAccess.createGame(game);

        // Now, update the game
        GameData updatedGame = new GameData(4, "updatedWhiteUser", "updatedBlackUser", "GameAfterUpdate", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.updateGame(updatedGame), "updateGame should not throw an exception on success");
    }

    @Test
    public void updateGameFailure() throws DataAccessException {
        MySqlDataAccess dataAccess = new MySqlDataAccess();
        // Attempt to update a game that does not exist
        GameData game = new GameData(999, "whiteUser", "blackUser", "GameBeforeUpdate", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.updateGame(game), "updateGame should throw an exception when the game does not exist");
    }

}