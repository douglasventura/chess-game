package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

  private ChessMatch chessMatch;

  public King(Board board, Color color, ChessMatch chessMatch) {
    super(board, color);
    this.chessMatch = chessMatch;
  }

  @Override
  public String toString() {
    return "K";
  }

  private boolean canMove(Position position) {
    ChessPiece piece = (ChessPiece) getBoard().piece(position);
    return (piece == null || piece.getColor() != getColor());
  }

  private boolean testRockCastling(Position position) {
    ChessPiece p = (ChessPiece)getBoard().piece(position);
    return p != null && p instanceof Rook && p.getColor() == getColor() && p.getMoveCount() == 0;
  }

  @Override
  public boolean[][] possibleMoves() {
    boolean[][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];

    Position p = new Position(0, 0);

    // above
    p.setValues(position.getRow() - 1, position.getColumn());
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // below
    p.setValues(position.getRow() + 1, position.getColumn());
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // left
    p.setValues(position.getRow(), position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // right
    p.setValues(position.getRow(), position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // northeast
    p.setValues(position.getRow() - 1, position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // northwest
    p.setValues(position.getRow() - 1, position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // southeast
    p.setValues(position.getRow() + 1, position.getColumn() + 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // southwest
    p.setValues(position.getRow() + 1, position.getColumn() - 1);
    if (getBoard().positionExists(p) && canMove(p)) {
      matrix[p.getRow()][p.getColumn()] = true;
    }

    // special move: castling
    if (getMoveCount() == 0 && !chessMatch.getCheck()) {
      // kingside rook
      Position target1 = new Position(position.getRow(), position.getColumn() + 3);
      if (testRockCastling(target1)) {
        Position p1 = new Position(position.getRow(), position.getColumn() + 1);
        Position p2 = new Position(position.getRow(), position.getColumn() + 2);
        if (getBoard().piece(p1) == null && getBoard().piece(p2) == null) {
          matrix[position.getRow()][position.getColumn() + 2] = true;
        }
      }
      // queenside rook
      Position target2 = new Position(position.getRow(), position.getColumn() - 4);
      if (testRockCastling(target2)) {
        Position p1 = new Position(position.getRow(), position.getColumn() - 1);
        Position p2 = new Position(position.getRow(), position.getColumn() - 2);
        Position p3 = new Position(position.getRow(), position.getColumn() - 3);
        if (getBoard().piece(p1) == null && getBoard().piece(p2) == null && getBoard().piece(p3) == null) {
          matrix[position.getRow()][position.getColumn() - 2] = true;
        }
      }
    }

    return matrix;
  }
   
}
