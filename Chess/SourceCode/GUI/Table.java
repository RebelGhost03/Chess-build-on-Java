package SourceCode.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;

import SourceCode.Engine.Board.Board;
import SourceCode.Engine.Board.BoardUtility;
import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Board.Tile;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.Engine.Player.MoveTransition;

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

public class Table {

    private final JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private final CapturedPiecePanel capturedPiecePanel;
    private final MoveLog moveLog;

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

    private boolean checkHighlightLegalsMove;
    private boolean exited;

    public Table() {
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
        this.checkHighlightLegalsMove = true;
        this.gameFrame.add(this.capturedPiecePanel, BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }
    
    private JMenuBar generateMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener((e) -> { System.out.println("Open the PGN File!"); });
        fileMenu.add(openPGN);

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener((e) -> { System.exit(0); });
        fileMenu.add(exitMenuItem);

        return fileMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");

        final JCheckBoxMenuItem highlightCheckBox = new JCheckBoxMenuItem("Highlight legal move", true);
        highlightCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 checkHighlightLegalsMove = highlightCheckBox.isSelected();
            }
        });

        preferencesMenu.add(highlightCheckBox);

        return preferencesMenu;

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
                public void mouseClicked(final MouseEvent e) {
                    /*if (SwingUtilities.isRightMouseButton(e)) {
                        startingTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
                    } else 
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        if (startingTile == null) {
                            startingTile = chessBoard.getTile(tileID);
                            humanMovedPiece = startingTile.getPiece();
                            if (humanMovedPiece == null) startingTile = null; 
                        } else {
                            destinationTile = chessBoard.getTile(tileID);
                            //System.out.println(tileID);
                            final Move move = Move.MoveFactory.createMove(chessBoard, startingTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);

                            if (transition.getMoveStatus().isDone()) {
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }

                            startingTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                capturedPiecePanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                   }*/
                }

                @Override
                public void mousePressed(final MouseEvent e) {
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
                public void mouseExited(final MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if (humanMovedPiece == null) setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if (humanMovedPiece != null && destinationTile != null) {
                        final Move move = Move.MoveFactory.createMove(chessBoard, startingTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                        final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);

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
                             boardPanel.drawBoard(chessBoard);
                        }
                    });
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
}
