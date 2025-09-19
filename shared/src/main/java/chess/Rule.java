package chess;

import java.util.*;

public class Rule {
  private final boolean recurse;
  private final int[][] moves;

  public Rule(boolean recurse, int[][] moves) {
    this.recurse=recurse;
    this.moves=moves;
  }


  public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition start) {
    Collection<ChessMove> moves = new HashSet<ChessMove>();



    return moves;
  }
}
