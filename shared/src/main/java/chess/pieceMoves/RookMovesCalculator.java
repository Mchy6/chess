package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {

    //    @Override
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> rookMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        // Up
        for (int i = col + 1; i <= 8; i++) {
            ChessPosition position = new ChessPosition(row, i);
//            System.out.println("position: " + position);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                rookMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                rookMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Down
        for (int i = col - 1; i >= 1; i--) {
            ChessPosition position = new ChessPosition(row, i);

            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                rookMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                rookMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Left
        for (int i = row - 1; i >= 1; i--) {
            ChessPosition position = new ChessPosition(i, col);
            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                rookMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                rookMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        // Right
        for (int i = row + 1; i <= 8; i++) {
            ChessPosition position = new ChessPosition(i, col);

            if (board.getPiece(position) != null && pieceColor != board.getPiece(position).getTeamColor()) {
                rookMoves.add(new ChessMove(myPosition, position, null));
                break;
            } else if (board.getPiece(position) == null) {
                rookMoves.add(new ChessMove(myPosition, position, null));
            } else {
                break;
            }
        }

        return rookMoves;
    }
}
