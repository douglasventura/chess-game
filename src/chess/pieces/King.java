package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class King extends ChessPiece {

  public King(Board board, Color color) {
    super(board, color);
  }

  @Override
  public String toString() {
    return "K";
  }

  private boolean canMove(Position position) {
    ChessPiece piece = (ChessPiece) getBoard().piece(position);
    return (piece == null || piece.getColor() != getColor());
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

    return matrix;
  }
   
}
