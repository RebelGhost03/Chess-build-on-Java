package SourceCode.Engine.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;

import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Tile;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Pieces.Rook;

public class BlackPlayer extends Player {

    public BlackPlayer(final Board board, final Collection<Move> whiteAllLegalMoves, final Collection<Move> blackAllLegalMoves) {
        super(board, whiteAllLegalMoves, blackAllLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Color getPlayerColor() {
        return Color.Black;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastle(final Collection<Move> playerLegalMoves,
                                                   final Collection<Move> opponentLegalMoves) {
        final List<Move> kingCastle = new ArrayList<>(); 

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            /*Black king side castle */
            if (!this.board.getTile(5).IsOccupiled()  && !this.board.getTile(6).IsOccupiled()) {
                final Tile rookTile = this.board.getTile(7);

                if (rookTile.IsOccupiled() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(5, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) {
                            kingCastle.add(new Move.KingSideCastleMove(this.board, this.playerKing, 6, 
                                                                       (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 5));
                    }
                }
            }

            /*Black queen side castle */
            if (!this.board.getTile(1).IsOccupiled() && !this.board.getTile(2).IsOccupiled() &&
                !this.board.getTile(3).IsOccupiled()) {
                final Tile rookTile = this.board.getTile(0);

                if (rookTile.IsOccupiled() && rookTile.getPiece().isFirstMove() && 
                    Player.calculateAttacksOnTile(2, opponentLegalMoves).isEmpty() &&
                    Player.calculateAttacksOnTile(3, opponentLegalMoves).isEmpty() &&
                    rookTile.getPiece().getPieceType().isRook()) {
                        kingCastle.add(new Move.KingSideCastleMove(this.board, this.playerKing, 2, 
                                                                   (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 3));
                }
            }
        }

        return ImmutableList.copyOf(kingCastle);
    }

}
