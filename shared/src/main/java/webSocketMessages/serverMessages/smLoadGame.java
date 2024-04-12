package webSocketMessages.serverMessages;

import chess.ChessGame;

public class smLoadGame extends ServerMessage {
    private final ChessGame game;

    public smLoadGame(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}