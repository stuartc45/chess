import ui.ChessClient;


public class Main {
    public static void main(String[] args) {
        if (args.length > 2) {
            new ChessClient("http://localhost:" + args[1]).run();
        } else {
            new ChessClient("http://localhost:8080").run();
        }



//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
    }
}