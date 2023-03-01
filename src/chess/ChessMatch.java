package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Rook;
import chess.pieces.Queen;

public class ChessMatch {

  private Board board;
  private Integer turn;
  private Color currentPlayer;
  private boolean check;
  private boolean checkMate;

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

  public boolean getCheckMate() {
    return checkMate;
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

    if (testCheckMate(opponent(currentPlayer))) {
      checkMate = true;
    } else {
      nextTurn();
    }

    return (ChessPiece) capturedPiece;
  }

  public boolean[][] possibleMoves(ChessPosition sourcePosition) {
    Position position = sourcePosition.toPosition();
    validateSourcePosition(position);
    return board.piece(position).possibleMoves();
  }

  private Piece makeMove(Position source, Position target) {
    ChessPiece piece = (ChessPiece) board.removePiece(source);
    piece.increaseMoveCount();
    Piece capturedPiece = board.removePiece(target);
    board.placePiece(piece, target);

    if (piece != null) {
      piecesOnTheBoard.remove(capturedPiece);
      capturedPieces.add(capturedPiece);
    }

    // special move: castling kingside rook
    if (piece instanceof King && target.getColumn() == source.getColumn() + 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
      Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
      ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
      board.placePiece(rook, targetRook);
      rook.increaseMoveCount();
    }

    // special move: castling queenside rook
    if (piece instanceof King && target.getColumn() == source.getColumn() - 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
      Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
      ChessPiece rook = (ChessPiece) board.removePiece(sourceRook);
      board.placePiece(rook, targetRook);
      rook.increaseMoveCount();
    }

    return capturedPiece;
  }

  private void undoMove(Position source, Position target, Piece capturedPiece) {
    ChessPiece piece = (ChessPiece) board.removePiece(target);
    piece.decreaseMoveCount();
    board.placePiece(piece, source);

    if (capturedPiece != null) {
      board.placePiece(capturedPiece, target);
      capturedPieces.remove(capturedPiece);
      piecesOnTheBoard.add(capturedPiece);
    }

    // undo special move: castling kingside rook
    if (piece instanceof King && target.getColumn() == source.getColumn() + 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
      Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
      ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
      board.placePiece(rook, sourceRook);
      rook.decreaseMoveCount();
    }

    // undo special move: castling queenside rook
    if (piece instanceof King && target.getColumn() == source.getColumn() - 2) {
      Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);
      Position targetRook = new Position(source.getRow(), source.getColumn() - 1);
      ChessPiece rook = (ChessPiece) board.removePiece(targetRook);
      board.placePiece(rook, sourceRook);
      rook.decreaseMoveCount();
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
    List<Piece> opponentPieces = piecesOnTheBoard.stream()
        .filter(piece -> ((ChessPiece) piece).getColor() == opponent(color)).collect(Collectors.toList());

    for (Piece p : opponentPieces) {
      boolean[][] pieces = p.possibleMoves();
      if (pieces[kingPosition.getRow()][kingPosition.getColumn()]) {
        return true;
      }
    }
    return false;
  }

  private boolean testCheckMate(Color color) {
    if (!testCheck(color)) {
      return false;
    }

    List<Piece> pieces = piecesOnTheBoard.stream().filter(piece -> ((ChessPiece) piece).getColor() == color)
        .collect(Collectors.toList());
    for (Piece piece : pieces) {
      boolean[][] aux = piece.possibleMoves();
      for (int i = 0; i < board.getRows(); i++) {
        for (int j = 0; j < board.getColumns(); j++) {
          if (aux[i][j]) {
            Position source = ((ChessPiece) piece).getChessPosition().toPosition();
            Position target = new Position(i, j);
            Piece capturedPiece = makeMove(source, target);
            boolean testCheck = testCheck(color);
            undoMove(source, target, capturedPiece);

            if (!testCheck) {
              return false;
            }
          }
        }

      }
    }
    return true;
  }

  private ChessPiece king(Color color) {
    List<Piece> pieces = piecesOnTheBoard.stream().filter(piece -> ((ChessPiece) piece).getColor() == color)
        .collect(Collectors.toList());

    for (Piece p : pieces) {
      if (p instanceof King) {
        return (ChessPiece) p;
      }
    }
    throw new IllegalStateException("There is no " + color + " king on the board");
  }

  private void initialSetup() {

    // WHITE PIECES
    // pawns
    placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
    placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

    // rooks
    placeNewPiece('a', 1, new Rook(board, Color.WHITE));
    placeNewPiece('h', 1, new Rook(board, Color.WHITE));

    // bishops
    placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
    placeNewPiece('f', 1, new Bishop(board, Color.WHITE));

    // knights
    placeNewPiece('b', 1, new Knight(board, Color.WHITE));
    placeNewPiece('g', 1, new Knight(board, Color.WHITE));

    // another
    placeNewPiece('d', 1, new Queen(board, Color.WHITE));
    placeNewPiece('e', 1, new King(board, Color.WHITE, this));

    // BLACK PIECES
    // pawns
    placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
    placeNewPiece('h', 7, new Pawn(board, Color.BLACK));

    // rooks
    placeNewPiece('a', 8, new Rook(board, Color.BLACK));
    placeNewPiece('h', 8, new Rook(board, Color.BLACK));

    // bishops
    placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
    placeNewPiece('f', 8, new Bishop(board, Color.BLACK));

    // knights
    placeNewPiece('b', 8, new Knight(board, Color.BLACK));
    placeNewPiece('g', 8, new Knight(board, Color.BLACK));

    // another
    placeNewPiece('d', 8, new Queen(board, Color.BLACK));
    placeNewPiece('e', 8, new King(board, Color.BLACK, this));
  }

}
