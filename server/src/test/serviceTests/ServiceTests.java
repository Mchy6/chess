package serviceTests;

import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import request.CreateGameRequest;
import request.RegisterRequest;
import response.CreateGameResponse;
import server.service.Service;
import request.JoinGameRequest;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.UnauthorizedException;
import exception.BadRequestException;
import exception.AlreadyTakenException;
import request.*;
import response.*;

class MyJoinGameServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void joinGameSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        String authToken = service.register(rRequest).getAuthToken();

        CreateGameRequest cRequest = new CreateGameRequest("GameName");
        cRequest.setAuthToken(authToken);
        CreateGameResponse response = service.createGame(cRequest);


        JoinGameRequest request = new JoinGameRequest("WHITE", response.getGameID());
        request.setAuthToken(authToken);
        assertDoesNotThrow(() -> service.joinGame(request));
    }

    @Test
    void joinGameFailureUnauthorized() {
        JoinGameRequest request = new JoinGameRequest("BLACK", 1);
        request.setAuthToken("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.joinGame(request));
    }
}

class MyCreateGameServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void createGameSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        String authToken = service.register(rRequest).getAuthToken();

        CreateGameRequest request = new CreateGameRequest("GameName");
        request.setAuthToken(authToken);
        CreateGameResponse response = service.createGame(request);
        assertNotNull(response.getGameID());
    }

    @Test
    void createGameFailure() {
        CreateGameRequest request = new CreateGameRequest("GameName");
        request.setAuthToken("invalid");
        assertThrows(UnauthorizedException.class, () -> service.createGame(request));
    }
}

class MyLogoutServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void logoutSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        String authToken = service.register(rRequest).getAuthToken();
        LogoutRequest request = new LogoutRequest(authToken);
        assertDoesNotThrow(() -> service.logout(request));
    }

    @Test
    void logoutFailure() {
        LogoutRequest request = new LogoutRequest("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.logout(request));
    }
}

class MyListGamesServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void listGamesSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        String authToken = service.register(rRequest).getAuthToken();

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResponse response = service.listGames(request);
        assertNotNull(response.getGames());
    }

    @Test
    void listGamesFailure() {
        ListGamesRequest request = new ListGamesRequest("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.listGames(request));
    }
}

class MyLoginServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void loginSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        RegisterResponse rResponse = service.register(rRequest);
        LoginRequest lRequest = new LoginRequest("username", "password");
        LoginResponse lResponse = service.login(lRequest);
        assertNotNull(lResponse.getAuthToken());
    }

    @Test
    void loginFailure() {
        LoginRequest request = new LoginRequest("wrongUsername", "password");
        assertThrows(UnauthorizedException.class, () -> service.login(request));
    }
}
