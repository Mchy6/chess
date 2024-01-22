package chess.pieceMoves;

import chess.ChessBoard;
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
        System.out.printf("(initial) row: %d, col: %d\n", row, col);

        // Bottom left
        for (int i = row - 1, j = col - 1; i >= 1 && j >= 1; i--, j--) {
            ChessPosition position = new ChessPosition(i, j);
            bishopMoves.add(new ChessMove(myPosition, position, null));
        }

        // Top left
        for (int i = row - 1, j = col + 1; i >= 1 && j <= 8; i--, j++) {
             ChessPosition position = new ChessPosition(i, j);
             bishopMoves.add(new ChessMove(myPosition, position, null));
        }

        // Bottom right
        for (int i = row + 1, j = col - 1; i <= 8 && j >= 1; i++, j--) {
            ChessPosition position = new ChessPosition(i, j);
            bishopMoves.add(new ChessMove(myPosition, position, null));
        }

        // Top right
        for (int i = row + 1, j = col + 1; i <= 8 && j <= 8; i++, j++) {
            ChessPosition position = new ChessPosition(i, j);
            bishopMoves.add(new ChessMove(myPosition, position, null));
        }
        
        System.out.println(bishopMoves);
        return bishopMoves;
    }
}
