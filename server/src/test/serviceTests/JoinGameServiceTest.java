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

class JoinGameServiceTest {
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

