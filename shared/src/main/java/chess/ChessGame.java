package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;
    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> moves = new HashSet<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)){
            if (!checkMove(move)) {
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
//        ChessPosition start = move.getStartPosition();
//        ChessPosition end = move.getEndPosition();
//        ChessPiece capturePiece = board.getPiece(end);
//        ChessPiece piece = board.getPiece(start);
//        TeamColor color = piece.getTeamColor();
//        if (color != getTeamTurn()) {
//            throw new InvalidMoveException();
//        }
//        if (capturePiece != null) {
//            board.removePiece(end);
//        }
//        board.addPiece(end, piece);
//        board.removePiece(start);
//
//
//        if (isInCheck(color)) {
//            board.addPiece(start, piece);
//            board.removePiece(end);
//            if (capturePiece != null) {
//                board.addPiece(end, capturePiece);
//            }
//
//            throw new InvalidMoveException();
//        }







        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece capturePiece = board.getPiece(end);
        ChessPiece piece = board.getPiece(start);
        if (piece == null) {
            throw new InvalidMoveException();
        }
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> moves = validMoves(start);
        if (color != getTeamTurn()) {
            throw new InvalidMoveException();
        }
        if (moves.contains(move)) {
            if (capturePiece != null) {
            board.removePiece(end);
            }
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (end.getRow() == 1 || end.getRow() == 8)) {
                ChessPiece promotion = new ChessPiece(color, move.getPromotionPiece());
                board.addPiece(end, promotion);
            }
            else {board.addPiece(end, piece);}
            board.removePiece(start);
        }
        else {
            throw new InvalidMoveException();
        }

        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPosition = spot;
                }
            }
        }
        ChessMove promotionMove = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, spot);
                    if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (kingPosition.getRow() == 1 || kingPosition.getRow() == 8)) {
                        promotionMove = new ChessMove(spot, kingPosition, ChessPiece.PieceType.QUEEN);
                    }
                    ChessMove move = new ChessMove(spot, kingPosition, null);
                    if (possibleMoves.contains(move) || possibleMoves.contains(promotionMove)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(spot);
                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition spot = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(spot);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = validMoves(spot);
                    if (!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return !isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private boolean checkMove(ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece capturePiece = board.getPiece(end);
        ChessPiece piece = board.getPiece(start);
        TeamColor color = piece.getTeamColor();
        boolean check = false;

        if (capturePiece != null) {
            board.removePiece(end);
        }
        board.addPiece(end, piece);
        board.removePiece(start);


        if (isInCheck(color)) {
            check = true;
        }
        board.addPiece(start, piece);
        board.removePiece(end);
        if (capturePiece != null) {
            board.addPiece(end, capturePiece);
        }
        return check;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame=(ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
