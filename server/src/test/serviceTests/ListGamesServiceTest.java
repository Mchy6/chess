package serviceTests;

import dataAccess.MemoryDataAccess;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import request.RegisterRequest;
import response.RegisterResponse;
import server.service.Service;
import request.ListGamesRequest;
import response.ListGamesResponse;
import exception.ResponseException;
import exception.UnauthorizedException;

class ListGamesServiceTest {
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
