package serviceTests;

import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
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
    void listGamesSuccess() throws ResponseException, UnauthorizedException {
        ListGamesRequest request = new ListGamesRequest("validAuthToken");
        ListGamesResponse response = service.listGames(request);
        assertNotNull(response.getGames());
    }

    @Test
    void listGamesFailure() {
        ListGamesRequest request = new ListGamesRequest("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.listGames(request));
    }
}
