package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

  private final ChessGame.TeamColor pieceColor;
  private final PieceType type;

  public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    this.pieceColor = pieceColor;
    this.type = type;
  }

  /**
   * The various different chess piece options
   */
  public enum PieceType {
      KING,
      QUEEN,
      BISHOP,
      KNIGHT,
      ROOK,
      PAWN
  }

  /**
   * @return Which team this chess piece belongs to
   */
  public ChessGame.TeamColor getTeamColor() {
      return pieceColor;
  }

  /**
   * @return which type of chess piece this piece is
   */
  public PieceType getPieceType() {
      return type;
  }

  /**
   * Calculates all the positions a chess piece can move to
   * Does not take into account moves that are illegal due to leaving the king in
   * danger
   *
   * @return Collection of valid moves
   */
  public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

      Rule rule =switch (getPieceType()) {
        case KING -> new Rule(false, myPosition, new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}});

        case QUEEN -> new Rule(true, myPosition, new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}});

        case BISHOP -> new Rule(true, myPosition, new int[][]{{1, 1}, {1, -1}, {-1, -1}, {-1, 1}});

        case KNIGHT -> new Rule(false, myPosition, new int[][]{{2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}});

        case ROOK -> new Rule(true, myPosition, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});

        case PAWN -> new Rule(true, myPosition, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
         // @TODO implement something for the pawn
        default -> null;
      };
      return rule.getMoves(board);
  }

//  public HashSet<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
//    HashSet<ChessMove> moves = new HashSet<ChessMove>();
//    return moves;
//  }

  @Override
  public String toString() {
    return "ChessPiece{" +
            "pieceColor=" + pieceColor +
            ", type=" + type +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ChessPiece that=(ChessPiece) o;
    return pieceColor == that.pieceColor && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(pieceColor, type);
  }
}
