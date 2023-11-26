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

public class King extends Piece {
    
    private static final int[] PotentialMove = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(final int piecePosition, final Color pieceColor) {
        super(PieceType.King, piecePosition, pieceColor, true);
    }

    public King(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.King, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

    /*Mark the king as a string */
    @Override
    public String toString() {
        return PieceType.King.toString();
    }

    /*Establish a collection of legal moves the selected pawn could make */
    @Override
    public Collection<Move> LegalMove(Board board) {
        final List<Move> AllLegalMove = new ArrayList<>();
        /*Check all the potential valid move the selected king could make */
        for (final int CurrentCandidateOffset: PotentialMove) {
            final int candidateDestinationCoordinate = this.piecePosition + CurrentCandidateOffset;
            /*Check invalid move */
            if (isFirstColumnExclusion(this.piecePosition, CurrentCandidateOffset)) break;
            if (isEighthColumnExclusion(this.piecePosition, CurrentCandidateOffset)) break;
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

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.First_Column[currentPosition] && ((candidateOffset == -9) || (candidateOffset == -1) || (candidateOffset == 7));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Eighth_Column[currentPosition] && ((candidateOffset == -7) || (candidateOffset == 1) || (candidateOffset == 9));
    }

}
