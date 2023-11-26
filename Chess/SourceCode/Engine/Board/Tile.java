package SourceCode.Engine.Board;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

import SourceCode.Engine.Pieces.Piece;

/*Construct a Tile */
public abstract class Tile {
    
    protected final int TileCoordinate;

    /*Construct all the possible empty tiles */
    private static final Map <Integer, EmptyTile> emptyTileCached = createAllPossibleEmptyTiles();

    private static Map  <Integer, EmptyTile> createAllPossibleEmptyTiles() {

        final Map <Integer, EmptyTile> emptyTileMap = new HashMap<>();
        
        for (int i = 0; i < BoardUtility.Total_Tiles; ++i) {
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return ImmutableMap.copyOf(emptyTileMap);
    }

    /*Give either a cached empty tile or an occupiled tile */
    public static Tile createTile(final int TileCoordinate, final Piece piece) {
        return piece != null ? new OccupiledTile(TileCoordinate, piece) : emptyTileCached.get(TileCoordinate);
    }

    private Tile(final int TileCoordinate) {
        this.TileCoordinate = TileCoordinate;
    }

    public int getTileCoordinate() {
        return this.TileCoordinate;
    }

    /*Check if the tile is occupiled */
    public abstract boolean IsOccupiled();

    /*Place a piece on the tile*/
    public abstract Piece getPiece();

    /*Construct an empty tile */
    public static final class EmptyTile extends Tile {

        private EmptyTile(final int Coordinate) {
            super(Coordinate);
        }

        @Override
        public boolean IsOccupiled() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    /*Construct an occupiled tile */
    public static final class OccupiledTile extends Tile {

        private final Piece pieceOnTile;

        private OccupiledTile(int TileCoordinate, final Piece pieceOnTile) {
            super(TileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean IsOccupiled() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceColor().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }
    }

}