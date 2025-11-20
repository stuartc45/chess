import chess.ChessBoard;
import ui.ChessClient;
import ui.PrintChessBoard;


public class Main {
    public static void main(String[] args) {
        var white = new PrintChessBoard("white");
        var black = new PrintChessBoard("black");
        var board = new ChessBoard();
        board.resetBoard();
        white.printBoard(board);
        black.printBoard(board);
//        if (args.length > 2) {
//            new ChessClient("http://localhost:" + args[1]).run();
//        } else {
//            new ChessClient("http://localhost:8080").run();
//        }
    }
}