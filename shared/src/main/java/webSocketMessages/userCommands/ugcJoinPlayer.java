package webSocketMessages.userCommands;

import chess.ChessGame;

public class ugcJoinPlayer extends UserGameCommand {

    public ugcJoinPlayer(int gameID, ChessGame.TeamColor teamColor, String authToken, String username) {
        this.setAuthToken(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
        this.gameID = gameID;
        this.teamColor = teamColor;
        this.username = username;
    }
}