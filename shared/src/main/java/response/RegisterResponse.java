package response;

public class RegisterResponse {
    private String username;
    private String authToken;
    private String message;

    public RegisterResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public RegisterResponse(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getMessage() {
        return message;
    }
}
