package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinPlayer extends UserGameCommand {
    private ChessGame.TeamColor playerColor;
    private int gameID;
    private String playerName;

    public ugcJoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken, String playerName) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}