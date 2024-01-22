package chess.pieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {

    //    @Override
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> pawnMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessGame.TeamColor pieceColor = board.getPiece(myPosition).getTeamColor();

        // White pawn
        if (pieceColor == ChessGame.TeamColor.WHITE) {
            ChessPosition white1step = new ChessPosition(row + 1, col);
            ChessPosition white2step = new ChessPosition(4, col);

            // add 2 step
            if (row == 2 && board.getPiece(white2step) == null) {
                pawnMoves.add(new ChessMove(myPosition, white2step, null));
            }

            // add 1 step
            if (board.getPiece(white1step) == null && row != 8) {
                pawnMoves.add(new ChessMove(myPosition, white1step, null));
            }
//            else if (board.getPiece(white1step) != null && row == 8) {
////                promote(white1step);
//            }

            // add diagonal
            if (col != 1 && col != 8) {
                ChessPosition whiteLeft = new ChessPosition(row + 1, col - 1);
                ChessPosition whiteRight = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(whiteLeft) != null && board.getPiece(whiteLeft).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, whiteLeft, null));
                } else if (board.getPiece(whiteRight) != null && board.getPiece(whiteRight).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, whiteRight, null));
                }
            } else if (col == 1) {
                ChessPosition whiteRight = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(whiteRight) != null && board.getPiece(whiteRight).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, whiteRight, null));
                }
            } else {
                ChessPosition whiteLeft = new ChessPosition(row + 1, col - 1);
                if (board.getPiece(whiteLeft) != null && board.getPiece(whiteLeft).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, whiteLeft, null));
                }
            }
        }

        // Black pawn
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            ChessPosition black1step = new ChessPosition(row - 1, col);
            ChessPosition black2step = new ChessPosition(5, col);

            // add 2 step
            if (row == 5 && board.getPiece(black2step) == null) {
                pawnMoves.add(new ChessMove(myPosition, black2step, null));
            }

            // add 1 step
            if (board.getPiece(black1step) == null) {
                pawnMoves.add(new ChessMove(myPosition, black1step, null));
            }

            // add diagonal
            if (col != 1 && col != 8) {
                ChessPosition blackLeft = new ChessPosition(row - 1, col - 1);
                ChessPosition blackRight = new ChessPosition(row - 1, col + 1);
                if (board.getPiece(blackLeft) != null && board.getPiece(blackLeft).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, blackLeft, null));
                } else if (board.getPiece(blackRight) != null && board.getPiece(blackRight).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, blackRight, null));
                }
            } else if (col == 1) {
                ChessPosition blackRight = new ChessPosition(row - 1, col + 1);
                if (board.getPiece(blackRight) != null && board.getPiece(blackRight).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, blackRight, null));
                }
            } else {
                ChessPosition blackLeft = new ChessPosition(row - 1, col - 1);
                if (board.getPiece(blackLeft) != null && board.getPiece(blackLeft).getTeamColor() != pieceColor) {
                    pawnMoves.add(new ChessMove(myPosition, blackLeft, null));
                }
            }
        }
//        private static void promote(ChessPosition white1step) {
//            pawnMoves
//        }


        return pawnMoves;
    }


}
