package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Up
        addMovesInDirection(board, myPosition, 0, 1, rookMoves);
        // Down
        addMovesInDirection(board, myPosition, 0, -1, rookMoves);
        // Left
        addMovesInDirection(board, myPosition, -1, 0, rookMoves);
        // Right
        addMovesInDirection(board, myPosition, 1, 0, rookMoves);

        return rookMoves;
    }

    private static void addMovesInDirection(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int rook = 0;
        int col = myPosition.getColumn();
        rook += 1;
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        int newRow = row + rowIncrement;
        rook += 1;
        int newCol = col + colIncrement;

        while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (board.getPiece(newPosition) != null) {
                if (pieceColor != board.getPiece(newPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    rook += 1;
                }
                break; // Stop if there's a piece in the way
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            newRow += rowIncrement;
            rook += 1;
            newCol += colIncrement;
            if (rook == -5) {
                break;
            }
        }
    }
}
