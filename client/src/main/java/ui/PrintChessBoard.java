package ui;

import chess.ChessBoard;
import static ui.EscapeSequences.*;


public class PrintChessBoard {
    private final String color;

    public PrintChessBoard(String color) {
        this.color = color;
    }

     public void printBoard(ChessBoard board) {
        Boolean isWhite = color.equalsIgnoreCase("white");
        var boardString = new StringBuilder();
        boardString.append(SET_BG_COLOR_RED)
                .append("    a   b   c  d   e  f   g   h    ")
                .append(RESET_BG_COLOR)
                .append("\n");
        for (int i = 0; i < 8; i++) {
            boardString.append(SET_BG_COLOR_RED).append(" ").append(8 - i).append(" ");
            for (int j = 0; j < 8; j++) {
                if ((i + j)% 2 == 0) {
                    boardString.append(SET_BG_COLOR_WHITE);
                } else {
                    boardString.append(SET_BG_COLOR_BLUE);
                }
                
            }
            boardString.append(SET_BG_COLOR_RED)
                    .append(" ")
                    .append(8 - i)
                    .append(" ")
                    .append(RESET_BG_COLOR)
                    .append("\n");
        }
        boardString.append(SET_BG_COLOR_RED).append("    a   b   c  d   e  f   g   h    ").append(RESET_BG_COLOR);
        System.out.println(boardString.toString());
     }
}
