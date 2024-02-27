package dataAccess;

import exception.ResponseException;

import java.util.Collection;

public interface DataAccess {
    void clearDB() throws ResponseException;
}
