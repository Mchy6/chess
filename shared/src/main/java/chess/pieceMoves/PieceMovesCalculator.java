package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PieceMovesCalculator {
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) == null) {
            return new ArrayList<>();
        } else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.BISHOP) {
            return BishopMovesCalculator.pieceMoves(board, myPosition);
        }
        /*else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KING) {
            return KingMovesCalculator.pieceMoves(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return KnightMovesCalculator.pieceMoves(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
            return PawnMovesCalculator.pieceMoves(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.QUEEN) {
            return QueenMovesCalculator.pieceMoves(board, myPosition);
        }
        else if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.ROOK) {
            return RookMovesCalculator.pieceMoves(board, myPosition);
        }
        */
        return new ArrayList<>(); // should return list of valid moves
    }

//    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition);
}

