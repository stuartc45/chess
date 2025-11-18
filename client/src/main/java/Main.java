import ui.ChessClient;

public class Main {
    public static void main(String[] args) {
        new ChessClient("server").run();

//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);
    }
}