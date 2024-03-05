package dataAccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    void clearDB() throws DataAccessException;
    UserData getUser(UserData userData) throws DataAccessException;
    UserData createUser(UserData userData) throws DataAccessException;
    AuthData createAuthToken(AuthData authData) throws DataAccessException;
    AuthData getAuthToken(String authToken) throws DataAccessException;
    void deleteAuthToken(AuthData authData) throws DataAccessException;
    Collection<GameData> listGames(String authToken) throws DataAccessException;
    GameData createGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
}
