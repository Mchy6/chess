package java.dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import exception.ResponseException;
import exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.RegisterRequest;
import response.CreateGameResponse;
import server.service.Service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ServiceTests {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void joinGameSuccess() throws ResponseException, UnauthorizedException, BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest rRequest = new RegisterRequest("username", "password", "email@example.com");
        String authToken = service.register(rRequest).getAuthToken();

        CreateGameRequest cRequest = new CreateGameRequest("GameName");
        cRequest.setAuthToken(authToken);
        CreateGameResponse response = service.createGame(cRequest);


        JoinGameRequest request = new JoinGameRequest("WHITE", response.getGameID());
        request.setAuthToken(authToken);
        assertDoesNotThrow(() -> service.joinGame(request));
    }
}