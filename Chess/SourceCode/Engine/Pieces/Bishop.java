package SourceCode.Engine.Pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.BoardUtility;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Tile;
import SourceCode.Engine.Board.Move.*;

/*Construct a bishop piece */
public class Bishop extends Piece {

    private final static int[] PotentialMoveVector = { -9, -7, 7, 9};
    
    public Bishop(final int piecePosition, final Color pieceColor) {
        super(PieceType.Bishop, piecePosition, pieceColor, true);
    }

    public Bishop(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.Bishop, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

    /*Mark the bishop as a string */
    @Override
    public String toString() {
        return PieceType.Bishop.toString();
    }

    /*Establish a collection of legal moves the selected bishop could make */
    @Override
    public Collection<Move> LegalMove(final Board board) {
        final List<Move> AllLegalMove = new ArrayList<>();

        for (final int CurrentCandidateOffset: PotentialMoveVector) {

            int candidateDestinationCoordinate = this.piecePosition;
            /*Check all the potential valid move the selected bishop could make */
            while (BoardUtility.isValidTileCoordinate(candidateDestinationCoordinate)) {
                /*Check invalid move */
                if (isFirstColumnExclusion(candidateDestinationCoordinate, CurrentCandidateOffset)) break;
                if (isEighthColumnExclusion(candidateDestinationCoordinate, CurrentCandidateOffset)) break;
                candidateDestinationCoordinate += CurrentCandidateOffset;
                if (BoardUtility.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    /*
                    If the destination tile is empty then add that move as a major move to the collection of legal moves 
                    Else add that move as an attack move to the collection of legal moves     
                    */
                    if (!candidateDestinationTile.IsOccupiled()) {
                        AllLegalMove.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    } else { 
                        final Piece pieceDestination = candidateDestinationTile.getPiece();
                        final Color pieceDestinationColor = pieceDestination.getPieceColor();
                        if (this.pieceColor != pieceDestinationColor) {
                            AllLegalMove.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceDestination));
                        }
                        break;
                    }
                }
            }

        }

        return Collections.unmodifiableList(AllLegalMove);
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.First_Column[currentPosition] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Eighth_Column[currentPosition] && (candidateOffset == -7 || candidateOffset == 9);
    }

}
