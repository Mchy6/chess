package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> knightMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col + 1), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col + 2), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col + 1), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col + 2), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row - 2, col - 1), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row - 1, col - 2), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row + 2, col - 1), null));
        knightMoves.add(new ChessMove(myPosition, new ChessPosition(row + 1, col - 2), null));

        Collection<ChessMove> temp = new ArrayList<>();
        for (ChessMove move : knightMoves) {
            int moveRow = move.getEndPosition().getRow();
            int moveCol = move.getEndPosition().getColumn();
            if (moveRow > 8 || moveRow < 1 || moveCol > 8 || moveCol < 1 || (board.getPiece(move.getEndPosition()) != null && board.getPiece(move.getEndPosition()).getTeamColor() == pieceColor)) {
            } else {
                temp.add(move);
            }
        }
        knightMoves = temp;
        return knightMoves;
    }
}
