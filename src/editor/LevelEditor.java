package editor;

import javax.swing.*;
import java.awt.*;

public class LevelEditor {
    private JPanel mainPanel;
    private JScrollPane levelScrollPane;
    private JScrollPane toolScrollPane;
    private JPanel levelPanel;

    private int gridSize = 16;
    private int levelWidth = 32;
    private int levelHeight = 24;
    private int levelPanelWidth = levelWidth * gridSize;
    private int levelPanelHeight = levelHeight * gridSize;
    private ForegroundLayer foregroundLayer = new ForegroundLayer(levelWidth, levelHeight);

    private IconLoader iconLoader = new IconLoader(gridSize);


    private void createUIComponents() {
        levelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLevel(g);
            }
        };
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, levelPanelWidth, levelPanelHeight);
        drawTiles(g);
        drawGrid(g);
    }

    private void drawTiles(Graphics g) {

        // test data
        foregroundLayer.setTile(4, 4, ForegroundTile.TEST_TILE);
        foregroundLayer.setTile(5, 4, ForegroundTile.TEST_TILE);
        foregroundLayer.setTile(6, 4, ForegroundTile.TEST_TILE);
        foregroundLayer.setTile(7, 4, ForegroundTile.TEST_TILE);
        foregroundLayer.setTile(6, 3, ForegroundTile.TEST_TILE);
        foregroundLayer.setTile(5, 3, ForegroundTile.TEST_TILE);

        // draw foreground layer
        for (int x = 0; x < levelWidth; x++) {
            for (int y = 0; y < levelHeight; y++) {
                ForegroundTile tile = foregroundLayer.getTile(x, y);
                if (tile.getPrimaryDisplayTileIcon() != null) {
                    int panelX = x * gridSize;
                    int panelY = y * gridSize;
                    Image iconImage = iconLoader.getImageForIcon(tile.getPrimaryDisplayTileIcon());
                    g.drawImage(iconImage, panelX, panelY, null);
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        int width = levelPanelWidth;
        int height = levelPanelHeight;

        g.setColor(Color.CYAN);

        // vertical lines
        for (int i = gridSize; i < width; i += gridSize) {
            g.drawLine(i, 0, i, height);
        }

        // horizontal lines
        for (int i = gridSize; i < height; i+= gridSize) {
            g.drawLine(0, i, width, i);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Level Editor");
        frame.setContentPane(new LevelEditor().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
