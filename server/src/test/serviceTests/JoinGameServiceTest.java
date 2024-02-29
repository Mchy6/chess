package serviceTests;

import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        request.setAuthToken("validAuthToken");
        assertDoesNotThrow(() -> service.joinGame(request));
    }

    @Test
    void joinGameFailureUnauthorized() {
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);
        request.setAuthToken("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.joinGame(request));
    }

    @Test
    void joinGameFailureBadRequest() {
        JoinGameRequest request = new JoinGameRequest("INVALID_COLOR", 1);
        request.setAuthToken("validAuthToken");
        assertThrows(BadRequestException.class, () -> service.joinGame(request));
    }
}

