package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinPlayer extends UserGameCommand {
    private ChessGame.TeamColor playerColor;
    private int gameID;

    public ugcJoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

}