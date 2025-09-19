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

  private void movesHelper(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start, int[] direction) {
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

  private void pawnMovesHelper(Collection<ChessMove> validMoves, ChessBoard board, ChessPosition start, int[] direction) {
    ChessGame.TeamColor color = board.getPiece(myPosition).getTeamColor();
    if (color == ChessGame.TeamColor.BLACK) {
      direction[0] *= -1;
    }
    ChessPosition end = new ChessPosition(start.getRow() + direction[0], start.getColumn() + direction[1]);
    if (end.getRow() == 8 || end.getRow() == 1) {
      // @TODO handle promotions
    }
    if (board.getPiece(end) == null) {
      validMoves.add(new ChessMove(myPosition, end, null));
    }
    if (color == ChessGame.TeamColor.BLACK && myPosition.getRow() == 7) {
      ChessPosition firstMove = new ChessPosition(end.getRow() -1, start.getColumn());
      if (board.getPiece(firstMove) == null) {
        validMoves.add(new ChessMove(myPosition, firstMove, null));
      }
    }
    if (color == ChessGame.TeamColor.WHITE && myPosition.getRow() == 2) {
      ChessPosition firstMove = new ChessPosition(end.getRow() +1, start.getColumn());
      if (board.getPiece(firstMove) == null) {
        validMoves.add(new ChessMove(myPosition, firstMove, null));
      }
    }
    


    // check piece color
    // do move (forward one square)
    // check for promotion
    // check if blocked
    // if first move
    //    then check one more square
    // check diagonal for enemies (don't check out of bounds)
    // if true
    //    add move
    //
  }

  public Collection<ChessMove> getMoves(ChessBoard board) {
    Collection<ChessMove> validMoves = new HashSet<ChessMove>();

    if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN) {
      pawnMovesHelper(validMoves, board, myPosition, moves[0]);
      return validMoves;
    }
    for (int[] move : moves) {
      movesHelper(validMoves, board, myPosition, move);
    }
    return validMoves;
  }
}
