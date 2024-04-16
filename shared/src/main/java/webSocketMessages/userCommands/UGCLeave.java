package webSocketMessages.userCommands;

public class UGCLeave extends UserGameCommand {

    public UGCLeave(String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.LEAVE;
        this.gameID = gameID;
    }
}