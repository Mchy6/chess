package dataAccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {
    void clearDB() throws ResponseException;
    UserData getUser(UserData userData) throws ResponseException;
    UserData createUser(UserData userData) throws ResponseException;
    AuthData createAuthToken(AuthData authData) throws ResponseException;
}
