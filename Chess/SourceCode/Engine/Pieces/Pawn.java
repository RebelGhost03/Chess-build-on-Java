package SourceCode.Engine.Pieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.BoardUtility;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Move.*;

public class Pawn extends Piece {
    
    private static final int[] potentialMajorMove = {8, 7, 9, 16};

    public Pawn(final int piecePosition, final Color pieceColor) {
        super(PieceType.Pawn, piecePosition, pieceColor, true);
    }

    public Pawn(final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        super(PieceType.Pawn, piecePosition, pieceColor, isFirstMove);
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getMovedPiece().getPieceColor());
    }

    /*Mark the pawn as a string */
    @Override
    public String toString() {
        return PieceType.Pawn.toString();
    }

    @Override
    /*Establish a collection of legal moves the selected pawn could make */
    public Collection<Move> LegalMove(final Board board) {

        final List<Move> AllLegalMove = new ArrayList<>();

        for (final int CurrentCandidateOffset: potentialMajorMove) {
            final int candidateDestinationCoordinate = this.piecePosition + (this.getPieceColor().getDirection() * CurrentCandidateOffset);
            /*Check the valid move the selected pawn could make */
            if (!BoardUtility.isValidTileCoordinate(candidateDestinationCoordinate)) continue;
            /*If the destination tile is empty then add that move as a major move to the collection of legal moves*/  
            if (CurrentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).IsOccupiled()) {
                AllLegalMove.add(new PawnMove(board, this, candidateDestinationCoordinate));
            } else 
            /*If is itthe first time a pawn moves, it has the additional option of advancing two squares, 
            provided that both squares are vacant */
            if (CurrentCandidateOffset == 16 && this.isFirstMove() && 
               ((BoardUtility.Second_Row[this.piecePosition] && this.getPieceColor().isBlack()) ||
               (BoardUtility.Seventh_Row[this.piecePosition] && this.getPieceColor().isWhite()))) {
                    /*Check if the 2 front tiles infront of the selected pawn is empty or not */
                    final int behindCandidateDestinationCoordinate = this.piecePosition + (this.getPieceColor().getDirection() * 8);
                    if (!board.getTile(behindCandidateDestinationCoordinate).IsOccupiled() &&
                        !board.getTile(candidateDestinationCoordinate).IsOccupiled()) {
                            AllLegalMove.add(new PawnJump(board, this, candidateDestinationCoordinate));
                        }
            } else
            /*
            A pawn captures by moving diagonally forward one square to the right 
            Also check the validity of the capture move
            */
            if (CurrentCandidateOffset == 7 &&
               !((BoardUtility.First_Column[this.piecePosition] && this.pieceColor.isBlack()) ||
                 (BoardUtility.Eighth_Column[this.piecePosition] && this.pieceColor.isWhite()))) {
                    if (board.getTile(candidateDestinationCoordinate).IsOccupiled()) {

                        final Piece pieceOnDestination = board.getTile(candidateDestinationCoordinate).getPiece();

                        if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                            AllLegalMove.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnDestination));
                        }
                    /*Check if is possible to do an en passant capture move*/
                    } else if (board.getEnPassantPawn() != null) {
                        if (board.getEnPassantPawn().getPiecePostition() == (this.piecePosition + this.pieceColor.getOppositeDirection())) {
                            final Piece pieceOnDestination = board.getEnPassantPawn();
                            if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                                  AllLegalMove.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnDestination));
                            }
                        } 
                    }
            } else 
            /*
            A pawn captures by moving diagonally forward one square to the left 
            Also check the validity of the capture move
            */
            if (CurrentCandidateOffset == 9 &&
               !((BoardUtility.Eighth_Column[this.piecePosition] && this.pieceColor.isBlack()) ||
                 (BoardUtility.First_Column[this.piecePosition] && this.pieceColor.isWhite()))) {
                    if (board.getTile(candidateDestinationCoordinate).IsOccupiled()) {

                        final Piece pieceOnDestination = board.getTile(candidateDestinationCoordinate).getPiece();

                        if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                            AllLegalMove.add(new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnDestination));
                        }
                    /*Check if is possible to do an en passant capture move*/
                    } else if (board.getEnPassantPawn() != null) {
                        if (board.getEnPassantPawn().getPiecePostition() == (this.piecePosition - this.pieceColor.getOppositeDirection())) {
                            final Piece pieceOnDestination = board.getEnPassantPawn();
                            if (this.pieceColor != pieceOnDestination.getPieceColor()) {
                                  AllLegalMove.add(new PawnEnPassantAttackMove(board, this, candidateDestinationCoordinate, pieceOnDestination));
                            }
                        } 
                    }
            }
        }
        return Collections.unmodifiableList(AllLegalMove);
    }
}
