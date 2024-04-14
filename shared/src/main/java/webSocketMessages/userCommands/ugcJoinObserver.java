package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinObserver extends UserGameCommand {
    public ugcJoinObserver(String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}