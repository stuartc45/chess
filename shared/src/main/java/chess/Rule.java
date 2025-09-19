package chess;

import java.util.*;

public class Rule {
  private final boolean recurse;
  private final ChessPosition myPosition;
  private final int[][] moves;


  public Rule(boolean recurse, ChessPosition myPosition, int[][] moves) {
    this.recurse=recurse;
    this.myPosition=myPosition;
    this.moves=moves;
  }

  public void movesHelper(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start, int[] direction) {
    ChessPosition end = new ChessPosition(start.getRow() + direction[0], start.getColumn() + direction[1]);
    if (end.getRow() > 8 || end.getRow() < 1 || end.getColumn() > 8 || end.getColumn() < 1) {
      return;
    }
    if (board.getPiece(end) != null) {
      if (board.getPiece(end).getTeamColor() == board.getPiece(myPosition).getTeamColor()) {
        return;
      }
      validMoves.add(new ChessMove(myPosition, end, null));
      return;
    }
    validMoves.add(new ChessMove(myPosition, end, null));
    if (recurse) {
      movesHelper(validMoves, board, end, direction);
    }
  }

  public Collection<ChessMove> getMoves(ChessBoard board) {
    Collection<ChessMove> validMoves = new HashSet<ChessMove>();

    for (int[] move : moves) {
      movesHelper(validMoves, board, myPosition, move);
    }
    return validMoves;
  }
}
