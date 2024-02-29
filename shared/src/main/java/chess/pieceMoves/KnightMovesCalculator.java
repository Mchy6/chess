package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class KnightMovesCalculator extends PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        return Arrays.asList(
                        new ChessPosition(row + 2, col + 1),
                        new ChessPosition(row + 1, col + 2),
                        new ChessPosition(row - 2, col + 1),
                        new ChessPosition(row - 1, col + 2),
                        new ChessPosition(row - 2, col - 1),
                        new ChessPosition(row - 1, col - 2),
                        new ChessPosition(row + 2, col - 1),
                        new ChessPosition(row + 1, col - 2)
                ).stream()
                .filter(pos -> pos.getRow() <= 8 && pos.getRow() >= 1 && pos.getColumn() <= 8 && pos.getColumn() >= 1)
                .filter(pos -> board.getPiece(pos) == null || board.getPiece(pos).getTeamColor() != pieceColor)
                .map(pos -> new ChessMove(myPosition, pos, null))
                .collect(Collectors.toList());
    }
}
