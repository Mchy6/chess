package webSocketMessages.userCommands;

public class UGCResign extends UserGameCommand {
    public UGCResign(String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}