package SourceCode.Engine.Player.AI;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Player.Player;

public class StandardBoardEvaluator implements BoardEvaluation {

    private static final int CHECK_BONUS = 50;
    private static final int CASTLED_BONUS = 60;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;

    @Override
    public int evaluate(final Board board, final int depth) {
        return scorePlayer(board, board.whitePlayer(), depth) - 
               scorePlayer(board, board.blackPlayer(), depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) + checkmate(player, depth) + check(player) + mobility(player) + castled(player);
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece: player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    } 

    private int checkmate(final Player player, final int depth) {
        return player.getOpponent().isInCheckmate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(final int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private int castled(final Player player) {
        return player.isCastled() ? CASTLED_BONUS : 0;
    }

}
