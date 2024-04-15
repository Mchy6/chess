package webSocketMessages.userCommands;

public class UGCJoinObserver extends UserGameCommand {
    public UGCJoinObserver(String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}