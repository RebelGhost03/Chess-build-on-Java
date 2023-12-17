package SourceCode.Engine.Player.AI;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Player.MoveTransition;

public class Minmax implements MoveStrategy{

    private final BoardEvaluation boardEvaluation;
    private final int searchDepth;

    public Minmax(final int searchDepth) {
        this.boardEvaluation = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    } 

    @Override
    public String toString() {
        return "Minmax";
    }

    @Override
    public Move execute(Board board) {
        
        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        int numMoves = board.currentPlayer().getLegalMoves().size();

        System.out.println(board.currentPlayer() + " Thinking with depth = " + this.searchDepth);

        for (final Move move: board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = board.currentPlayer().getPlayerColor().isWhite() ? 
                            min(moveTransition.getTransitionBoard(), this.searchDepth - 1) :
                            max(moveTransition.getTransitionBoard(), this.searchDepth - 1);
                
                if (board.currentPlayer().getPlayerColor().isWhite() && currentValue >= highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getPlayerColor().isBlack() && currentValue <= lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        final long executionTime = System.currentTimeMillis() - startTime;

        return bestMove;
    }

    public int min(final Board board, final int depth) {
        if (depth == 0) {
            return this.boardEvaluation.evaluate(board, depth);
        } 

        int lowestSeenValue = Integer.MAX_VALUE;

        for (final Move move: board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue <= lowestSeenValue) {
                    lowestSeenValue  = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private static boolean isEndGameBoard(final Board board) {
        return board.currentPlayer().isInCheckmate() || board.currentPlayer().isInStalemate();
    }

    public int max(final Board board, final int depth) {
        if (depth == 0 || isEndGameBoard(board)) {
            return this.boardEvaluation.evaluate(board, depth);
        } 

        int highestSeenValue = Integer.MIN_VALUE;

        for (final Move move: board.currentPlayer().getLegalMoves()) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if (currentValue >= highestSeenValue) {
                    highestSeenValue  = currentValue;
                }
            }
        }
        return highestSeenValue;
    }

}
