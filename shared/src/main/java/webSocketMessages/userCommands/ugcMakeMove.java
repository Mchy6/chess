package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class ugcMakeMove extends UserGameCommand {

    public ugcMakeMove(String authToken, int gameID, ChessMove move) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.move = move;
    }
}