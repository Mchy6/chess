package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinPlayer extends UserGameCommand {
//    public ugcJoinPlayer(String authToken, CommandType commandType, int gameID, ChessGame.TeamColor playerColor) {
//        this.setAuthToken(authToken);
//        this.commandType = commandType;
//        this.gameID = gameID;
//        this.playerColor = playerColor;
//        this.username = username;
//    }
    public ugcJoinPlayer(int gameID, ChessGame.TeamColor playerColor, String authToken) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.playerColor = playerColor;
    }
}