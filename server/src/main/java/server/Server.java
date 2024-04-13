package server;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import exception.*;
import request.*;
import response.*;
import response.LoginResponse;
import response.RegisterResponse;
import server.service.Service;
import spark.*;
import exception.ResponseException;
import server.websocket.WebSocketHandler;

public class Server {
    private final Service service;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        try {
        service = new Service(new MySqlDataAccess());
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        try {
            webSocketHandler = new WebSocketHandler();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.webSocket("/connect", webSocketHandler);

        var gson = new Gson();

        // Clear application endpoint
        Spark.delete("/db", (req, res) -> {

            try {
                service.clearDB();
            } catch (DataAccessException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }

            return "{}";
        });

        // Register endpoint
        Spark.post("/user", (req, res) -> {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

            try {
                RegisterResponse registerResponse = service.register(registerRequest);
                return gson.toJson(registerResponse);
            } catch (BadRequestException e) { // throw in service, when parameter is null (or not white/black for playerColor)
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(400);
                return gson.toJson(exceptionResponse);
            } catch (AlreadyTakenException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(403);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }

        });

        // Login endpoint
        Spark.post("/session", (req, res) -> {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            try {
                LoginResponse loginResponse = service.login(loginRequest);
                return gson.toJson(loginResponse);
            } catch (UnauthorizedException e) { // return if authToken is not found
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(401);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }
        });

        // Logout endpoint
        Spark.delete("/session", (req, res) -> {
            LogoutRequest logoutRequest = new LogoutRequest(req.headers("Authorization"));
            try {
                service.logout(logoutRequest);
                return "{}";
            } catch (UnauthorizedException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(401);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }
        });

        // List Games endpoint
        Spark.get("/game", (req, res) -> {
            ListGamesRequest listGamesRequest = new ListGamesRequest(req.headers("Authorization"));
            try {
                ListGamesResponse listGamesResponse = service.listGames(listGamesRequest);
                return gson.toJson(listGamesResponse);
            } catch (UnauthorizedException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(401);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }

        });

        // Create Game endpoint
        Spark.post("/game", (req, res) -> {
            CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);
            createGameRequest.setAuthToken(req.headers("Authorization"));
            try {
                CreateGameResponse createGameResponse = service.createGame(createGameRequest);
                return gson.toJson(createGameResponse);
            } catch (BadRequestException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(400);
                return gson.toJson(exceptionResponse);
            } catch (UnauthorizedException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(401);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }
        });

        // Join Game endpoint
        Spark.put("/game", (req, res) -> {
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            joinGameRequest.setAuthToken(req.headers("Authorization"));
            try {
                service.joinGame(joinGameRequest);
//                shouldn't need to have this following line, websocketfacade should send ws message and websockethandler
//                should receive it automatically
//                webSocketHandler.joinPlayer(joinGameRequest.getPlayer); // need some way to get name
                return "{}";
            } catch (BadRequestException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(400);
                return gson.toJson(exceptionResponse);
            } catch (UnauthorizedException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(401);
                return gson.toJson(exceptionResponse);
            } catch (AlreadyTakenException e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(403);
                return gson.toJson(exceptionResponse);
            } catch (Exception e) {
                ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
                res.status(500);
                return gson.toJson(exceptionResponse);
            }
        });

        Spark.awaitInitialization();
        return Spark.port();
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
