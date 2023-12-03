package SourceCode.GUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.Move;
import SourceCode.GUI.Table.MoveLog;

public class GameHistoryPanel extends JPanel {

    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(130, 400);
    
    GameHistoryPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15); 
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveLog) {
        int currentRow = 0;
        this.model.clear();

        for (final Move move: moveLog.getMoves()) {
            final String moveText = move.toString();
            if (move.getMovedPiece().getPieceColor().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else if (move.getMovedPiece().getPieceColor().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                ++currentRow;
            }
        }

        if (moveLog.getMoves().size() > 0) {
            final Move lastMove = moveLog.getMoves().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();
            if (lastMove.getMovedPiece().getPieceColor().isWhite()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow, 0);
            } else if (lastMove.getMovedPiece().getPieceColor().isBlack()) {
                this.model.setValueAt(moveText + calculateCheckAndCheckmateHash(board), currentRow - 1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public String calculateCheckAndCheckmateHash(final Board board) {
        if (board.currentPlayer().isInCheckmate()) {
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel {

        private final List<Row> value;
        private static final String[] NAMES = {"White", "Black"};

        DataModel() {
            this.value = new ArrayList<>();
        }

        public void clear() {
            this.value.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.value == null) {
                return 0;
            }
            return this.value.size();
        }

        @Override
        public int getColumnCount() {
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Row currentRow = this.value.get(row);
            if (column == 0) {
                return currentRow.getWhiteMoves();
            } else if (column == 1) {
                return currentRow.getBlackMoves();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object Value, final int row, final int column) {
            final Row currentRow;
            if (this.value.size() <= row) {
                currentRow = new Row();
                this.value.add(currentRow);
            } else {
                currentRow = this.value.get(row);
            }

            if (column == 0) {
                currentRow.setWhiteMove((String) Value);
                fireTableRowsInserted(row, row);
            } else if (column == 1) {
                currentRow.setBlackMove((String) Value);
                fireTableCellUpdated(row, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column) {
            return Move.class;
        }

        @Override
        public String getColumnName(final int column) {
            return NAMES[column];
        }
    }

    private static class Row {

        private String whiteMoves;
        private String blackMoves;

        Row() {}

        public String getWhiteMoves() {
            return this.whiteMoves;
        }

        public String getBlackMoves() {
            return this.blackMoves;
        }

        public void setWhiteMove(final String move) {
            this.whiteMoves = move;
        }

        public void setBlackMove(final String move) {
            this.blackMoves = move;
        }
    }
}
