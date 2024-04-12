package webSocketMessages.userCommands;

public class UserGameCommand {

    public enum CommandType {
        JOIN_PLAYER,
        JOIN_OBSERVER,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    protected CommandType commandType;

    private String authToken;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    public CommandType getType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }
}
