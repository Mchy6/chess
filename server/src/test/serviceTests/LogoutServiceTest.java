package serviceTests;
import dataAccess.MemoryDataAccess;
import exception.AlreadyTakenException;
import exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import request.RegisterRequest;
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

