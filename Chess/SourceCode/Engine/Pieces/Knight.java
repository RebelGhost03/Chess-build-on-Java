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

/*Construct a knight piece */
public class Knight extends Piece{

    private final static int[] PotentialMove = { -17, -15, -10, -6, 6 , 10, 15, 17};

    public Knight(final int piecePosition, final Color pieceColor) {
        super(PieceType.Knight, piecePosition, pieceColor, true);
    }

    public Knight(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.Knight, piecePosition, pieceColor, isFirstMove);
    }
    
    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

    /*Mark the knight as a string */
    @Override
    public String toString() {
        return PieceType.Knight.toString();
    }

    /*Establish a collection of legal moves the selected knight could make */
    @Override
    public Collection<Move> LegalMove(Board board) {

        final List <Move> AllLegalMove = new ArrayList<>(); 

        for (final int CurrentCandidateOffset : PotentialMove) {
            final int candidateDestinationCoordinate = this.piecePosition + CurrentCandidateOffset;
            /*Check invalid move */
            if (isFirstColumnExclusion(this.piecePosition, CurrentCandidateOffset)) continue;
            if (isSecondColumnExclusion(this.piecePosition, CurrentCandidateOffset)) continue;
            if (isSeventhColumnExclusion(this.piecePosition, CurrentCandidateOffset)) continue;
            if (isEighthColumnExclusion(this.piecePosition, CurrentCandidateOffset)) continue;
            /*Check if the tile is a valid coordinate */
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
                }
            }
        }
        return Collections.unmodifiableList(AllLegalMove);
    }

    /*Check if the knight piece could make a illegal move on the 1st column*/
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.First_Column[currentPosition] && ((candidateOffset == -17) || (candidateOffset == -10)
        || (candidateOffset == 6) || (candidateOffset == 15));
    }

    /*Check if the knight piece could make a illegal move on the 2nd column*/
    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Second_Column[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    /*Check if the knight piece could make a illegal move on the 7th column*/
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Seventh_Column[currentPosition] && ((candidateOffset == -6) || (candidateOffset == 10));
    }

    /*Check if the knight piece could make a illegal move on the 8th column*/
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Eighth_Column[currentPosition] && ((candidateOffset == -15) || (candidateOffset == -6) 
        || (candidateOffset == 10) || (candidateOffset == 17));
    }

}
