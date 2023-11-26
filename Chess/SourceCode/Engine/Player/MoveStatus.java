package SourceCode.Engine.Player;

public enum MoveStatus {

    Done {
        @Override
        public boolean isDone() {
            return true;
        }
    }, 
    Illegal_Move {
        @Override
        public boolean isDone() {
            return false;
        }
    }, 
    Leaves_Player_In_Check {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
