package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class    BishopMovesCalculator extends PieceMovesCalculator {

    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> bishopMoves = new ArrayList<>();

        // Bottom left
        addDiagonalMoves(board, myPosition, -1, -1, bishopMoves);
        // Top left
        addDiagonalMoves(board, myPosition, -1, 1, bishopMoves);
        // Bottom right
        addDiagonalMoves(board, myPosition, 1, -1, bishopMoves);
        // Top right
        addDiagonalMoves(board, myPosition, 1, 1, bishopMoves);

        return bishopMoves;
    }

    private static void addDiagonalMoves(ChessBoard board, ChessPosition myPosition, int rowIncrement, int colIncrement, Collection<ChessMove> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        int newRow = row + rowIncrement;
        int newCol = col + colIncrement;

        while (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPosition newPosition = new ChessPosition(newRow, newCol);
            if (board.getPiece(newPosition) != null) {
                if (pieceColor != board.getPiece(newPosition).getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                break; // Stop if there's a piece in the way
            } else {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            newRow += rowIncrement;
            newCol += colIncrement;
        }
    }
}
