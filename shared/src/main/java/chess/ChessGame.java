package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor currentTeamColor;


    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.setTeamTurn(TeamColor.WHITE);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.currentTeamColor;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTeamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = this.board.getPiece(startPosition);
        if (piece == null) {
            return null;
        } else {
            Collection<ChessMove> allMoves = piece.pieceMoves(this.board, startPosition);
            Collection<ChessMove> validMoves = new ArrayList<>();
            for (ChessMove move : allMoves) {
                if (moveIsValid(move)) {
                    validMoves.add(move);
                }
            }
            return validMoves;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
        // throw new InvalidMoveException("something something");
    }

    private boolean moveIsValid(ChessMove move) {
        ChessBoard boardCopy = this.board;
        ChessPiece piece = boardCopy.getPiece(move.getStartPosition());
        if (isInCheck(boardCopy.getPiece(move.getStartPosition()).getTeamColor())) {
            boardCopy.removePiece(move.getEndPosition());
            boardCopy.removePiece(move.getStartPosition());
            boardCopy.addPiece(move.getEndPosition(), piece);
            if (isInCheck(boardCopy.getPiece(move.getEndPosition()).getTeamColor())) {
                return false;
            }
            return true;
        }
        return true;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        ChessPiece king = this.board.getPiece(kingPosition);
        Collection <ChessMove> kingMoves = king.pieceMoves(this.board, kingPosition);

        ArrayList<ChessPiece> enemyPiecePositions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    enemyPiecePositions.add(new ChessPosition(i, j));
                }
            }
        }

        for (ChessPiece piece : enemyPieces) {
            Collection<ChessMove> moves = piece.pieceMoves(this.board, );
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
