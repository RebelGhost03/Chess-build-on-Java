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

/*Construct a rook piece */
public class Rook extends Piece {

    private final static int[] PotentialMoveVector = { -8, -1, 1, 8};

    public Rook(final int piecePosition, final Color pieceColor) {
        super(PieceType.Rook, piecePosition, pieceColor, true);
    }

    public Rook(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.Rook, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

    /*Mark the rook as a string */
    @Override
    public String toString() {
        return PieceType.Rook.toString();
    }
    
    /*Establish a collection of legal moves the selected rook could make */
    @Override
    public Collection<Move> LegalMove(final Board board) {
        final List<Move> AllLegalMove = new ArrayList<>();
        for (final int CurrentCandidateOffset: PotentialMoveVector) {
            int candidateDestinationCoordinate = this.piecePosition;
            /*Check all the potential valid move the selected rook could make */
            while (BoardUtility.isValidTileCoordinate(candidateDestinationCoordinate)) {
                /*Check invalid move */
                if (isFirstColumnExclusion(this.piecePosition, CurrentCandidateOffset)) break;
                if (isEighthColumnExclusion(this.piecePosition, CurrentCandidateOffset)) break;
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
        return BoardUtility.First_Column[currentPosition] && candidateOffset == -1;
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtility.Eighth_Column[currentPosition] && candidateOffset == 1;
    }
}
