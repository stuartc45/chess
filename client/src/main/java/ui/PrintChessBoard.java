package ui;

import chess.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;


public class PrintChessBoard {
    private final ChessGame.TeamColor color;

    public PrintChessBoard(ChessGame.TeamColor color) {
        this.color = color;
    }

     public void printBoard(ChessBoard board) {
        boolean isWhite;
        isWhite = color == ChessGame.TeamColor.WHITE;
        var boardString = new StringBuilder();
        String letters = isWhite ? "    a   b   c  d   e  f   g   h    " : "    h   g   f  e   d  c   b   a    ";
        boardString.append(SET_BG_COLOR_RED).append(SET_TEXT_COLOR_WHITE)
                .append(letters)
                .append(RESET_BG_COLOR)
                .append("\n");
        for (int i = 0; i < 8; i++) {
            int rowNum = isWhite ? 8 - i : i + 1;
            boardString.append(SET_BG_COLOR_RED).append(" ").append(rowNum).append(" ");
            for (int j = 0; j < 8; j++) {
                int colNum = isWhite ? j + 1 : 8 - j;
                if ((i + j)% 2 == 0) {
                    boardString.append(SET_BG_COLOR_LIGHT_GREY);
                } else {
                    boardString.append(SET_BG_COLOR_BLACK);
                }
                boardString.append(getPieceSymbol(board.getPiece(new ChessPosition(rowNum, colNum))));
            }
            boardString.append(SET_BG_COLOR_RED)
                    .append(" ")
                    .append(rowNum)
                    .append(" ")
                    .append(RESET_BG_COLOR)
                    .append("\n");
        }
        boardString.append(SET_BG_COLOR_RED)
                .append(letters)
                .append(RESET_BG_COLOR);
        System.out.println(boardString.toString());
     }

     public void printHighlightBoard(ChessGame game, ChessPosition position) {
         ChessBoard board = game.getBoard();
         Collection<ChessMove> validMoves = game.validMoves(position);
         Set<ChessPosition> highlight = new HashSet<>();
         for (var move : validMoves) {
             highlight.add(move.getEndPosition());
         }
         highlight.add(position);
         boolean isWhite;
         isWhite = color == ChessGame.TeamColor.WHITE;
         var boardString = new StringBuilder();
         String letters = isWhite ? "    a   b   c  d   e  f   g   h    " : "    h   g   f  e   d  c   b   a    ";
         boardString.append(SET_BG_COLOR_RED).append(SET_TEXT_COLOR_WHITE)
                 .append(letters)
                 .append(RESET_BG_COLOR)
                 .append("\n");
         for (int i = 0; i < 8; i++) {
             int rowNum = isWhite ? 8 - i : i + 1;
             boardString.append(SET_BG_COLOR_RED).append(" ").append(rowNum).append(" ");

             for (int j = 0; j < 8; j++) {
                 int colNum = isWhite ? j + 1 : 8 - j;
                 ChessPosition square = new ChessPosition(rowNum, colNum);

                 boolean highlighted = highlight.contains(square);

                 if (highlighted && (i + j) % 2 == 0) {
                     boardString.append(SET_BG_COLOR_GREEN);
                 } else if (highlighted) {
                     boardString.append(SET_BG_COLOR_DARK_GREEN);
                 } else if ((i + j) % 2 == 0) {
                     boardString.append(SET_BG_COLOR_LIGHT_GREY);
                 } else {
                     boardString.append(SET_BG_COLOR_BLACK);
                 }

                 boardString.append(getPieceSymbol(board.getPiece(square)));
             }

             boardString.append(SET_BG_COLOR_RED)
                     .append(" ").append(rowNum).append(" ")
                     .append(RESET_BG_COLOR)
                     .append("\n");
         }
         boardString.append(SET_BG_COLOR_RED)
                 .append(letters)
                 .append(RESET_BG_COLOR);
         System.out.println(boardString.toString());
     }

     private String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? WHITE_KING : BLACK_KING;
            case QUEEN -> isWhite ? WHITE_QUEEN : BLACK_QUEEN;
            case ROOK -> isWhite ? WHITE_ROOK : BLACK_ROOK;
            case BISHOP -> isWhite ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> isWhite ? WHITE_KNIGHT : BLACK_KNIGHT;
            case PAWN -> isWhite ? WHITE_PAWN : BLACK_PAWN;
        };
     }
}
