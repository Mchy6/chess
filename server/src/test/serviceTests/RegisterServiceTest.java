package serviceTests;
import dataAccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import server.service.Service;
import request.RegisterRequest;
import response.RegisterResponse;
import dataAccess.DataAccess;
import exception.ResponseException;
import exception.AlreadyTakenException;
import exception.BadRequestException;

class RegisterServiceTest {
    static Service service;

    @BeforeEach
    void setup() {
        service = new Service(new MemoryDataAccess());
    }

    @Test
    void registerSuccess() throws ResponseException, AlreadyTakenException, BadRequestException {
        RegisterRequest request = new RegisterRequest("username", "password", "email@example.com");
        RegisterResponse response = service.register(request);
        assertNotNull(response.getAuthToken());
    }

    @Test
    void registerFailure() {
        RegisterRequest request = new RegisterRequest(null, "password", "email@example.com");
        assertThrows(BadRequestException.class, () -> service.register(request));
    }
}
