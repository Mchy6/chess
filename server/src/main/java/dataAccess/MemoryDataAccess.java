package dataAccess;

import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override // <- may be unnecessary
    public void clearDB() throws DataAccessException {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    @Override
    public UserData getUser(UserData userData) throws DataAccessException {
        return users.get(userData.username());
    }

    @Override
    public UserData createUser(UserData userData) throws DataAccessException {
        users.put(userData.username(), userData);
        return userData;
    }

    @Override
    public AuthData createAuthToken(AuthData authData) throws DataAccessException {
        authTokens.put(authData.authToken(), authData);
        return authData;
    }

    @Override
    public AuthData getAuthToken(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuthToken(AuthData authData) throws DataAccessException {
        authTokens.remove(authData.authToken());
    }

    @Override
    public Collection<GameData> listGames(String authToken) throws DataAccessException {
        return games.values();
    }

    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        games.put(gameData.gameID(), gameData);
    }

}