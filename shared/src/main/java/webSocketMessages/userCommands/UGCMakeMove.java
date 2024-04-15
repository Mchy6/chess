package webSocketMessages.userCommands;

import chess.ChessMove;

public class UGCMakeMove extends UserGameCommand {

    public UGCMakeMove(String authToken, int gameID, ChessMove move) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }
}