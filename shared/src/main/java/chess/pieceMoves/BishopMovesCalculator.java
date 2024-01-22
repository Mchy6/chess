package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

//    @Override
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();
        // Bottom left
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
            ChessPosition position = new ChessPosition(i, j);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Top left
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
            ChessPosition position = new ChessPosition(i, j);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Bottom right
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
            ChessPosition position = new ChessPosition(i, j);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Top right
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
            ChessPosition position = new ChessPosition(i, j);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }
        return bishopMoves;
    }
}
