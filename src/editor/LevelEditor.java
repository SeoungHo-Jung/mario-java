package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Stack;

public class LevelEditor implements ActionListener {

    private static JFrame FRAME;

    private JPanel mainPanel;
    private JScrollPane levelScrollPane;
    private JScrollPane toolScrollPane;
    private JPanel levelPanel;
    private JPanel tilePalettePanel;
    private JPanel toolControlPanel;
    private JPanel selectedTilePreviewPanel;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenuItem newMenuItem;
    private JMenuItem quitMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem propertiesMenuItem;

    private final int gridSize = 16;
    private final int paletteColumns = 8;

    private final IconLoader iconLoader = new IconLoader(gridSize);

    private int levelWidth;
    private int levelHeight;
    private int levelPanelWidth;
    private int levelPanelHeight;
    private ForegroundLayer foregroundLayer;
    private ForegroundTile selectedTile = ForegroundTile.EMPTY_TILE;
    private boolean isGridEnabled = true;

    private Stack<EditorCommand> undoStack = new Stack<>();

    public LevelEditor() {
        levelPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                handleLevelPanelMouseEvent(e);
            }
        });
        tilePalettePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                handleTilePalettePanelMouseEvent(e);
            }
        });
    }

    public JPanel getLevelPanel() {
        return levelPanel;
    }

    public ForegroundLayer getForegroundLayer() {
        return foregroundLayer;
    }

    public int getLevelHeight() {
        return levelHeight;
    }

    public int getLevelWidth() {
        return levelWidth;
    }

    public void setLevelDimensions(int width, int height) {
        System.out.println("Set Level Dimensions " + width + " " + height);
        levelWidth = width;
        levelHeight = height;
        levelPanelWidth = levelWidth * gridSize;
        levelPanelHeight = levelHeight * gridSize;

        // TODO: Validate that no tiles are being deleted

        foregroundLayer = new ForegroundLayer(width, height, foregroundLayer);

        // Resize components & repaint
        levelPanel.setPreferredSize(new Dimension(levelPanelWidth, levelPanelHeight));
        JViewport viewport = levelScrollPane.getViewport();
        viewport.setViewSize(new Dimension(levelPanelWidth, levelPanelHeight));
        levelScrollPane.revalidate();
        levelScrollPane.repaint();
    }

    public void setForegroundLayer(ForegroundLayer foregroundLayer) {
        this.foregroundLayer = foregroundLayer;
    }

    private void createUIComponents() {
        levelPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLevel(g);
            }
        };

        tilePalettePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPalette(g);
            }
        };

        selectedTilePreviewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPreview(g);
            }
        };

        createMenuBar();
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        newMenuItem = new JMenuItem("New");
        newMenuItem.setActionCommand("new");
        newMenuItem.addActionListener(this);
        fileMenu.add(newMenuItem);

        fileMenu.addSeparator();

        quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setActionCommand("quit");
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.META_DOWN_MASK));
        quitMenuItem.addActionListener(this);
        fileMenu.add(quitMenuItem);

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setActionCommand("undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK));
        undoMenuItem.addActionListener(this);
        editMenu.add(undoMenuItem);

        editMenu.addSeparator();

        propertiesMenuItem = new JMenuItem("Properties");
        propertiesMenuItem.setActionCommand("properties");
        propertiesMenuItem.addActionListener(this);
        editMenu.add(propertiesMenuItem);

        FRAME.setJMenuBar(menuBar);
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, levelPanelWidth, levelPanelHeight);
        if (foregroundLayer != null) {
            drawTiles(g);
        }
        if (isGridEnabled) {
            drawGrid(g);
        }
    }

    private void drawTiles(Graphics g) {
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

    private void drawPalette(Graphics g) {
        final List<ForegroundTile> fgTiles = TileData.FOREGROUND_TILES;

        for (int i = 0; i < fgTiles.size(); i++) {
            ForegroundTile tile = fgTiles.get(i);
            Image iconImage = iconLoader.getImageForIcon(tile.getPrimaryDisplayTileIcon());
            int x = (i % paletteColumns) * gridSize;
            int y = (i / paletteColumns) * gridSize;
            g.drawImage(iconImage, x, y, null);
        }
    }

    private void drawPreview(Graphics g) {
        Image iconImage = iconLoader.getImageForIcon(selectedTile.getPrimaryDisplayTileIcon());
        g.drawImage(iconImage, 0, 0, gridSize * 2, gridSize * 2, null);
    }

    public void doCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
    }

    private void createNewLevel() {
        // TODO: Make this into an EditorCommand? Display save confirmation?
        final int defaultWidth = 10;
        final int defaultHeight = 10;
        setLevelDimensions(defaultWidth, defaultHeight);
    }

    private void handleLevelPanelMouseEvent(MouseEvent e) {
        int x = e.getX() / gridSize;
        int y = e.getY() / gridSize;
        if (x >= 0 && x < levelWidth && y >= 0 && y < levelHeight) {
            ForegroundTile oldTile = foregroundLayer.getTile(x, y);
            EditorCommand command = new ChangeForegroundTileCommand(x, y, selectedTile, oldTile, this);
            doCommand(command);
        }
    }

    private void handleTilePalettePanelMouseEvent(MouseEvent e) {
        List<ForegroundTile> fgTiles = TileData.FOREGROUND_TILES;
        int x = e.getX() / gridSize;
        int y = e.getY() / gridSize;
        int index = (y * paletteColumns) + x;
        if (index >= 0 && index < fgTiles.size()) {
            selectedTile = fgTiles.get(index);
            System.out.println("Selected tile " + selectedTile.getName());
        }
        selectedTilePreviewPanel.repaint();
    }

    private void handleNewRequested() {
        createNewLevel();
    }

    private void handleUndoRequested() {
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
        }
    }

    private void handleOpenPropertiesDialog() {
        PropertiesDialog dialog = new PropertiesDialog(this);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void handleQuitRequested() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "new" -> handleNewRequested();
            case "undo" -> handleUndoRequested();
            case "properties" -> handleOpenPropertiesDialog();
            case "quit" -> handleQuitRequested();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Level Editor");
        FRAME = frame;
        frame.setContentPane(new LevelEditor().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 300));
        frame.setPreferredSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
