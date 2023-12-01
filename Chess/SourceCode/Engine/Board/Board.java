package SourceCode.Engine.Board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import SourceCode.Engine.Color;
import SourceCode.Engine.Pieces.Bishop;
import SourceCode.Engine.Pieces.King;
import SourceCode.Engine.Pieces.Knight;
import SourceCode.Engine.Pieces.Pawn;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Pieces.Queen;
import SourceCode.Engine.Pieces.Rook;
import SourceCode.Engine.Player.BlackPlayer;
import SourceCode.Engine.Player.Player;
import SourceCode.Engine.Player.WhitePlayer;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private final Pawn enPassantPawn;

    private Board(final Builder builder) {
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateRemainingPieces(this.gameBoard, Color.White);
        this.blackPieces = calculateRemainingPieces(this.gameBoard, Color.Black);
        this.enPassantPawn = builder.enPassantPawn;
        final Collection<Move> whiteAllLegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackAllLegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteAllLegalMoves, blackAllLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackAllLegalMoves, whiteAllLegalMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);

    }

    /*Mark the board as  
        r  n  b  q  k  b  n  r
        p  p  p  p  p  p  p  p
        -  -  -  -  -  -  -  -
        -  -  -  -  -  -  -  -
        -  -  -  -  -  -  -  -
        -  -  -  -  -  -  -  -
        P  P  P  P  P  P  P  P
        R  N  B  Q  K  B  N  R
    */
    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < BoardUtility.Total_Tiles; ++i) {

            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));

            if ((i+1) % BoardUtility.Tolal_Tile_Per_Row == 0) {
                builder.append("\n");
            }
        }

        return builder.toString();
    }

    public Iterable<Move> getAllLegalMoves() {
        List <Move> AllLegalMoves = new ArrayList<>();
        AllLegalMoves.addAll(this.whitePlayer.getLegalMoves());
        AllLegalMoves.addAll(this.blackPlayer.getLegalMoves());
        return Collections.unmodifiableList(AllLegalMoves);
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    /*List of all the legal move a piece can takes */
    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {

        final List<Move> AllLegalMoves = new ArrayList<>();

        for (final Piece piece: pieces) {
            AllLegalMoves.addAll(piece.LegalMove(this));
        }

        return AllLegalMoves;
    }

    /*Add all the remaining pieces on the board to a collection of pieces */
    private static Collection<Piece> calculateRemainingPieces(final List<Tile> gameBoard, final Color color) {

        final List<Piece> remainingPieces = new ArrayList<>();

        for (final Tile tile: gameBoard) {
            if (tile.IsOccupiled()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceColor() == color) {
                    remainingPieces.add(piece);
                }
            }
        }

        return Collections.unmodifiableList(remainingPieces);
    }

    /* Setting the piece on the tiles */
    private static List<Tile> createGameBoard(final Builder builder) {

        final List<Tile> tiles = new ArrayList<>();

        for (int i = 0; i < BoardUtility.Total_Tiles; ++i) {
            //tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
            tiles.add(Tile.createTile(i, builder.boardConfig.get(i)));
        }

        return Collections.unmodifiableList(tiles);

    }

    /* Setting the initial state of the board */
    public static Board createStandardBoard() {

        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(0, Color.Black));
        builder.setPiece(new Knight(1, Color.Black));
        builder.setPiece(new Bishop(2, Color.Black));
        builder.setPiece(new Queen(3, Color.Black));
        builder.setPiece(new King(4, Color.Black));
        builder.setPiece(new Bishop(5, Color.Black));
        builder.setPiece(new Knight(6, Color.Black));
        builder.setPiece(new Rook(7, Color.Black));
        builder.setPiece(new Pawn(8, Color.Black));
        builder.setPiece(new Pawn(9, Color.Black));
        builder.setPiece(new Pawn(10, Color.Black));
        builder.setPiece(new Pawn(11, Color.Black));
        builder.setPiece(new Pawn(12, Color.Black));
        builder.setPiece(new Pawn(13, Color.Black));
        builder.setPiece(new Pawn(14, Color.Black));
        builder.setPiece(new Pawn(15, Color.Black));
        // White Layout
        builder.setPiece(new Pawn(48, Color.White));
        builder.setPiece(new Pawn(49, Color.White));
        builder.setPiece(new Pawn(50, Color.White));
        builder.setPiece(new Pawn(51, Color.White));
        builder.setPiece(new Pawn(52, Color.White));
        builder.setPiece(new Pawn(53, Color.White));
        builder.setPiece(new Pawn(54, Color.White));
        builder.setPiece(new Pawn(55, Color.White));
        builder.setPiece(new Rook(56, Color.White));
        builder.setPiece(new Knight(57, Color.White));
        builder.setPiece(new Bishop(58, Color.White));
        builder.setPiece(new Queen(59, Color.White));
        builder.setPiece(new King(60, Color.White));
        builder.setPiece(new Bishop(61, Color.White));
        builder.setPiece(new Knight(62, Color.White));
        builder.setPiece(new Rook(63, Color.White));
        // White will be the first to move
        builder.setMoveMaker(Color.White);
        // build the board
        return builder.build();
    }

    public Tile getTile(final int TileCoordinate) {
        return gameBoard.get(TileCoordinate);
    }

    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Color nextMoveMaker;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap<>();
        }

        /* Set the piece on the tile */
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePostition(), piece);
            return this;
        }

        /* Set the next move maker */
        public Builder setMoveMaker(final Color nexMoveMaker) {
            this.nextMoveMaker = nexMoveMaker;
            return this;
        }

        /* Create an immutable board based on the builder class */
        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }

}
