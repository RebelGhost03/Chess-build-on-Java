package SourceCode.Engine;

import SourceCode.Engine.Player.BlackPlayer;
import SourceCode.Engine.Player.Player;
import SourceCode.Engine.Player.WhitePlayer;

/*Indicate the color of the selected piece */
public enum Color {

    White {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

    }, 
    Black {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;    
        }
    };

    public abstract int getDirection();
    public abstract int getOppositeDirection();
    
    /*Check the selected piece's color*/
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

}
