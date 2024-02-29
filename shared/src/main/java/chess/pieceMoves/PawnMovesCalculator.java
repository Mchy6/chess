package chess.pieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class PawnMovesCalculator extends PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int row = myPosition.getRow(), col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();
        int direction = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int startRow = pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7;
        int promoteRow = pieceColor == ChessGame.TeamColor.WHITE ? 7 : 2;
        ChessPosition[] steps = {new ChessPosition(row + direction, col), new ChessPosition(row + 2 * direction, col)};
        ChessPosition[] attacks = {new ChessPosition(row + direction, col - 1), new ChessPosition(row + direction, col + 1)};

        // Handle forward moves
        if (row == startRow && board.getPiece(steps[1]) == null && board.getPiece(steps[0]) == null) {
            pawnMoves.add(new ChessMove(myPosition, steps[1], null));
        }
        if (board.getPiece(steps[0]) == null) {
            addMove(pawnMoves, myPosition, steps[0], row == promoteRow);
        }

        // Handle attacks
        Stream.of(attacks).forEach(pos -> {
            if (isValid(pos) && board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != pieceColor) {
                addMove(pawnMoves, myPosition, pos, row == promoteRow);
            }
        });

        return pawnMoves;
    }

    private static void addMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, boolean promote) {
        if (promote) {
            moves.addAll(Arrays.asList(
                    new ChessMove(start, end, ChessPiece.PieceType.QUEEN),
                    new ChessMove(start, end, ChessPiece.PieceType.ROOK),
                    new ChessMove(start, end, ChessPiece.PieceType.BISHOP),
                    new ChessMove(start, end, ChessPiece.PieceType.KNIGHT)));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }

    private static boolean isValid(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }
}
