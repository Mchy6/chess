package server.service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import requests.RegisterRequest;
import requests.RegisterResponse;

import java.util.UUID;

public class Service {

    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void clearDB() throws DataAccessException {
        try {
            this.dataAccess.clearDB();
        } catch (Exception e) {
            System.out.println("clearDB failed");
        }
        // for register, must consider other exceptions
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        try {
            UserData userData = new UserData(registerRequest.getUsername(), registerRequest.getPassword(), registerRequest.getEmail());
            if (dataAccess.getUser(userData) != null) {
                throw new AlreadyTakenException("Username already taken");
            } else {
                dataAccess.createUser(userData);
                AuthData authData = new AuthData(registerRequest.getUsername(), UUID.randomUUID().toString());

                dataAccess.createAuthToken(authData);
            }


    }

}
