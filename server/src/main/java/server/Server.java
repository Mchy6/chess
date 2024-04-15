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

        configureEndpoints(gson);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void configureEndpoints(Gson gson) {
        configureClearDBEndpoint(gson);
        configureRegisterEndpoint(gson);
        configureLoginEndpoint(gson);
        configureLogoutEndpoint(gson);
        configureListGamesEndpoint(gson);
        configureCreateGameEndpoint(gson);
        configureJoinGameEndpoint(gson);
    }

    private void configureClearDBEndpoint(Gson gson) {
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
    }

    private void configureRegisterEndpoint(Gson gson) {
        Spark.post("/user", (req, res) -> {
            RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

            try {
                RegisterResponse registerResponse = service.register(registerRequest);
                return gson.toJson(registerResponse);
            } catch (BadRequestException e) {
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
    }

    private void configureLoginEndpoint(Gson gson) {
        Spark.post("/session", (req, res) -> {
            LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

            try {
                LoginResponse loginResponse = service.login(loginRequest);
                return gson.toJson(loginResponse);
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
    }

    private void configureLogoutEndpoint(Gson gson) {
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
    }

    private void configureListGamesEndpoint(Gson gson) {
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
    }

    private void configureCreateGameEndpoint(Gson gson) {
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
    }

    private void configureJoinGameEndpoint(Gson gson) {
        Spark.put("/game", (req, res) -> {
            JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);
            joinGameRequest.setAuthToken(req.headers("Authorization"));
            try {
                service.joinGame(joinGameRequest);
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
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
