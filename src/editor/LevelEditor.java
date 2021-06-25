package editor;

import javax.swing.*;
import java.awt.*;

public class LevelEditor {
    private JPanel mainPanel;
    private JScrollPane levelScrollPane;
    private JScrollPane toolScrollPane;
    private JPanel levelPanel;

    private int gridSize = 16;
    private int levelWidth = 800;
    private int levelHeight = 800;

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
        g.fillRect(0, 0, levelWidth, levelHeight);
        drawTiles(g);
        drawGrid(g);
    }

    private void drawTiles(Graphics g) {

    }

    private void drawGrid(Graphics g) {
        int width = levelWidth;
        int height = levelHeight;

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
