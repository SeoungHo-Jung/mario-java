package samj.mario.editor;

import samj.mario.editor.data.Tile;
import samj.mario.editor.io.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

import static samj.mario.editor.LevelEditor.GRID_SIZE;
import static samj.mario.editor.LevelEditor.PALETTE_COLUMNS;

public class PaletteTab {

    private JPanel iconPanel;
    private JScrollPane scrollPane;
    private final List<Tile> tiles;
    private final IconLoader iconLoader;
    private final Consumer<Tile> tileSelectedHandler;

    public PaletteTab(List<Tile> tiles, IconLoader iconLoader, Consumer<Tile> tileSelectedHandler) {
        this.tiles = tiles;
        this.iconLoader = iconLoader;
        this.tileSelectedHandler = tileSelectedHandler;
        $$$setupUI$$$();
        iconPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                handleTilePalettePanelMouseEvent(e);
            }
        });
    }

    private void createUIComponents() {
        iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPalette(g);
            }
        };
    }

    private void drawPalette(Graphics g) {
        for (int i = 0; i < tiles.size(); i++) {
            Tile tile = tiles.get(i);
            Image primaryIconImage = iconLoader.getImageForIcon(tile.getPrimaryDisplayIcon());
            Image secondaryIconImage = iconLoader.getImageForIcon(tile.getSecondaryDisplayIcon());
            int x = (i % PALETTE_COLUMNS) * GRID_SIZE;
            int y = (i / PALETTE_COLUMNS) * GRID_SIZE;
            g.drawImage(primaryIconImage, x, y, null);
            g.drawImage(secondaryIconImage, x, y, null);
        }
    }

    private void handleTilePalettePanelMouseEvent(MouseEvent e) {
        int x = e.getX() / GRID_SIZE;
        int y = e.getY() / GRID_SIZE;
        int index = (y * PALETTE_COLUMNS) + x;
        if (index >= 0 && index < tiles.size()) {
            tileSelectedHandler.accept(tiles.get(index));
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(iconPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return scrollPane;
    }

}
