package samj.mario.editor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.command.*;
import samj.mario.editor.data.*;
import samj.mario.editor.data.EditorIcon;
import samj.mario.editor.io.FileIO;
import samj.mario.editor.io.IconLoader;
import samj.mario.editor.io.JsonLevelFormat;
import samj.mario.editor.io.LevelFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

import static samj.mario.editor.data.TileData.TILE_DEFINITIONS;

public class LevelEditor implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(LevelEditor.class);

    private static JFrame FRAME;
    public static final int WINDOW_MIN_WIDTH = 400;
    public static final int WINDOW_MIN_HEIGHT = 300;
    public static final int WINDOW_PREFERRED_WIDTH = 1000;
    public static final int WINDOW_PREFERRED_HEIGHT = 575;
    public static final int GRID_SIZE = 16;
    private static final int GRID_SCALE_FACTOR = 2;
    private static final int PALETTE_SCALE_FACTOR = 2;
    private static final int PREVIEW_SCALE_FACTOR = 3;
    public static final int PALETTE_COLUMNS = 8;
    private static final String COMBO_BOX_NONE_ITEM = "NONE";

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
    private JPanel behaviorAttributesPanel;
    private JComboBox behaviorComboBox;
    private JPanel sideBarPanel;
    private JPanel containerAttributesPanel;
    private JComboBox containerComboBox;
    private JSpinner containerCountSpinner;
    private JPanel enemyAttributesPanel;
    private JComboBox enemyTypeComboBox;
    private JPanel selectedTilePreviewWrapperPanel;
    private JLabel selectedTileCoordinateLabel;

    public enum EditorMode {
        SELECT,
        DRAW,
        ERASE
    }

    private final LevelEditor thiz = this;
    private final LevelFormat levelFormat = new JsonLevelFormat();
    private final FileIO fileIO = new FileIO(levelFormat);
    private final IconResolver iconResolver = new IconResolver();
    private final IconLoader iconLoader = new IconLoader(GRID_SIZE);

    private EditorMode currentMode = EditorMode.SELECT;
    private int levelPanelWidth;
    private int levelPanelHeight;
    private Level level;
    private int selectedGridTileX;
    private int selectedGridTileY;
    private Tile selectedPaletteTile;
    private Integer selectedPaletteTileX;
    private Integer selectedPaletteTileY;
    private boolean isGridEnabled = true;
    private boolean isOverlayEnabled = true;
    private final Stack<EditorCommand> undoStack = new Stack<>();

    private final ItemListener behaviorComboBoxItemListener = e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Tile selectedTile = getSelectedGridTile();
            EditorCommand command = new ChangeTileTypeCommand(selectedTile, selectedTile.getType(), (TileType) e.getItem(), thiz);
            doCommand(command);
        }
    };

    private final ItemListener containerComboBoxItemListener = e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Tile selectedTile = getSelectedGridTile();
            EditorCommand command = new ChangeContainerTypeCommand(selectedTile, selectedTile.getContainerType(), (ContainerType) e.getItem(), thiz);
            doCommand(command);
        }
    };

    private final ItemListener enemyTypeComboBoxItemListener = e -> {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            Tile selectedTile = getSelectedGridTile();
            EnemyType newEnemyType = e.getItem() == COMBO_BOX_NONE_ITEM ? null : (EnemyType) e.getItem();
            EditorCommand command = new SetEnemySpawnCommand(selectedTile, selectedTile.getEnemyType(), newEnemyType, thiz);
            doCommand(command);
        }
    };

    private final ChangeListener containerCountSpinnerChangeListener = e -> {
        Tile selectedTile = getSelectedGridTile();
        JSpinner spinner = (JSpinner) e.getSource();
        // Value must be positive
        if ((Integer) spinner.getValue() < 1) {
            spinner.removeChangeListener(thiz.containerCountSpinnerChangeListener);
            spinner.setValue(1);
            spinner.addChangeListener(thiz.containerCountSpinnerChangeListener);
        }
        EditorCommand command = new ChangeContainerCountCommand(selectedTile, selectedTile.getCount(), (Integer) spinner.getValue(), thiz);
        doCommand(command);
    };

    public LevelEditor() {
        $$$setupUI$$$();
        levelPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int scaledGridSize = GRID_SIZE * GRID_SCALE_FACTOR;
                int x = e.getX() / scaledGridSize;
                int y = e.getY() / scaledGridSize;
                if (x >= 0 && x < level.getWidth() && y >= 0 && y < level.getHeight()) {
                    switch (currentMode) {
                        case SELECT -> {
                            EditorCommand command = new SelectGridTileCommand(selectedGridTileX, selectedGridTileY, x, y, LevelEditor.this);
                            doCommand(command);
                        }
                        case DRAW -> {
                            Tile oldTile = level.getTileMatrix().getTile(x, y);
                            EditorCommand changeTileCommand = new ChangeTileCommand(x, y, new Tile(selectedPaletteTile), oldTile, LevelEditor.this);
                            doCommand(changeTileCommand);
                            EditorCommand selectGridTileCommand = new SelectGridTileCommand(selectedGridTileX, selectedGridTileY, x, y, LevelEditor.this);
                            doCommand(selectGridTileCommand);
                        }
                        case ERASE -> {
                            Tile oldTile = level.getTileMatrix().getTile(x, y);
                            EditorCommand command = new EraseTileCommand(x, y, oldTile, LevelEditor.this);
                            doCommand(command);
                        }
                    }
                }
            }
        });
        tilePalettePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int scaledGridSize = GRID_SIZE * PALETTE_SCALE_FACTOR;
                int x = e.getX() / scaledGridSize;
                int y = e.getY() / scaledGridSize;
                int index = (y * PALETTE_COLUMNS) + x;
                if (index >= 0 && index < TILE_DEFINITIONS.size()) {
                    selectedPaletteTile = TILE_DEFINITIONS.get(index).prototype;
                }
                selectedPaletteTileX = x;
                selectedPaletteTileY = y;
                repaintLevel();
            }
        });
        selectButton.addActionListener(e -> {
            if (currentMode != EditorMode.SELECT) {
                EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.SELECT, thiz);
                doCommand(command);
            }
        });
        drawButton.addActionListener(e -> {
            if (currentMode != EditorMode.DRAW) {
                EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.DRAW, thiz);
                doCommand(command);
            }
        });
        eraseButton.addActionListener(e -> {
            if (currentMode != EditorMode.ERASE) {
                EditorCommand command = new ChangeEditorModeCommand(currentMode, EditorMode.ERASE, thiz);
                doCommand(command);
            }
        });
        behaviorComboBox.addItemListener(behaviorComboBoxItemListener);
        containerComboBox.addItemListener(containerComboBoxItemListener);
        containerCountSpinner.addChangeListener(containerCountSpinnerChangeListener);

        // Add icons to the buttons
        EditorIcon selectIcon = new EditorIcon(IconSheet.EDITOR, 0, 0);
        Icon selectSwingIcon = new ImageIcon(iconLoader.getImageForIcon(selectIcon).getScaledInstance(32, 32, 0));
        selectButton.setIcon(selectSwingIcon);
        selectButton.setText(null);

        EditorIcon drawIcon = new EditorIcon(IconSheet.EDITOR, 1, 0);
        Icon drawSwingIcon = new ImageIcon(iconLoader.getImageForIcon(drawIcon).getScaledInstance(32, 32, 0));
        drawButton.setIcon(drawSwingIcon);
        drawButton.setText(null);

        EditorIcon eraseIcon = new EditorIcon(IconSheet.EDITOR, 2, 0);
        Icon eraseSwingIcon = new ImageIcon(iconLoader.getImageForIcon(eraseIcon).getScaledInstance(32, 32, 0));
        eraseButton.setIcon(eraseSwingIcon);
        eraseButton.setText(null);

        // By default, create an empty level on startup
        createNewLevel();

        // Update display to reflect the default mode
        setCurrentMode(currentMode);
    }

    public Level getLevel() {
        return level;
    }

    private Tile getSelectedGridTile() {
        return level.getTileMatrix().getTile(selectedGridTileX, selectedGridTileY);
    }

    public void setSelectedGridTile(int selectedGridTileX, int selectedGridTileY) {
        this.selectedGridTileX = selectedGridTileX;
        this.selectedGridTileY = selectedGridTileY;
        refreshSelectedTileCoordinates(selectedGridTileX, selectedGridTileY);
        repaintLevel();
        refreshAttributeControls();
    }

    public boolean isGridEnabled() {
        return isGridEnabled;
    }

    public boolean isOverlayEnabled() {
        return isOverlayEnabled;
    }

    private void refreshSelectedTileCoordinates(int selectedGridTileX, int selectedGridTileY) {
        this.selectedTileCoordinateLabel.setText(String.format("(%d,%d)", selectedGridTileX, selectedGridTileY));
    }

    public void setCurrentMode(EditorMode mode) {
        this.currentMode = mode;
        selectButton.setSelected(mode == EditorMode.SELECT);
        drawButton.setSelected(mode == EditorMode.DRAW);
        eraseButton.setSelected(mode == EditorMode.ERASE);
        repaintLevel();
        refreshAttributeControls();
    }

    public void doCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
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
        Dimension tilePanelDimensions = new Dimension(PALETTE_COLUMNS * GRID_SIZE * PALETTE_SCALE_FACTOR, ((int) Math.ceil((double) TILE_DEFINITIONS.size() / (double) PALETTE_COLUMNS)) * GRID_SIZE * PALETTE_SCALE_FACTOR);
        tilePalettePanel.setMinimumSize(tilePanelDimensions);
        tilePalettePanel.setPreferredSize(tilePanelDimensions);
        tilePalettePanel.setMaximumSize(tilePanelDimensions);

        selectedTilePreviewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawPreview(g);
            }
        };

        JMenuBar menuBar = new EditorMenuBar(this);
        FRAME.setJMenuBar(menuBar);
    }

    private void drawLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, levelPanelWidth, levelPanelHeight);
        if (level != null) {
            drawBackground(g);
            drawTiles(g);
        }
        if (isGridEnabled) {
            drawGrid(g);
        }
        if (currentMode == EditorMode.SELECT) {
            drawSelectionBox(g);
        }
    }

    private void drawBackground(Graphics g) {
        g.setColor(level.getBackgroundColor());
        g.fillRect(0, 0, levelPanelWidth, levelPanelHeight);
    }

    private void drawTiles(Graphics g) {
        for (int x = 0; x < level.getWidth(); x++) {
            for (int y = 0; y < level.getHeight(); y++) {
                Tile tile = level.getTileMatrix().getTile(x, y);
                EditorIcon primaryDisplayIcon = iconResolver.primaryDisplayIcon(tile);
                EditorIcon secondaryDisplayIcon = iconResolver.secondaryDisplayIcon(tile);
                int scaledGridSize = GRID_SIZE * GRID_SCALE_FACTOR;
                int panelX = x * scaledGridSize;
                int panelY = y * scaledGridSize;
                if (primaryDisplayIcon != null) {
                    Image primaryIconImage = iconLoader.getImageForIcon(primaryDisplayIcon);
                    g.drawImage(primaryIconImage, panelX, panelY, scaledGridSize, scaledGridSize, null);
                }
                if (isOverlayEnabled && secondaryDisplayIcon != null) {
                    Image secondaryIconImage = iconLoader.getImageForIcon(secondaryDisplayIcon);
                    g.drawImage(secondaryIconImage, panelX, panelY, scaledGridSize, scaledGridSize, null);
                }
            }
        }
    }

    private void drawGrid(Graphics g) {
        int width = levelPanelWidth;
        int height = levelPanelHeight;

        g.setColor(Color.LIGHT_GRAY);

        int scaledGridSize = GRID_SIZE * GRID_SCALE_FACTOR;

        // vertical lines
        for (int i = scaledGridSize; i < width; i += scaledGridSize) {
            g.drawLine(i, 0, i, height);
        }

        // horizontal lines
        for (int i = scaledGridSize; i < height; i += scaledGridSize) {
            g.drawLine(0, i, width, i);
        }
    }

    private void drawSelectionBox(Graphics g) {
        int scaledGridSize = GRID_SIZE * GRID_SCALE_FACTOR;

        int x = selectedGridTileX * scaledGridSize;
        int y = selectedGridTileY * scaledGridSize;

        g.setColor(Color.RED);

        // draw a square, upper left at x,y
        g.drawRect(x, y, scaledGridSize, scaledGridSize);
    }

    private void drawTilePalette(Graphics g) {
        int scaledGridSize = GRID_SIZE * PALETTE_SCALE_FACTOR;
        for (int i = 0; i < TILE_DEFINITIONS.size(); i++) {
            TileDefinition tileDef = TILE_DEFINITIONS.get(i);
            Tile tile = tileDef.prototype;
            Image primaryIconImage = iconLoader.getImageForIcon(iconResolver.primaryDisplayIcon(tile));
            int x = (i % PALETTE_COLUMNS) * scaledGridSize;
            int y = (i / PALETTE_COLUMNS) * scaledGridSize;
            g.drawImage(primaryIconImage, x, y, scaledGridSize, scaledGridSize, null);
        }
        // draw border around the selected tile and shade with transparent color
        if (selectedPaletteTileX != null && selectedPaletteTileY != null) {
            g.setColor(Color.RED);
            g.drawRect(selectedPaletteTileX * scaledGridSize, selectedPaletteTileY * scaledGridSize, scaledGridSize, scaledGridSize);
            g.setColor(new Color(0, 0, 0, 64));
            g.fillRect(selectedPaletteTileX * scaledGridSize, selectedPaletteTileY * scaledGridSize, scaledGridSize, scaledGridSize);
        }
    }

    private void drawPreview(Graphics g) {
        Tile selectedGridTile = getSelectedGridTile();
        Image primaryIcon = iconLoader.getImageForIcon(iconResolver.primaryDisplayIcon(selectedGridTile));
        int scaledGridSize = GRID_SIZE * PREVIEW_SCALE_FACTOR;
        g.drawImage(primaryIcon, 0, 0, scaledGridSize, scaledGridSize, null);
        Image secondaryIcon = iconLoader.getImageForIcon(iconResolver.secondaryDisplayIcon(selectedGridTile));
        g.drawImage(secondaryIcon, 0, 0, scaledGridSize, scaledGridSize, null);
    }

    public void repaintLevel() {
        // Resize components & repaint
        levelPanel.setPreferredSize(new Dimension(levelPanelWidth, levelPanelHeight));
        JViewport viewport = levelScrollPane.getViewport();
        viewport.setViewSize(new Dimension(levelPanelWidth, levelPanelHeight));
        levelScrollPane.revalidate();
        levelScrollPane.repaint();
        tilePalettePanel.repaint();
        selectedTilePreviewPanel.repaint();
    }

    public void refreshAttributeControls() {
        // Only show selection panel in SELECT mode
        selectedTilePanel.setVisible(currentMode == EditorMode.SELECT);

        // Set up the tile attribute controls
        Tile selectedTile = getSelectedGridTile();
        TileType selectedTileType = selectedTile.getType();

        // remove the listeners to prevent sending events while the values are modified
        behaviorComboBox.removeItemListener(behaviorComboBoxItemListener);
        containerComboBox.removeItemListener(containerComboBoxItemListener);
        containerCountSpinner.removeChangeListener(containerCountSpinnerChangeListener);
        enemyTypeComboBox.removeItemListener(enemyTypeComboBoxItemListener);

        behaviorComboBox.removeAllItems();
        selectedTile.getAllowedTileTypes().forEach(type -> behaviorComboBox.addItem(type));
        behaviorComboBox.setSelectedItem(selectedTileType);

        if (selectedTileType == TileType.CONTAINER) {
            containerAttributesPanel.setVisible(true);
            containerComboBox.removeAllItems();
            selectedTile.getAllowedContainerTypes().forEach(type -> containerComboBox.addItem(type));
            containerComboBox.setSelectedItem(selectedTile.getContainerType());
            containerCountSpinner.setValue(selectedTile.getCount());
        } else {
            containerAttributesPanel.setVisible(false);
        }

        if (selectedTileType == TileType.EMPTY || selectedTileType == TileType.BACKGROUND) {
            enemyAttributesPanel.setVisible(true);
            enemyTypeComboBox.removeAllItems();
            enemyTypeComboBox.addItem(COMBO_BOX_NONE_ITEM);
            for (EnemyType type : EnemyType.values()) {
                if (type != EnemyType.UNKNOWN) {
                    enemyTypeComboBox.addItem(type);
                }
            }
            if (selectedTile.getEnemyType() != null) {
                enemyTypeComboBox.setSelectedItem(selectedTile.getEnemyType());
            } else {
                enemyTypeComboBox.setSelectedItem(COMBO_BOX_NONE_ITEM);
            }
        } else {
            enemyAttributesPanel.setVisible(false);
        }

        // put back the listeners
        behaviorComboBox.addItemListener(behaviorComboBoxItemListener);
        containerComboBox.addItemListener(containerComboBoxItemListener);
        containerCountSpinner.addChangeListener(containerCountSpinnerChangeListener);
        enemyTypeComboBox.addItemListener(enemyTypeComboBoxItemListener);
    }

    private void createNewLevel() {
        final int defaultWidth = 100;
        final int defaultHeight = 15;
        final Color defaultBackgroundColor = new Color(152, 137, 255);
        final String defaultName = "New Level";
        final int defaultTimeLimit = 300;

        level = new Level();
        level.setDimensions(defaultWidth, defaultHeight);
        level.setBackgroundColor(defaultBackgroundColor);
        level.setName(defaultName);
        level.setTimeLimit(defaultTimeLimit);
        level.setTileMatrix(new TileMatrix(defaultWidth, defaultHeight));
        levelPanelWidth = defaultWidth * GRID_SIZE * GRID_SCALE_FACTOR;
        levelPanelHeight = defaultHeight * GRID_SIZE * GRID_SCALE_FACTOR;
        resetEditor();

        logger.debug("New Level created");
    }

    private void loadExistingLevel(Level level) {
        this.level = level;
        levelPanelWidth = level.getWidth() * GRID_SIZE * GRID_SCALE_FACTOR;
        levelPanelHeight = level.getHeight() * GRID_SIZE * GRID_SCALE_FACTOR;
        resetEditor();

        logger.debug("Existing Level loaded");
    }

    private void resetEditor() {
        selectedGridTileX = 0;
        selectedGridTileY = 0;
        refreshSelectedTileCoordinates(0, 0);
        undoStack.clear();
        repaintLevel();
        refreshAttributeControls();
    }

    public void changeLevelDimensions(int width, int height) {
        level.setDimensions(width, height);
        levelPanelWidth = width * GRID_SIZE * GRID_SCALE_FACTOR;
        levelPanelHeight = height * GRID_SIZE * GRID_SCALE_FACTOR;

        // TODO: Validate that no tiles are being deleted?

        level.setTileMatrix(new TileMatrix(width, height, level.getTileMatrix()));

        repaintLevel();
    }

    public void changeLevelBackgroundColor(Color color) {
        level.setBackgroundColor(color);
        repaintLevel();
    }

    public void changeLevelName(String name) {
        level.setName(name);
    }

    public void changeTimeLimit(int limit) {
        level.setTimeLimit(limit);
    }

    private boolean solicitDialogConfirmation() {
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

    @Override
    public void actionPerformed(ActionEvent e) {

        // Handlers for menubar items
        switch (e.getActionCommand()) {
            case "new" -> {
                logger.debug("New requested");
                if (solicitDialogConfirmation()) {
                    createNewLevel();
                }
            }
            case "open" -> {
                logger.debug("Open requested");
                if (solicitDialogConfirmation()) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(mainPanel);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        Level openedLevel = fileIO.readLevelFile(selectedFile);
                        if (openedLevel != null) {
                            loadExistingLevel(openedLevel);
                        }
                    }
                }
            }
            case "save" -> {
                logger.debug("Save requested");
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(mainPanel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileIO.writeLevelFile(selectedFile, level);
                }
            }
            case "undo" -> {
                if (!undoStack.isEmpty()) {
                    logger.debug("Undo");
                    // Auto-undo commands:
                    // - Select
                    // - Change Mode
                    EditorCommand command;
                    do {
                        command = undoStack.pop();
                        command.undo();
                    } while (!undoStack.isEmpty() && (command instanceof SelectGridTileCommand || command instanceof ChangeEditorModeCommand));
                } else {
                    logger.info("Can't Undo");
                }
            }
            case "properties" -> {
                logger.debug("Properties dialog requested");
                PropertiesDialog dialog = new PropertiesDialog(this);
                dialog.setAlwaysOnTop(true);
                dialog.setLocationRelativeTo(mainPanel);
                dialog.pack();
                dialog.setVisible(true);
            }
            case "grid-toggle" -> {
                logger.debug("Grid toggle");
                AbstractButton button = (AbstractButton) e.getSource();
                isGridEnabled = button.isSelected();
                levelPanel.repaint();
            }
            case "overlay-toggle" -> {
                logger.debug("Overlay toggle");
                AbstractButton button = (AbstractButton) e.getSource();
                isOverlayEnabled = button.isSelected();
                levelPanel.repaint();
            }
            case "quit" -> {
                logger.debug("Quit requested");
                if (solicitDialogConfirmation()) {
                    System.exit(0);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Level Editor");
        FRAME = frame;
        frame.setContentPane(new LevelEditor().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(WINDOW_MIN_WIDTH, WINDOW_MIN_HEIGHT));
        frame.setPreferredSize(new Dimension(WINDOW_PREFERRED_WIDTH, WINDOW_PREFERRED_HEIGHT));
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
        levelScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        levelPanel.setAutoscrolls(false);
        levelPanel.setPreferredSize(new Dimension(-1, -1));
        levelScrollPane.setViewportView(levelPanel);
        sideBarPanel = new JPanel();
        sideBarPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(sideBarPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(300, -1), new Dimension(300, -1), new Dimension(300, -1), 0, false));
        sideBarPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        toolControlPanel = new JPanel();
        toolControlPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        sideBarPanel.add(toolControlPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(-1, 50), new Dimension(-1, 50), new Dimension(-1, 50), 0, false));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setVisible(true);
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
        tilePaletteScrollPane = new JScrollPane();
        tilePaletteScrollPane.setHorizontalScrollBarPolicy(31);
        sideBarPanel.add(tilePaletteScrollPane, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tilePaletteScrollPane.setBorder(BorderFactory.createTitledBorder(null, "Tile Palette", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tilePaletteScrollPane.setViewportView(tilePalettePanel);
        selectedTilePanel = new JPanel();
        selectedTilePanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        sideBarPanel.add(selectedTilePanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        selectedTilePanel.setBorder(BorderFactory.createTitledBorder(null, "Selected Tile", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        selectedTileAttributesPanel = new JPanel();
        selectedTileAttributesPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTilePanel.add(selectedTileAttributesPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        behaviorAttributesPanel = new JPanel();
        behaviorAttributesPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTileAttributesPanel.add(behaviorAttributesPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Behavior:");
        behaviorAttributesPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        behaviorComboBox = new JComboBox();
        behaviorAttributesPanel.add(behaviorComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        containerAttributesPanel = new JPanel();
        containerAttributesPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTileAttributesPanel.add(containerAttributesPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Item Type:");
        containerAttributesPanel.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        containerComboBox = new JComboBox();
        containerAttributesPanel.add(containerComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Item Count");
        containerAttributesPanel.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        containerCountSpinner = new JSpinner();
        containerAttributesPanel.add(containerCountSpinner, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enemyAttributesPanel = new JPanel();
        enemyAttributesPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTileAttributesPanel.add(enemyAttributesPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Enemy Spawn");
        enemyAttributesPanel.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enemyTypeComboBox = new JComboBox();
        enemyAttributesPanel.add(enemyTypeComboBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectedTilePreviewWrapperPanel = new JPanel();
        selectedTilePreviewWrapperPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTilePanel.add(selectedTilePreviewWrapperPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(52, -1), new Dimension(52, -1), new Dimension(52, -1), 0, true));
        selectedTilePreviewWrapperPanel.add(selectedTilePreviewPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(52, 52), new Dimension(48, 52), new Dimension(52, 52), 0, false));
        selectedTilePreviewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-4473925)));
        final Spacer spacer1 = new Spacer();
        selectedTilePreviewWrapperPanel.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(48, 14), null, 0, false));
        selectedTileCoordinateLabel = new JLabel();
        selectedTileCoordinateLabel.setHorizontalTextPosition(0);
        selectedTileCoordinateLabel.setText("");
        selectedTilePreviewWrapperPanel.add(selectedTileCoordinateLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(48, 0), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
