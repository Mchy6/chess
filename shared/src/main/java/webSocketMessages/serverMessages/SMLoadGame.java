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
        return switch (piece.getPieceType()) {
            case ROOK -> 'R';
            case KNIGHT -> 'N';
            case BISHOP -> 'B';
            case QUEEN -> 'Q';
            case KING -> 'K';
            case PAWN -> 'P';
            default -> ' ';
        };
    }


}