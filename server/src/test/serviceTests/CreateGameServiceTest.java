package serviceTests;

import dataAccess.MemoryDataAccess;
import exception.AlreadyTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import request.RegisterRequest;
import server.service.Service;
import request.CreateGameRequest;
import response.CreateGameResponse;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.UnauthorizedException;
import exception.BadRequestException;

class CreateGameServiceTest {
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
