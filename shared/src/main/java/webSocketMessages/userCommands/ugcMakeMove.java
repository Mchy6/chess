package webSocketMessages.userCommands;

import chess.ChessGame;
import chess.ChessMove;

public class ugcMakeMove extends UserGameCommand {

    public ugcMakeMove(int gameID, ChessGame.TeamColor teamColor, String authToken, String username, ChessMove move) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = teamColor;
        this.username = username;
        this.move = move;
    }
}