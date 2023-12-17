package SourceCode.GUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

import SourceCode.Engine.Board.Move;
import SourceCode.Engine.Pieces.Piece;
import SourceCode.GUI.Table.MoveLog;

public class CapturedPiecePanel extends JPanel {

    private final JPanel northPanel;
    private final JPanel southPanel;
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension CAPTURED_PIECES_DIMENSION = new Dimension(40, 80);

    
    public CapturedPiecePanel() {
        super(new BorderLayout());
        setBackground(PANEL_COLOR);
        setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout(8, 4));
        this.southPanel = new JPanel(new GridLayout(8, 4));
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        add(this.northPanel, BorderLayout.NORTH);
        add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(CAPTURED_PIECES_DIMENSION);
    }

    public void redo(final MoveLog moveLog) {

        this.northPanel.removeAll();
        this.southPanel.removeAll();

        for (final Move move: moveLog.getMoves()) {
             if (move.isAttack()) {
                  final Piece capturedPiece = move.getAttackedPiece();
                  BufferedImage icon = null;
                  try {
                     String path = "Art/pieces1/" + capturedPiece.getPieceColor().toString().substring(0, 1) +
                            capturedPiece.getPieceType().toString() + ".gif";
                     icon = ImageIO.read(new File(path));
                  } catch (IOException e) {
                     e.printStackTrace();
                  }
                  if (capturedPiece.getPieceColor().isWhite()) {
                      southPanel.add(new JLabel(new ImageIcon(icon)));
                  } 
                  else if (capturedPiece.getPieceColor().isBlack()) {
                      northPanel.add(new JLabel(new ImageIcon(icon)));
                  }
             } 
        }
        validate();
    }

}
