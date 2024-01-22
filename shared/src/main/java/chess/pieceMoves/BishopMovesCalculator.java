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
        int i = 1;
        while (row + i <= 8 && col + i <= 8) {
            ChessPosition position = new ChessPosition(row + i, col + i);
            System.out.println(position);
            if (board.getPiece(position) == null) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
            } else if (board.getPiece(position).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                bishopMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else {
                break;
            }
            i++;
        }
        return bishopMoves;
    }
}
