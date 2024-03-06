package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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



}