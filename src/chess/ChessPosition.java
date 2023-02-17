package chess;

import boardgame.Position;

public class ChessPosition {

  private char column;
  private Integer row;

  public ChessPosition(char column, Integer row) {
    if ((column < 'a' || column > 'h') || (row < 1 || row > 8)) {
      throw new ChessException("Error instantiating ChessPosition. Valid values are from a1 to h8.");
    }
    this.column = column;
    this.row = row;
  }

  public char getColumn() {
    return column;
  }

  public Integer getRow() {
    return row;
  }

  protected Position toPosition() {
    Integer rowPosition = 8 - row;
    Integer columnPosition = column - 'a';
    return new Position(rowPosition, columnPosition);
  }

  protected static ChessPosition fromPosition(Position position) {
    Integer rowPosition = 8 - position.getRow();
    char columnPosition = (char) ('a' + position.getColumn());
    return new ChessPosition(columnPosition, rowPosition);
  }

  @Override
  public String toString() {
    return "" + column + row;
  }

}
