package response;

public class LoginResponse {
    private String username;
    private String authToken;
    private String message;

    public LoginResponse(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public LoginResponse(String message) {
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
