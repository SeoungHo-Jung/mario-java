package samj.mario.editor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import samj.mario.editor.command.*;
import samj.mario.editor.data.*;
import samj.mario.editor.io.FileIO;
import samj.mario.editor.io.IconLoader;
import samj.mario.editor.io.JsonLevelFormat;
import samj.mario.editor.io.LevelFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import static samj.mario.editor.data.Tile.EMPTY_TILE;
import static samj.mario.editor.data.TileData.TILE_DEFINITIONS;

public class LevelEditor implements ActionListener {

    private static JFrame FRAME;

    private JPanel mainPanel;
    private JScrollPane levelScrollPane;
    private JPanel levelPanel;
    private JPanel toolControlPanel;
    private JPanel selectedTilePreviewPanel;
    private JScrollPane tilePaletteScrollPane;
    private JPanel selectedTilePanel;
    private JButton selectButton;
    private JButton drawButton;
    private JButton eraseButton;
    private JPanel selectedTileAttributesPanel;
    private JPanel tilePalettePanel;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu viewMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem quitMenuItem;
    private JMenuItem undoMenuItem;
    private JMenuItem propertiesMenuItem;
    private JCheckBoxMenuItem gridMenuItem;
    private JCheckBoxMenuItem overlayMenuItem;

    public static final int GRID_SIZE = 16;
    public static final int PALETTE_COLUMNS = 8;

    private Tile getSelectedGridTile() {
        return level.getTileMatrix().getTile(selectedGridTileX, selectedGridTileY);
    }

    public void setSelectedGridTile(int selectedGridTileX, int selectedGridTileY) {
        this.selectedGridTileX = selectedGridTileX;
        this.selectedGridTileY = selectedGridTileY;
        repaintLevel();
    }

    public void setCurrentMode(EditorMode mode) {
        this.currentMode = mode;
        selectButton.setSelected(mode == EditorMode.SELECT);
        drawButton.setSelected(mode == EditorMode.DRAW);
        eraseButton.setSelected(mode == EditorMode.ERASE);
        repaintLevel();
    }

    public enum EditorMode {
        SELECT,
        DRAW,
        ERASE
    }

    private final LevelFormat levelFormat = new JsonLevelFormat();
    private final FileIO fileIO = new FileIO(levelFormat);
    private final IconLoader iconLoader = new IconLoader(GRID_SIZE);

    private EditorMode currentMode = EditorMode.SELECT;
    private int levelPanelWidth;
    private int levelPanelHeight;
    private Level level;
    private int selectedGridTileX;
    private int selectedGridTileY;
    private Tile selectedPaletteTile = EMPTY_TILE; // TODO: Should this be the default?
    private boolean isGridEnabled = true;
    private boolean isOverlayEnabled = true;

    private final Stack<EditorCommand> undoStack = new Stack<>();

    public LevelEditor() {
        $$$setupUI$$$();
        LevelEditor levelEditor = this;
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
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMode != EditorMode.SELECT) {
                    EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.SELECT, levelEditor);
                    doCommand(command);
                }
            }
        });
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMode != EditorMode.DRAW) {
                    EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.DRAW, levelEditor);
                    doCommand(command);
                }
            }
        });
        eraseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMode != EditorMode.ERASE) {
                    EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.ERASE, levelEditor);
                    doCommand(command);
                }
            }
        });

        // Update display to reflect the default mode
        setCurrentMode(currentMode);

        // By default, create an empty level on startup
        createNewLevel();
    }

    public JPanel getLevelPanel() {
        return levelPanel;
    }

    public Level getLevel() {
        return level;
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
                drawTilePalette(g);
            }
        };
        Dimension tilePanelDimensions = new Dimension(PALETTE_COLUMNS * GRID_SIZE, (TILE_DEFINITIONS.size() / PALETTE_COLUMNS) * GRID_SIZE);
        tilePalettePanel.setMinimumSize(tilePanelDimensions);
        tilePalettePanel.setPreferredSize(tilePanelDimensions);
        tilePalettePanel.setMaximumSize(tilePanelDimensions);
        tilePalettePanel.setBorder(BorderFactory.createLoweredSoftBevelBorder());

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

        openMenuItem = new JMenuItem("Open");
        openMenuItem.setActionCommand("open");
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);

        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setActionCommand("save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.META_DOWN_MASK));
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);

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

        viewMenu = new JMenu("View");
        menuBar.add(viewMenu);

        gridMenuItem = new JCheckBoxMenuItem("Show Grid");
        gridMenuItem.setActionCommand("grid-toggle");
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.META_DOWN_MASK));
        gridMenuItem.addActionListener(this);
        gridMenuItem.setSelected(isGridEnabled);
        viewMenu.add(gridMenuItem);

        overlayMenuItem = new JCheckBoxMenuItem("Show Tile Icons");
        overlayMenuItem.setActionCommand("overlay-toggle");
        overlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.META_DOWN_MASK));
        overlayMenuItem.addActionListener(this);
        overlayMenuItem.setSelected(isOverlayEnabled);
        viewMenu.add(overlayMenuItem);

        FRAME.setJMenuBar(menuBar);
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, levelPanelWidth, levelPanelHeight);
        if (level != null) {
            drawTiles(g);
        }
        if (isGridEnabled) {
            drawGrid(g);
        }
        if (currentMode == EditorMode.SELECT) {
            drawSelectionBox(g);
        }
    }

    private void drawTiles(Graphics g) {
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                Tile tile = level.getTileMatrix().getTile(x, y);
                if (tile.getPrimaryDisplayIcon() != null) {
                    int panelX = x * GRID_SIZE;
                    int panelY = y * GRID_SIZE;
                    Image primaryIconImage = iconLoader.getImageForIcon(tile.getPrimaryDisplayIcon());
                    g.drawImage(primaryIconImage, panelX, panelY, null);
                    if (isOverlayEnabled) {
                        Image secondaryIconImage = iconLoader.getImageForIcon(tile.getSecondaryDisplayIcon());
                        g.drawImage(secondaryIconImage, panelX, panelY, null);
                    }
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        int width = levelPanelWidth;
        int height = levelPanelHeight;

        g.setColor(Color.CYAN);

        // vertical lines
        for (int i = GRID_SIZE; i < width; i += GRID_SIZE) {
            g.drawLine(i, 0, i, height);
        }

        // horizontal lines
        for (int i = GRID_SIZE; i < height; i += GRID_SIZE) {
            g.drawLine(0, i, width, i);
        }
    }

    private void drawSelectionBox(Graphics g) {
        int x = selectedGridTileX * GRID_SIZE;
        int y = selectedGridTileY * GRID_SIZE;

        g.setColor(Color.RED);

        // draw a square, upper left at x,y
        g.drawLine(x, y, x + GRID_SIZE, y);
        g.drawLine(x, y, x, y + GRID_SIZE);
        g.drawLine(x + GRID_SIZE, y, x + GRID_SIZE, y + GRID_SIZE);
        g.drawLine(x, y + GRID_SIZE, x + GRID_SIZE, y + GRID_SIZE);
    }

    private void drawTilePalette(Graphics g) {
        for (int i = 0; i < TILE_DEFINITIONS.size(); i++) {
            TileDefinition tileDef = TILE_DEFINITIONS.get(i);
            Tile tile = tileDef.prototype;
            Image primaryIconImage = iconLoader.getImageForIcon(tile.getPrimaryDisplayIcon());
            int x = (i % PALETTE_COLUMNS) * GRID_SIZE;
            int y = (i / PALETTE_COLUMNS) * GRID_SIZE;
            g.drawImage(primaryIconImage, x, y, null);
        }
    }

    private void drawPreview(Graphics g) {
        Tile selectedGridTile = getSelectedGridTile();
        Image primaryIcon = iconLoader.getImageForIcon(selectedGridTile.getPrimaryDisplayIcon());
        g.drawImage(primaryIcon, 0, 0, GRID_SIZE * 2, GRID_SIZE * 2, null);
    }

    public void doCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
    }

    private void createNewLevel() {
        final int width = 16;
        final int height = 16;

        level = new Level();
        level.setDimensions(width, height);
        level.setTileMatrix(new TileMatrix(width, height));
        levelPanelWidth = width * GRID_SIZE;
        levelPanelHeight = height * GRID_SIZE;

        undoStack.clear();
        repaintLevel();
    }

    private void loadExistingLevel(Level level) {
        this.level = level;
        levelPanelWidth = level.getWidth() * GRID_SIZE;
        levelPanelHeight = level.getHeight() * GRID_SIZE;

        undoStack.clear();
        repaintLevel();
    }

    public void changeLevelDimensions(int width, int height) {
        level.setDimensions(width, height);
        levelPanelWidth = width * GRID_SIZE;
        levelPanelHeight = height * GRID_SIZE;

        // TODO: Validate that no tiles are being deleted

        level.setTileMatrix(new TileMatrix(width, height, level.getTileMatrix()));

        repaintLevel();
    }

    public void repaintLevel() {
        // Resize components & repaint
        levelPanel.setPreferredSize(new Dimension(levelPanelWidth, levelPanelHeight));
        JViewport viewport = levelScrollPane.getViewport();
        viewport.setViewSize(new Dimension(levelPanelWidth, levelPanelHeight));
        levelScrollPane.revalidate();
        levelScrollPane.repaint();
        selectedTilePreviewPanel.repaint();
    }

    private boolean getDialogConfirmation() {
        if (undoStack.isEmpty()) {
            // this means the user hasn't done anything yet.
            return true;
        }

        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }

    private void handleLevelPanelMouseEvent(MouseEvent e) {
        int x = e.getX() / GRID_SIZE;
        int y = e.getY() / GRID_SIZE;
        if (x >= 0 && x < level.getWidth() && y >= 0 && y < level.getHeight()) {
            switch (currentMode) {
                case SELECT -> {
                    EditorCommand command = new SelectGridTileCommand(selectedGridTileX, selectedGridTileY, x, y, this);
                    doCommand(command);
                }
                case DRAW -> {
                    Tile oldTile = level.getTileMatrix().getTile(x, y);
                    EditorCommand command = new ChangeTileCommand(x, y, selectedPaletteTile, oldTile, this);
                    doCommand(command);
                }
                case ERASE -> {
                    Tile oldTile = level.getTileMatrix().getTile(x, y);
                    EditorCommand command = new EraseTileCommand(x, y, oldTile, this);
                    doCommand(command);
                }
            }
        }
    }

    private void handleTilePalettePanelMouseEvent(MouseEvent e) {
        int x = e.getX() / GRID_SIZE;
        int y = e.getY() / GRID_SIZE;
        int index = (y * PALETTE_COLUMNS) + x;
        if (index >= 0 && index < TILE_DEFINITIONS.size()) {
            selectedPaletteTile = TILE_DEFINITIONS.get(index).prototype;
        }
    }

    private void handleNewRequested() {
        if (getDialogConfirmation()) {
            createNewLevel();
        }
    }

    private void handleOpenRequested() {
        if (!getDialogConfirmation()) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Level level = fileIO.readLevelFile(selectedFile);
            if (level != null) {
                loadExistingLevel(level);
            }
        }
    }

    private void handleSaveRequested() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(mainPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileIO.writeLevelFile(selectedFile, level);
        }
    }

    private void handleUndoRequested() {
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
        }
    }

    private void handlePropertiesRequested() {
        PropertiesDialog dialog = new PropertiesDialog(this);
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(mainPanel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void handleGridToggleRequested(ActionEvent e) {
        AbstractButton button = (AbstractButton) e.getSource();
        isGridEnabled = button.isSelected();
        levelPanel.repaint();
    }

    private void handleOverlayToggleRequested(ActionEvent e) {
        AbstractButton button = (AbstractButton) e.getSource();
        isOverlayEnabled = button.isSelected();
        levelPanel.repaint();
    }

    private void handleQuitRequested() {
        if (getDialogConfirmation()) {
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "new" -> handleNewRequested();
            case "open" -> handleOpenRequested();
            case "save" -> handleSaveRequested();
            case "undo" -> handleUndoRequested();
            case "properties" -> handlePropertiesRequested();
            case "grid-toggle" -> handleGridToggleRequested(e);
            case "overlay-toggle" -> handleOverlayToggleRequested(e);
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

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        mainPanel.setPreferredSize(new Dimension(800, 600));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        levelScrollPane = new JScrollPane();
        levelScrollPane.setAlignmentX(0.5f);
        levelScrollPane.setAutoscrolls(false);
        levelScrollPane.setFocusable(true);
        panel1.add(levelScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(9999999, -1), null, 0, false));
        levelPanel.setAutoscrolls(false);
        levelPanel.setPreferredSize(new Dimension(-1, -1));
        levelScrollPane.setViewportView(levelPanel);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        toolControlPanel = new JPanel();
        toolControlPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(toolControlPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(-1, 50), 0, false));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolControlPanel.add(toolBar1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        selectButton = new JButton();
        selectButton.setText("Select");
        toolBar1.add(selectButton);
        drawButton = new JButton();
        drawButton.setText("Draw");
        toolBar1.add(drawButton);
        eraseButton = new JButton();
        eraseButton.setText("Erase");
        toolBar1.add(eraseButton);
        final Spacer spacer1 = new Spacer();
        toolControlPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        toolControlPanel.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tilePaletteScrollPane = new JScrollPane();
        panel2.add(tilePaletteScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tilePaletteScrollPane.setViewportView(tilePalettePanel);
        selectedTilePanel = new JPanel();
        selectedTilePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(selectedTilePanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectedTilePanel.add(selectedTilePreviewPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(32, 32), new Dimension(32, 32), null, 0, false));
        selectedTileAttributesPanel = new JPanel();
        selectedTileAttributesPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTilePanel.add(selectedTileAttributesPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
