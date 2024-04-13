package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinObserver extends UserGameCommand {
    public ugcJoinObserver(int gameID, String authToken) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }
}