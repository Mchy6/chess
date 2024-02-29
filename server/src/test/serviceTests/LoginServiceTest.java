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
import request.LoginRequest;
import response.LoginResponse;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.UnauthorizedException;

class LoginServiceTest {
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

