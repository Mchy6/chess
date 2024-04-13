package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class ugcLeave extends UserGameCommand {

    public ugcLeave(String authToken, String username) {
        this.setAuthToken(authToken);
        this.username = username;
    }
}