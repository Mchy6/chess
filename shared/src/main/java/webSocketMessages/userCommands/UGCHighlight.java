package webSocketMessages.userCommands;

import chess.ChessPosition;

public class UGCHighlight extends UserGameCommand {

    public UGCHighlight(String authToken, int gameID, ChessPosition position) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.position = position;
    }
}