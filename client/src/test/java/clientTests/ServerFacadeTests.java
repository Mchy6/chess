package clientTests;

import dataAccess.DataAccessException;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ResponseException;
import exception.UnauthorizedException;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() throws ResponseException, DataAccessException {
        server = new Server();
        var port = server.run(8082);
        String serverURL = "http://localhost:8082";
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(serverURL);
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void registerSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    void registerFailure() {
        try {
            facade.register("player1", "password", null);
        } catch (ResponseException e) {
            assertThrows(ResponseException.class, () -> {
                throw e;
            });
        }
    }

    @Test
    void loginSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        LoginResponse response = facade.login("player1", "password");
        assertNotNull(response.getAuthToken());
    }

    @Test
    void loginFailure() {
        assertThrows(ResponseException.class, () -> facade.login("player1", "wrongPassword"));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        LoginResponse response = facade.login("player1", "password");
        assertDoesNotThrow(() -> facade.logout(response.getAuthToken()));
    }

    @Test
    void logoutFailure() {
        assertThrows(ResponseException.class, () -> facade.logout("invalidAuthToken"));
    }

    @Test
    void createGameSuccess() throws ResponseException {
        LoginResponse response = facade.login("player1", "password");
        var gameName = "game1";
        assertDoesNotThrow(() -> facade.createGame(gameName, response.getAuthToken()));
    }

    @Test
    void createGameFailure() {
        assertThrows(ResponseException.class, () -> facade.createGame(null, "invalidAuthToken"));
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        LoginResponse response = facade.login("player1", "password");
        assertNotNull(facade.listGames(response.getAuthToken()));
    }

    @Test
    void listGamesFailure() {
        assertThrows(ResponseException.class, () -> facade.listGames("invalidAuthToken"));
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        LoginResponse response = facade.login("player1", "password");

        var gameName = "game1";
        var game = facade.createGame(gameName, response.getAuthToken());
        assertDoesNotThrow(() ->facade.joinGame("WHITE", game.getGameID(), response.getAuthToken()));
    }

    @Test
    void joinGameFailure() {
        assertThrows(ResponseException.class, () -> facade.joinGame("WHITE", 1, "invalidAuthToken"));
    }

}
