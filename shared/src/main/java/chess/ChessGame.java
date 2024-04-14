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
    private boolean gameIsOver;

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

    public void setGameIsOverTrue() {
        this.gameIsOver = true;
    }

    public boolean isGameOver() {
        if (this.isInCheckmate(TeamColor.WHITE) || this.isInCheckmate(TeamColor.BLACK) || this.isInStalemate(TeamColor.WHITE) || this.isInStalemate(TeamColor.BLACK)) {
            this.setGameIsOverTrue();
        }
        return this.gameIsOver;
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

        if (validMoves(move.getStartPosition()).contains(move) && this.currentTeamColor == this.board.getPiece(move.getStartPosition()).getTeamColor()) {
            ChessPiece piece = this.board.getPiece(move.getStartPosition());
            this.board.removePiece(move.getStartPosition());
            this.board.removePiece(move.getEndPosition());
            this.board.addPiece(move.getEndPosition(), piece);

            if (move.getPromotionPiece() != null) {
                this.board.removePiece(move.getEndPosition());
                this.board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }
            currentTeamColor = (currentTeamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        } else {
            throw new InvalidMoveException("Move is invalid");
        }


    }


    private boolean moveIsValid(ChessMove move) {
        ChessBoard boardCopy = this.board.copy();
        ChessPiece piece = this.board.getPiece(move.getStartPosition());

        boardCopy.removePiece(move.getEndPosition());
        boardCopy.removePiece(move.getStartPosition());
        boardCopy.addPiece(move.getEndPosition(), piece);
        ChessGame tempGame = new ChessGame();
        tempGame.setBoard(boardCopy);

        if (tempGame.isInCheck(boardCopy.getPiece(move.getEndPosition()).getTeamColor())) {
            return false;
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
//        this.tempBoard = this.board.copy();
        ChessPosition kingPosition = getKingPosition(teamColor);
        if (kingPosition != null) {
            ChessPiece king = this.board.getPiece(kingPosition);
            Collection <ChessMove> kingMoves = king.pieceMoves(this.board, kingPosition);
        }

        // get positions of all enemy pieces
        ArrayList<ChessPosition> enemyPiecePositions = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() != teamColor) {
                    enemyPiecePositions.add(new ChessPosition(i, j));
                }
            }
        }
        // check if any enemy piece can move to the king's position
        for (ChessPosition position : enemyPiecePositions) {
            Collection<ChessMove> moves = this.board.getPiece(position).pieceMoves(this.board, position);
            if (moves.isEmpty()) {
                moves = this.board.getPiece(position).pieceMoves(this.board, position);
            }
//            System.out.println(moves);
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int i = 8; i > 0; i--) {
            for (int j = 8; j > 0; j--) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return new ChessPosition(i, j);
                }
            }
        }
        return null;
    }

    private Collection<ChessMove> addAllMoves(TeamColor teamColor) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPiece piece = this.board.getPiece(new ChessPosition(i, j));
                if (piece != null && piece.getTeamColor() == teamColor) {
                    ChessPosition currentPosition = new ChessPosition(i, j);
                    // Collect all moves for the piece
                    allMoves.addAll(validMoves(currentPosition));
                }
            }
        }
        return allMoves;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> allMoves = addAllMoves(teamColor);

        // Check for checkmate
        if (isInCheck(teamColor) && allMoves.isEmpty()) {
            setGameIsOverTrue();
            return true;
        }
        return false;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Collection<ChessMove> allMoves = addAllMoves(teamColor);

        // Check for stalemate
        if (!isInCheck(teamColor) && allMoves.isEmpty()) {
            setGameIsOverTrue();
            return true;
        }
        return false;
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
