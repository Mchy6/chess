package server.service;

import chess.ChessGame;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.UnauthorizedException;
import model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import request.*;
import response.*;
import response.LoginResponse;
import response.RegisterResponse;
import java.util.Random;
import java.util.Objects;
import java.util.UUID;

public class Service {

    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
    public void clearDB() throws DataAccessException {
        this.dataAccess.clearDB();
    }

    public RegisterResponse register(RegisterRequest registerRequest) throws DataAccessException, AlreadyTakenException, BadRequestException {
        if (registerRequest.getUsername() == null || registerRequest.getPassword() == null || registerRequest.getEmail() == null) {
            throw new BadRequestException("Error: bad request");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(registerRequest.getPassword());

        UserData userData = new UserData(registerRequest.getUsername(), hashedPassword, registerRequest.getEmail());
        if (dataAccess.getUser(userData) != null) {
            throw new AlreadyTakenException("Error: already taken");
        } else {
            dataAccess.createUser(userData);
            AuthData authData = new AuthData(registerRequest.getUsername(), UUID.randomUUID().toString());

            dataAccess.createAuthToken(authData);
            return new RegisterResponse(registerRequest.getUsername(), authData.authToken());

        }
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException, UnauthorizedException { // where does unauthorized exception go?
        UserData userData = new UserData(loginRequest.getUsername(), loginRequest.getPassword(), null);
        if (dataAccess.getUser(userData) != null) {
            // verify password

            var password = loginRequest.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

//            if (dataAccess.getUser(userData).password().equals(loginRequest.getPassword())) {
            if (encoder.matches(password, dataAccess.getUser(userData).password())) {
                AuthData authData = new AuthData(loginRequest.getUsername(), UUID.randomUUID().toString());
                dataAccess.createAuthToken(authData);
                return new LoginResponse(loginRequest.getUsername(), authData.authToken());
            } else {
                throw new UnauthorizedException("Error: unauthorized");
            }

        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException, UnauthorizedException {
        if (dataAccess.getAuthToken(logoutRequest.getAuthToken()) != null) {
            dataAccess.deleteAuthToken(dataAccess.getAuthToken(logoutRequest.getAuthToken()));
        } else {
            throw new UnauthorizedException("Error: unauthorized"); // does this go here?
        }
    }

    public ListGamesResponse listGames(ListGamesRequest listGamesRequest) throws UnauthorizedException, DataAccessException {
        if (dataAccess.getAuthToken(listGamesRequest.getAuthToken()) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new ListGamesResponse(dataAccess.listGames(listGamesRequest.getAuthToken()));
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest) throws UnauthorizedException, DataAccessException, BadRequestException {
        if (dataAccess.getAuthToken(createGameRequest.getAuthToken()) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (createGameRequest.getGameName() == null) {
            throw new BadRequestException("Error: bad request");
        }
        int id = new Random().nextInt(1000000);
        return new CreateGameResponse(dataAccess.createGame(new GameData(id, null, null, createGameRequest.getGameName(), new ChessGame())).gameID());
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws UnauthorizedException, DataAccessException, BadRequestException, AlreadyTakenException {
        AuthData authData = dataAccess.getAuthToken(joinGameRequest.getAuthToken());
        GameData gameData = dataAccess.getGame(joinGameRequest.getGameID());

        if (authData == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        if (gameData == null
                || (!"white".equalsIgnoreCase(joinGameRequest.getPlayerColor())
                && !"black".equalsIgnoreCase(joinGameRequest.getPlayerColor()))) {
            System.out.println(joinGameRequest.getPlayerColor());
            throw new BadRequestException("Error: bad request");
        }

        if (Objects.equals(joinGameRequest.getPlayerColor(), "WHITE") && gameData.whiteUsername() != null
                || Objects.equals(joinGameRequest.getPlayerColor(), "BLACK") && gameData.blackUsername() != null) {

            throw new AlreadyTakenException("Error: already taken");
        }

        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();
        String color = joinGameRequest.getPlayerColor();
        GameData updatedGameData;
        if (color == null) {
//            System.out.println("DEBUG: null?");
            updatedGameData = new GameData(joinGameRequest.getGameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        } else if (color.equalsIgnoreCase("WHITE")) {

            updatedGameData = new GameData(joinGameRequest.getGameID(), authData.username(), blackUsername, gameData.gameName(), gameData.game());
//            System.out.println("DEBUG: just put white player " + authData.username() + " in game");
        } else {
            updatedGameData = new GameData(joinGameRequest.getGameID(), whiteUsername, authData.username(), gameData.gameName(), gameData.game());
        }
        dataAccess.updateGame(updatedGameData);
        // how do I update when a move is made?
        // Should it take in gameID or gameData?
        // Where do I get black/white player names?
    }
}
