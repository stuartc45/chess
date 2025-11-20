import chess.ChessBoard;
import ui.ChessClient;
import ui.PrintChessBoard;


public class Main {
    public static void main(String[] args) {
        var printing = new PrintChessBoard("white");
        printing.printBoard(new ChessBoard());
//        if (args.length > 2) {
//            new ChessClient("http://localhost:" + args[1]).run();
//        } else {
//            new ChessClient("http://localhost:8080").run();
//        }
    }
}