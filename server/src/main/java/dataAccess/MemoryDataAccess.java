package dataAccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;
import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
//    private int nextId = 1;
//    final private HashMap<String (will be username), user> users = new HashMap<>();
//    final private HashMap<Integer, game> games = new HashMap<>();
//    final private HashMap<String (will be authToken string), authToken> authTokens = new HashMap<>();
    final private HashMap<String, UserData> users = new HashMap<>();
    final private HashMap<Integer, GameData> games = new HashMap<>();
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override // <- may be unnecessary
    public void clearDB() throws ResponseException {
        users.clear();
        games.clear();
        authTokens.clear();
    }

    @Override
    public UserData getUser(UserData userData) throws ResponseException {
        return users.get(userData.getUsername());
    }

    @Override
    public UserData createUser(UserData userData) throws ResponseException {
        users.put(userData.getUsername(), userData);
        return userData;
    }

    @Override
    public AuthData createAuthToken(AuthData authData) throws ResponseException {
        authTokens.put(authData.getAuthToken(), authData);
        return authData;
    }
}