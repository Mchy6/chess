package dataAccess;

import exception.ResponseException;
import model.*;

import java.util.Collection;

public interface DataAccess {
    void clearDB() throws ResponseException;
    UserData getUser(UserData userData) throws ResponseException;
    UserData createUser(UserData userData) throws ResponseException;
    AuthData createAuthToken(AuthData authData) throws ResponseException;
    AuthData getAuthToken(String authToken) throws ResponseException;
    void deleteAuthToken(AuthData authData) throws ResponseException;
    Collection<GameData> listGames(String authToken) throws ResponseException;
    GameData createGame(GameData gameData) throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    void updateGame(GameData gameData) throws ResponseException;
}
