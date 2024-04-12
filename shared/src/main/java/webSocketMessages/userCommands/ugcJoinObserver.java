package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinObserver extends UserGameCommand {
    private int gameID;

    public ugcJoinObserver(int gameID, ChessGame.TeamColor teamColor, String authToken, String observerName) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
        this.username = observerName;
    }
}