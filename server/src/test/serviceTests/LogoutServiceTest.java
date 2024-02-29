package serviceTests;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import server.service.Service;
import request.LogoutRequest;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.UnauthorizedException;

class LogoutServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void logoutSuccess() throws ResponseException, UnauthorizedException {
        LogoutRequest request = new LogoutRequest("validAuthToken");
        assertDoesNotThrow(() -> service.logout(request));
    }

    @Test
    void logoutFailure() {
        LogoutRequest request = new LogoutRequest("invalidAuthToken");
        assertThrows(UnauthorizedException.class, () -> service.logout(request));
    }
}

