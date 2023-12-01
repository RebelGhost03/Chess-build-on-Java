package SourceCode.Engine.Board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BoardUtility {

    public static final boolean[] First_Column = initColumn(0);
    public static final boolean[] Second_Column = initColumn(1);
    public static final boolean[] Seventh_Column = initColumn(6);
    public static final boolean[] Eighth_Column = initColumn(7);

    public static final boolean[] First_Row = initRow(0);
    public static final boolean[] Second_Row = initRow(8);
    public static final boolean[] Third_Row = initRow(16);
    public static final boolean[] Fourth_Row = initRow(24);
    public static final boolean[] Fifth_Row = initRow(32);
    public static final boolean[] Sixth_Row = initRow(40);
    public static final boolean[] Seventh_Row = initRow(48);
    public static final boolean[] Eighth_Row = initRow(56);

    public static final int Total_Tiles = 64;
    public static final int Tolal_Tile_Per_Row = 8;

    public static final String[] COORDINATE_TO_POSITION = initializeAlgebericNotation();
    public static final Map <String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinate();

    private BoardUtility() {
        throw new RuntimeException("Unable to instantiate board");
    }

    private static Map<String, Integer> initializePositionToCoordinate() {
        final Map<String, Integer> postitionToCoordinate = new HashMap<>();

        for (int i = 0; i < Total_Tiles; ++i) postitionToCoordinate.put(COORDINATE_TO_POSITION[i], i);

        return Collections.unmodifiableMap(postitionToCoordinate);
    }

    private static String[] initializeAlgebericNotation() {
        return new String[] {
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"};
    }

    /*Check if the destination tile is valid or invalid */
    public static boolean isValidTileCoordinate(final int TileCoordinate) {
        return TileCoordinate >= 0 && TileCoordinate < 64;
    }

    /*Mark a specific column */
    private static boolean[] initColumn(int columnNumber) {
        final boolean[] column = new boolean[Total_Tiles];

        do {
            column[columnNumber] = true;
            columnNumber += Tolal_Tile_Per_Row;
        } while (columnNumber < Total_Tiles);

        return column;
    }

    /*Mark a specific row */
    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[Total_Tiles];

        do {
            row[rowNumber] = true;
            ++rowNumber;
        } while (rowNumber % Tolal_Tile_Per_Row != 0);

        return row;
    }

    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return COORDINATE_TO_POSITION[coordinate];
    }
    
}
