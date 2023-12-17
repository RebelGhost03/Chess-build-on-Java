package SourceCode.Engine.Player.AI;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;

public interface MoveStrategy {
    
    Move execute(Board board);

}
