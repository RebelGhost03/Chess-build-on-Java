package SourceCode.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.BoardUtility;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Tile;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Player.MoveTransition;
import SourceCode.Engine.Player.AI.Minmax;
import SourceCode.Engine.Player.AI.MoveStrategy;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

public class Table extends Observable {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final CapturedPiecePanel capturedPiecePanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private static Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private static Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 350);
    private static Dimension TILE_PANEL_DIMENSION = new Dimension(10, 10);
    private Color lightTileColor = Color.decode("#FFFACD");
    private Color darkTileColor = Color.decode("#593E1A");
    private static String defaultPieceImagesPath = "Art/Pieces1/";

    private Board chessBoard;
    private Tile startingTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private Move computerMove;
    private List<Board> undoChessBoard;
    private List<Move> undoMove;

    private boolean checkHighlightLegalsMove;
    private boolean exited;

    private static final Table INSTANCE = new Table();

    private Table() {
        this.undoChessBoard = new ArrayList<>();
        this.undoMove = new ArrayList<>();
        this.gameFrame = new JFrame("MainChess");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = generateMenuBar(); 
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.capturedPiecePanel = new CapturedPiecePanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.checkHighlightLegalsMove = false;
        this.gameFrame.add(this.capturedPiecePanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        center(this.gameFrame);
        this.gameFrame.setVisible(true);
    }

    public void show() {
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(chessBoard, Table.get().getMoveLog());
        Table.get().getCapturedPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    public static Table get() {
        return INSTANCE;
    }

    private GameSetup getGameSetup() {
        return this.gameSetup;
    }

    private Board getGameBoard() {
        return this.chessBoard;
    }

    private void updateGameBoard(final Board board) {
        this.chessBoard = board;
    }

    private void updateComputerMove(final Move move) {
        this.computerMove = move;
    }
    
    private MoveLog getMoveLog() {
        return this.moveLog;
    }

    private BoardPanel getBoardPanel() {
        return this.boardPanel;
    }

    private GameHistoryPanel getGameHistoryPanel() {
        return this.gameHistoryPanel;
    }

    private CapturedPiecePanel getCapturedPiecesPanel() {
        return this.capturedPiecePanel;
    }

    public void addUndoBoard(final Board board) {
        undoChessBoard.add(board);
    }

    public void addUndoMove(final Move move) {
        undoMove.add(move);
    }

    private static void center(final JFrame frame) {
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = frame.getSize().width;
        final int h = frame.getSize().height;
        final int x = (dim.width - w) / 2;
        final int y = (dim.height - h) / 2;
        frame.setLocation(x, y);
    }

    private JMenuBar generateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createOptionMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener((e) -> {Undo(); });
        fileMenu.add(undoItem);

        fileMenu.addSeparator();

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener((e) -> { System.exit(0); });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");

        final JCheckBoxMenuItem highlightCheckBox = new JCheckBoxMenuItem("Highlight legal move", false);
        highlightCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 checkHighlightLegalsMove = highlightCheckBox.isSelected();
            }
        });

        preferencesMenu.add(highlightCheckBox);

        return preferencesMenu;

    }

    private JMenu createOptionMenu() {
        final JMenu optionMenu = new JMenu("Options");

        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionMenu.add(setupGameMenuItem);
        return optionMenu;
    }

    private void setupUpdate(final GameSetup gameSetup) {
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer {
        @Override
        public void update(final Observable o, final Object arg) {
            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().currentPlayer()) &&
                !Table.get().getGameBoard().currentPlayer().isInCheckmate() &&
                !Table.get().getGameBoard().currentPlayer().isInStalemate()) {
                System.out.println(Table.get().getGameBoard().currentPlayer() + " is set to AI, thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if (Table.get().getGameBoard().currentPlayer().isInCheckmate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in checkmate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (Table.get().getGameBoard().currentPlayer().isInStalemate()) {
                JOptionPane.showMessageDialog(Table.get().getBoardPanel(),
                        "Game Over: Player " + Table.get().getGameBoard().currentPlayer() + " is in stalemate!", "Game Over",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank() {}

        @Override
        protected Move doInBackground() throws Exception {
            final MoveStrategy minmax = new Minmax(Table.get().getGameSetup().depthSetting);
            final Move bestMove = minmax.execute(Table.get().getGameBoard());
            return bestMove;
        }

        @Override
        public void done() {
            try {
                Table.get().addUndoBoard(Table.get().getGameBoard());
                final Move bestMove = get();
                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().currentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getCapturedPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
                Table.get().addUndoMove(bestMove);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private class BoardPanel extends JPanel {

        final List<TilePanel> boardTiles;

        BoardPanel() {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();

            for (int i = 0; i < BoardUtility.Total_Tiles; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }

            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board) {
            removeAll();
            for (final TilePanel tilePanel: boardTiles) {
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    private class TilePanel extends JPanel {
        
        private final int tileID;

        TilePanel(final BoardPanel boardPanel, final int tileID) {
            super(new GridBagLayout());
            this.tileID = tileID;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignPieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {}

                @Override
                public void mousePressed(final MouseEvent e) {
                    if ((!gameSetup.isAIPlayer(chessBoard.currentPlayer()) && 
                        !gameSetup.isAIPlayer(chessBoard.currentPlayer().getOpponent()))|| 
                        (!gameSetup.isAIPlayer(chessBoard.currentPlayer()) &&
                        gameSetup.isAIPlayer(chessBoard.currentPlayer().getOpponent()))) {
                        if (SwingUtilities.isRightMouseButton(e)) {
                            startingTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        } else 
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (startingTile == null) {
                                startingTile = chessBoard.getTile(tileID);
                                humanMovedPiece = startingTile.getPiece();
                                if (humanMovedPiece == null) startingTile = null; 
                            } 
                        }
                    }
                }

                @Override
                public void mouseEntered(final MouseEvent e) {
                    destinationTile = chessBoard.getTile(tileID);
                    if (humanMovedPiece != null) {
                    setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(defaultPieceImagesPath + 
                                                            humanMovedPiece.getPieceColor().toString().substring(0, 1) + 
                                                            humanMovedPiece.toString() + ".gif").getImage(),
                                                            new Point(0,0),"custom cursor"));
                    }
                    else setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                @Override   
                public void mouseExited(final MouseEvent e) {}

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if ((!gameSetup.isAIPlayer(chessBoard.currentPlayer()) && 
                        !gameSetup.isAIPlayer(chessBoard.currentPlayer().getOpponent()))|| 
                        (!gameSetup.isAIPlayer(chessBoard.currentPlayer()) &&
                        gameSetup.isAIPlayer(chessBoard.currentPlayer().getOpponent()))) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        if (humanMovedPiece == null) setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        if (humanMovedPiece != null && destinationTile != null) {
                            final Move move = Move.MoveFactory.createMove(chessBoard, startingTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);

                            undoChessBoard.add(chessBoard);
                            undoMove.add(move);

                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }

                            startingTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                            exited = true;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                capturedPiecePanel.redo(moveLog);
                                if (gameSetup.isAIPlayer(chessBoard.currentPlayer())) {
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                }
                }
            });

            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(final MouseEvent e) {
                    if (humanMovedPiece != null) {
                        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(defaultPieceImagesPath + 
                                                            humanMovedPiece.getPieceColor().toString().substring(0, 1) + 
                                                            humanMovedPiece.toString() + ".gif").getImage(),
                                                            new Point(0,0),"custom cursor"));
                        removePieceIcon(chessBoard);
                    }
                }

                @Override
                public void mouseMoved(final MouseEvent e) {
                    if (exited) {
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        exited = false;
                    }
                }

            });

            validate();
        }

        public void drawTile(final Board board) {
            assignTileColor();
            assignPieceIcon(board);
            highlightLegalsMove(board);
            validate();
            repaint();
        }

        private void assignPieceIcon(final Board board) { 
            this.removeAll();

            if (board.getTile(this.tileID).IsOccupiled()) {
                try {
                    final BufferedImage image = ImageIO.read(new File(defaultPieceImagesPath + 
                                                            board.getTile(this.tileID).getPiece().getPieceColor().toString().substring(0, 1) + 
                                                            board.getTile(this.tileID).getPiece().toString() + ".gif"));
                    add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void removePieceIcon(final Board board) { 
            this.removeAll();
            validate();
            repaint();
        }

        private void assignTileColor() {
            if (BoardUtility.First_Row[this.tileID] ||
                BoardUtility.Third_Row[this.tileID] ||
                BoardUtility.Fifth_Row[this.tileID] ||
                BoardUtility.Seventh_Row[this.tileID]) {
                setBackground(this.tileID % 2 == 0 ? lightTileColor : darkTileColor);
            } else if(BoardUtility.Second_Row[this.tileID] ||
                      BoardUtility.Fourth_Row[this.tileID] ||
                      BoardUtility.Sixth_Row[this.tileID]  ||
                      BoardUtility.Eighth_Row[this.tileID]) {
                setBackground(this.tileID % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }

        public void highlightLegalsMove(final Board board) {
            if (checkHighlightLegalsMove) {
                for (final Move move: pieceLegalMoves(board)) {
                    if (move.getDestinationCoordinate() == this.tileID) {
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File("Art/Misc/green_dot.png")))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        private Collection<Move> pieceLegalMoves(final Board board) {
            if (humanMovedPiece != null && humanMovedPiece.getPieceColor() == board.currentPlayer().getPlayerColor()) {
                return humanMovedPiece.LegalMove(board);
            }

            return Collections.emptyList();
        }
    }

    public void Undo() {
        if (undoChessBoard.size() == 1) gameHistoryPanel.redo(undoChessBoard.get(0), moveLog);
        if (!undoChessBoard.isEmpty()) {
            moveLog.removeMove(undoMove.get(undoMove.size() - 1));
            gameHistoryPanel.redo(undoChessBoard.get(undoChessBoard.size() - 1), moveLog);
            capturedPiecePanel.redo(moveLog);
            chessBoard = undoChessBoard.get(undoChessBoard.size() - 1);
            boardPanel.drawBoard(chessBoard);
            undoChessBoard.remove(undoChessBoard.size() - 1);
            undoMove.remove(undoMove.size() - 1);
            startingTile = null;
            destinationTile = null;
            humanMovedPiece = null;
        }
    }

    public static class MoveLog {

        private final List<Move> moves;

        MoveLog() {
            this.moves = new ArrayList<>();
        }

        public List<Move> getMoves() {
            return this.moves;
        }

        public void addMove(final Move move) {
            this.moves.add(move);
        }

        public int size() {
            return this.moves.size();
        }

        public void clear() {
            this.moves.clear();
        }

        public Move removeMove(int index) {
            return this.moves.remove(index);
        }

        public boolean removeMove(final Move move) {
            return this.moves.remove(move);
        }
    }

    public enum PlayerType {
        HUMAN,
        COMPUTER
    }
}
