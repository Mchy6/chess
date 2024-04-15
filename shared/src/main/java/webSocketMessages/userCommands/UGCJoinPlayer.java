package webSocketMessages.userCommands;

import chess.ChessGame;

public class UGCJoinPlayer extends UserGameCommand {
    public UGCJoinPlayer(ChessGame.TeamColor playerColor, String authToken, int gameID) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
}