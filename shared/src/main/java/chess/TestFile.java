package chess;

public class TestFile {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        ChessPosition position = new ChessPosition(5, 4);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board.addPiece(position, piece);
        System.out.println(board.getPiece(position));
        System.out.println(board.getPiece(position).getPieceType());
    }
}