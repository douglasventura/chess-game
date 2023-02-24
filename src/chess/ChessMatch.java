package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

  private Board board;
  private Integer turn;
  private Color currentPlayer;
  private boolean check;

  List<Piece> piecesOnTheBoard = new ArrayList<>();
  List<Piece> capturedPieces = new ArrayList<>();

  public ChessMatch() {
    board = new Board(8, 8);
    turn = 1;
    currentPlayer = Color.WHITE;
    initialSetup();
  }

  public Integer getTurn() {
    return turn;
  }

  public Color getCurrentPlayer() {
    return currentPlayer;
  }

  public boolean getCheck() {
    return check;
  }

  public ChessPiece[][] getPieces() {
    ChessPiece[][] chessPieces = new ChessPiece[board.getRows()][board.getColumns()];

    for (int i = 0; i < board.getRows(); i++) {
      for (int j = 0; j < board.getColumns(); j++) {
        chessPieces[i][j] = (ChessPiece) board.piece(i, j);
      }
    }

    return chessPieces;
  }

  public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
    Position source = sourcePosition.toPosition();
    Position target = targetPosition.toPosition();
    validateSourcePosition(source);
    validateTargetPosition(source, target);
    Piece capturedPiece = makeMove(source, target);

    if (testCheck(currentPlayer)) {
      undoMove(source, target, capturedPiece);
      throw new ChessException("You can't put yourself in check");
    }

    check = (testCheck(opponent(currentPlayer))) ? true : false;
    nextTurn();
    return (ChessPiece) capturedPiece;
  }

  public boolean[][] possibleMoves(ChessPosition sourcePosition) {
    Position position = sourcePosition.toPosition();
    validateSourcePosition(position);
    return board.piece(position).possibleMoves();
  }

  private Piece makeMove(Position source, Position target) {
    Piece piece = board.removePiece(source);
    Piece capturedPiece = board.removePiece(target);
    board.placePiece(piece, target);

    if (piece != null) {
      piecesOnTheBoard.remove(capturedPiece);
      capturedPieces.add(capturedPiece);
    }

    return capturedPiece;
  }

  private void undoMove(Position source, Position target, Piece capturedPiece) {
    Piece piece = board.removePiece(target);
    board.placePiece(piece, source);

    if (capturedPiece != null) {
      board.placePiece(capturedPiece, target);
      capturedPieces.remove(capturedPiece);
      piecesOnTheBoard.add(capturedPiece);
    }
  }

  private void validateSourcePosition(Position position) {
    if (!board.thereIsAPiece(position)) {
      throw new ChessException("There is no piece on source position.");
    }
    if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
      throw new ChessException("The chosen piece is not yours");
    }
    if (!board.piece(position).isThereAnyPossibleMove()) {
      throw new ChessException("There is no possible moves for the chosen piece");
    }
  }

  private void validateTargetPosition(Position source, Position target) {
    if (!board.piece(source).possibleMove(target)) {
      throw new ChessException("The chosen piece can't move to target position");
    }
  }

  private void nextTurn() {
    turn++;
    currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private void placeNewPiece(char column, Integer row, ChessPiece piece) {
    board.placePiece(piece, new ChessPosition(column, row).toPosition());
    piecesOnTheBoard.add(piece);
  }

  private Color opponent(Color color) {
    return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
  }

  private boolean testCheck(Color color) {
 		Position kingPosition = king(color).getChessPosition().toPosition();
 		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(piece -> ((ChessPiece)piece).getColor() == opponent(color)).collect(Collectors.toList());

 		for (Piece p : opponentPieces) {
 			boolean[][] pieces = p.possibleMoves();
 			if (pieces[kingPosition.getRow()][kingPosition.getColumn()]) {
 				return true;
 			}
 		}
 		return false;
 	}

  private ChessPiece king (Color color) {
    List<Piece> pieces = piecesOnTheBoard.stream().filter(piece -> ((ChessPiece)piece).getColor() == color).collect(Collectors.toList());

    for (Piece p : pieces) {
      if (p instanceof King) {
        return (ChessPiece)p;
      }
    }
    throw new IllegalStateException("There is no " + color + " king on the board");
  }

  private void initialSetup() {
    placeNewPiece('c', 2, new Rook(board, Color.WHITE));
    placeNewPiece('d', 2, new Rook(board, Color.WHITE));
    placeNewPiece('e', 2, new Rook(board, Color.WHITE));
    placeNewPiece('e', 1, new Rook(board, Color.WHITE));
    placeNewPiece('d', 1, new King(board, Color.WHITE));

    placeNewPiece('c', 7, new Rook(board, Color.BLACK));
    placeNewPiece('c', 8, new Rook(board, Color.BLACK));
    placeNewPiece('d', 7, new Rook(board, Color.BLACK));
    placeNewPiece('e', 7, new Rook(board, Color.BLACK));
    placeNewPiece('e', 8, new Rook(board, Color.BLACK));
    placeNewPiece('d', 8, new King(board, Color.BLACK));
  }

}
