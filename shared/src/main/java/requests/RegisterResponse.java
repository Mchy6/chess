package requests;

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

}
