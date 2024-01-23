package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> kingMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row, col + 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col - 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row, col - 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col - 1), null));
        kingMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));

        Collection<ChessMove> temp = new ArrayList<>();
        for (ChessMove move : kingMoves) {
            int moveRow = move.getEndPosition().getRow();
            int moveCol = move.getEndPosition().getColumn();
            if (moveRow > 8 || moveRow < 1 || moveCol > 8 || moveCol < 1 || (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).getTeamColor() == pieceColor)) {
            } else {
                temp.add(move);
            }
        }
        kingMoves = temp;
        return kingMoves;
    }
}
