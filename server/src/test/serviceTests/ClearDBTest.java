package serviceTests;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import request.LoginRequest;
import response.LoginResponse;
import server.service.Service;
import request.RegisterRequest;
import response.RegisterResponse;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.AlreadyTakenException;
import exception.BadRequestException;

class ClearDBTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void clearDBSuccess() throws ResponseException, AlreadyTakenException, BadRequestException, DataAccessException, UnauthorizedException {
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        RegisterResponse response = service.register(request);
        service.clearDB();
        LoginRequest lRequest = new LoginRequest("username", "password");
        assertThrows(UnauthorizedException.class, () -> service.login(lRequest));

    }
}
