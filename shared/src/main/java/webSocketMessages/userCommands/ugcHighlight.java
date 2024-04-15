package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

public class ugcHighlight extends UserGameCommand {

    public ugcHighlight(String authToken, int gameID, ChessPosition position) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.position = position;
    }
}