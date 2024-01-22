package chess;

public class testFile {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        ChessPosition position = new ChessPosition(5, 4);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board.addPiece(position, piece);
        System.out.println(board.getPiece(position));
        System.out.println(board.getPiece(position).getPieceType());
    }
}

/*
validateMoves("""
                       8 | | | | | | | | |
                       7 | | | | | | | | |
                       6 | | | | | | | | |
                       5 | | | |B| | | | |
                       4 | | | | | | | | |
                       3 | | | | | | | | |
                       2 | | | | | | | | |
                       1 | | | | | | | | |
                          1 2 3 4 5 6 7 8
                        """,
                startPosition(5, 4),
                endPositions(new int[][]{
                        {6, 5}, {7, 6}, {8, 7},
                        {4, 5}, {3, 6}, {2, 7}, {1, 8},
                        {4, 3}, {3, 2}, {2, 1},
                        {6, 3}, {7, 2}, {8, 1},
                })
        );
 */