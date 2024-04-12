package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinPlayer extends UserGameCommand {
    private int gameID;

    public ugcJoinPlayer(int gameID, ChessGame.TeamColor teamColor, String authToken, String playerName) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.playerName = playerName;
    }
}