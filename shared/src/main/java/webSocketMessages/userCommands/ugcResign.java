package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcResign extends UserGameCommand {
    public ugcResign(String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.RESIGN;
        this.gameID = gameID;
    }
}