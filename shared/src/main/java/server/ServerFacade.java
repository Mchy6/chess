package server;

import com.google.gson.Gson;
import exception.ResponseException;
import response.*;
import request.*;



import java.io.*;
import java.net.*;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;
//    private final DataAccess dataAccess;


    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResponse register(String username, String password, String email) throws ResponseException {
        var path = "/user";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        try {
            return this.makeRequest("POST", path, registerRequest, RegisterResponse.class, null);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public LoginResponse login(String username, String password) throws ResponseException {
        var path = "/session";
        LoginRequest loginRequest = new LoginRequest(username, password);
        try {
            return this.makeRequest("POST", path, loginRequest, LoginResponse.class, null);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        try {
            this.makeRequest("DELETE", path, logoutRequest, null, authToken);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public CreateGameResponse createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
//        dataAccess.createAuthToken(authData);
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        try {
            return this.makeRequest("POST", path, createGameRequest, CreateGameResponse.class, authToken);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public ListGamesResponse listGames(String authToken) throws ResponseException {
        var path = "/game";
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        try {
            return this.makeRequest("GET", path, listGamesRequest, ListGamesResponse.class, authToken);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void joinGame(String playerColor, int gameID, String authToken) throws ResponseException {
        var path = "/game"; //  + "/" + gameID;
        JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
        try {
            this.makeRequest("PUT", path, joinGameRequest, null, authToken);
        } catch (ResponseException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.addRequestProperty("Authorization", authToken);
            if (!Objects.equals(method, "GET")) {
                http.setDoOutput(true);
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}