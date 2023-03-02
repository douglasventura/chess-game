package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

  private ChessMatch chessMatch;

  public Pawn(Board board, Color color, ChessMatch chessMatch) {
    super(board, color);
    this.chessMatch = chessMatch;
  }

  @Override
  public String toString() {
    return "P";
  }

  @Override
  public boolean[][] possibleMoves() {
    boolean[][] matrix = new boolean[getBoard().getRows()][getBoard().getColumns()];

    Position p = new Position(0, 0);

    if (getColor() == Color.WHITE) {
      // move one position to above
      p.setValues(position.getRow() - 1, position.getColumn());
      if (getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p))) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move two position to above, if it's first moviment
      p.setValues(position.getRow() - 2, position.getColumn());
      Position p2 = new Position(position.getRow() - 1, position.getColumn());
      if (getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p))
          && getBoard().positionExists(p2)
          && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move one position to northeast, if there is a opponent piece
      p.setValues(position.getRow() - 1, position.getColumn() + 1);
      if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move one positoin to northwest, if there is a opponent piece
      p.setValues(position.getRow() - 1, position.getColumn() - 1);
      if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // special move: en passant white
      if (position.getRow() == 3) { // row 3 in matrix -> row 5 in board
        // check left
        Position left = new Position(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
          matrix[left.getRow() - 1][left.getColumn()] = true;
        }
        // check right
        Position right = new Position(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
          matrix[right.getRow() - 1][right.getColumn()] = true;
        }
      }

    } else {
      // move one position to below
      p.setValues(position.getRow() + 1, position.getColumn());
      if (getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p))) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move two position to below, if it's first moviment
      p.setValues(position.getRow() + 2, position.getColumn());
      Position p2 = new Position(position.getRow() + 1, position.getColumn());
      if (getBoard().positionExists(p) && (!getBoard().thereIsAPiece(p) || isThereOpponentPiece(p))
          && getBoard().positionExists(p2)
          && !getBoard().thereIsAPiece(p2) && getMoveCount() == 0) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move one position to southeast, if there is a opponent piece
      p.setValues(position.getRow() + 1, position.getColumn() + 1);
      if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
        matrix[p.getRow()][p.getColumn()] = true;
      }

      // move one positoin to southwest, if there is a opponent piece
      p.setValues(position.getRow() + 1, position.getColumn() - 1);
      if (getBoard().positionExists(p) && isThereOpponentPiece(p)) {
        matrix[p.getRow()][p.getColumn()] = true;
      }
      
      // special move: en passant black
      if (position.getRow() == 4) { // row 4 in matrix -> row 4 in board
        // check left
        Position left = new Position(position.getRow(), position.getColumn() - 1);
        if (getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantVulnerable()) {
          matrix[left.getRow() + 1][left.getColumn()] = true;
          System.out.println("entrei: " + left.getRow() + ", " + left.getColumn());
        }
        // check right
        Position right = new Position(position.getRow(), position.getColumn() + 1);
        if (getBoard().positionExists(right) && isThereOpponentPiece(right) && getBoard().piece(right) == chessMatch.getEnPassantVulnerable()) {
          matrix[right.getRow() + 1][right.getColumn()] = true;
          System.out.println("entrei 4: " + right.getRow() + ", " + right.getColumn());
        }
      }
    }

    return matrix;
  }

}
