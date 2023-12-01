package SourceCode.Engine.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Pieces.King;
import SourceCode.Engine.Pieces.Piece;

/*Construct abstract class for Player */
public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> AllLegalMoves;
    protected final boolean isInCheck;

    Player (final Board board, final Collection<Move> PlayerLegals, final Collection<Move> OpponentMoves) {
        this.board = board;
        this.playerKing = constructKing();
        PlayerLegals.addAll(calculateKingCastle(PlayerLegals, OpponentMoves));
        this.AllLegalMoves = Collections.unmodifiableCollection(PlayerLegals);
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePostition(), OpponentMoves).isEmpty();
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.AllLegalMoves;
    }

    /*Check if the opponent can captures the piece on a specific position */
    protected static Collection<Move> calculateAttacksOnTile(int piecePostition, Collection<Move> OpponentMoves) {
        
        final List<Move> attackMoves = new ArrayList<>();

        for (final Move move: OpponentMoves) {
            if (piecePostition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        
        return ImmutableList.copyOf(attackMoves);
    }

    public boolean isMoveLegal(final Move move) {
        return this.AllLegalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckmate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStalemate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return false;
    }
    
    /*Establish the King from the remaining active pieces */
    private King constructKing() {

        for (final Piece piece: getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }

        throw new RuntimeException("Not a valid board!");

    }

    /*
    Return a new board if the current player make a move
     else return the same board
    */
    public MoveTransition makeMove(final Move move) {

        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.Illegal_Move);
        }
        /*Transit the board to the opponent */
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttack = Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePostition(), 
                                transitionBoard.currentPlayer().getLegalMoves());
        if (!kingAttack.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.Leaves_Player_In_Check);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.Done);
    }

    /*Check if the piece had an escaped move*/
    public boolean hasEscapeMoves() {
        
        for (final Move move: this.AllLegalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) return true;
        }

        return false;
    }

    public abstract Color getPlayerColor();
    public abstract Collection<Piece> getActivePieces();
    public abstract Player getOpponent(); 
    protected abstract Collection<Move> calculateKingCastle(Collection<Move> playerLegalMoves, Collection<Move> opponentLegalMoves);

}
