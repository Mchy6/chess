package webSocketMessages.userCommands;

public class UGCLeave extends UserGameCommand {

    public UGCLeave(String authToken, String username) {
        this.setAuthToken(authToken);
        this.username = username;
    }
}