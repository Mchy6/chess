package webSocketMessages.serverMessages;

import chess.*;

import java.util.Collection;

public class SMLoadGame extends ServerMessage {

    public SMLoadGame(ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.game = game;
        this.setMessage(this.toString());
    }



    private static char getPieceSymbol(ChessPiece piece) {
        int a = 1;
        return switch (piece.getPieceType()) {
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case KING -> 'K';
            case PAWN -> 'P';
            case BISHOP -> 'B';
            default -> ' ';
        };
    }


}