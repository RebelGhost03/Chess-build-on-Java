package SourceCode.Engine.Pieces;

import java.util.Collection;

import SourceCode.Engine.Color;
import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;

/*Construct an abstract piece */
public abstract class Piece {
    
    protected final int piecePosition;
    protected final Color pieceColor;
    protected final boolean isFirstMove;
    protected final PieceType pieceType;
    private final int cachedHashCode;

    Piece(final PieceType pieceType, final int piecePosition, final Color pieceColor, final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.piecePosition = piecePosition;
        this.pieceColor = pieceColor;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = calculateHashCode();
    }

    private int calculateHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceColor.hashCode();
        result = 31 * result + piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Piece)) {
            return false;
        }

        final Piece otherPiece = (Piece) other;
        return piecePosition == otherPiece.getPiecePostition() && pieceType == otherPiece.getPieceType() &&
               pieceColor == otherPiece.getPieceColor() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    /*Get this piece's postition */
    public int getPiecePostition() {
        return this.piecePosition;
    }

    /*public void setPiecePosition(final int piecePosition1, final int piecePosition2) {
        this.piecePosition = piecePosition1 * 8 + piecePosition2;
    }*/

    /*Get this piece's color */
    public Color getPieceColor() {
        return this.pieceColor;
    }

    /*List of all the legal move a piece can takes */
    public abstract Collection<Move> LegalMove(final Board board);

    /*Check if is the first time the piece moves */
    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    /*Get this piece's type */
    public PieceType getPieceType() {
        return this.pieceType;
    }

    /*Return a new piece with a new position */
    public abstract Piece movePiece(Move move);

    public enum PieceType {

        Pawn("P") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        Bishop("B") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        Knight("N") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        Rook("R") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        Queen("Q") {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        King("K") {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;

        PieceType(final String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();
    }
}
