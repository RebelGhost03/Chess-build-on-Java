package SourceCode.Engine.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Tile;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Pieces.Rook;

public class WhitePlayer extends Player {

    public WhitePlayer(final Board board, final Collection<Move> whiteAllLegalMoves, final Collection<Move> blackAllLegalMoves) {
        super(board, whiteAllLegalMoves, blackAllLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Color getPlayerColor() {
        return Color.White;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    /*White side castles move*/
    @Override
    protected Collection<Move> calculateKingCastle(final Collection<Move> playerLegalMoves,
                                                   final Collection<Move> opponentLegalMoves) {

        final List<Move> kingCastle = new ArrayList<>(); 

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            /*White king side castle */
            if (!this.board.getTile(61).IsOccupiled() && !this.board.getTile(62).IsOccupiled()) {
                final Tile rookTile = this.board.getTile(63);

                if (rookTile.IsOccupiled() && rookTile.getPiece().isFirstMove()) {
                    if (Player.calculateAttacksOnTile(61, opponentLegalMoves).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentLegalMoves).isEmpty() &&
                        rookTile.getPiece().getPieceType().isRook()) {
                            kingCastle.add(new Move.KingSideCastleMove(this.board, this.playerKing, 62, 
                                                                       (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 61));
                    }
                }
            }

            /*White queen side castle */
            if (!this.board.getTile(59).IsOccupiled() && !this.board.getTile(58).IsOccupiled() &&
                !this.board.getTile(57).IsOccupiled()) {
                final Tile rookTile = this.board.getTile(56);

                if (rookTile.IsOccupiled() && rookTile.getPiece().isFirstMove() &&
                    Player.calculateAttacksOnTile(59, opponentLegalMoves).isEmpty() &&
                    Player.calculateAttacksOnTile(58, opponentLegalMoves).isEmpty() &&
                    rookTile.getPiece().getPieceType().isRook()) {
                       kingCastle.add(new Move.KingSideCastleMove(this.board, this.playerKing, 58, 
                                                                       (Rook) rookTile.getPiece(), rookTile.getTileCoordinate(), 59));
                }
            }
        }


        return Collections.unmodifiableList(kingCastle);
    }    
        
}
