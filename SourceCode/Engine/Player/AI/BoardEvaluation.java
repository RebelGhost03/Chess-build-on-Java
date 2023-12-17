package SourceCode.Engine.Player.AI;

import SourceCode.Engine.Board.Board;

public interface BoardEvaluation {
    
    int evaluate(Board board, int depth);

}
