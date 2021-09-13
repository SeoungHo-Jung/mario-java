package samj.mario.editor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import samj.mario.editor.command.*;
import samj.mario.editor.data.*;
import samj.mario.editor.data.Icon;
import samj.mario.editor.io.FileIO;
import samj.mario.editor.io.IconLoader;
import samj.mario.editor.io.JsonLevelFormat;
import samj.mario.editor.io.LevelFormat;
import samj.mario.editor.util.OsUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

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
    private JPanel behaviorAttributesPanel;
    private JComboBox behaviorComboBox;
    private JPanel sideBarPanel;
    private JPanel containerAttributesPanel;
    private JComboBox containerComboBox;
    private JSpinner containerCountSpinner;
    private JPanel enemyAttributesPanel;
    private JComboBox enemyTypeComboBox;
    private JPanel selectedTilePreviewWrapperPanel;

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

    public static final int SYSTEM_COMMAND_MODIFIER = OsUtil.isMac() ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;
    public static final int GRID_SIZE = 16;
    public static final int PALETTE_COLUMNS = 8;
    private static final String COMBO_BOX_NONE_ITEM = "NONE";

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

    private Tile getSelectedGridTile() {
        return level.getTileMatrix().getTile(selectedGridTileX, selectedGridTileY);
    }

    public void setSelectedGridTile(int selectedGridTileX, int selectedGridTileY) {
        this.selectedGridTileX = selectedGridTileX;
        this.selectedGridTileY = selectedGridTileY;
        repaintLevel();
        refreshAttributeControls();
    }

    public void setCurrentMode(EditorMode mode) {
        this.currentMode = mode;
        selectButton.setSelected(mode == EditorMode.SELECT);
        drawButton.setSelected(mode == EditorMode.DRAW);
        eraseButton.setSelected(mode == EditorMode.ERASE);
        repaintLevel();
        refreshAttributeControls();
    }

    public LevelEditor() {
        $$$setupUI$$$();
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

        // By default, create an empty level on startup
        createNewLevel();

        // Update display to reflect the default mode
        setCurrentMode(currentMode);
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
        Dimension tilePanelDimensions = new Dimension(PALETTE_COLUMNS * GRID_SIZE, ((int) Math.ceil((double) TILE_DEFINITIONS.size() / (double) PALETTE_COLUMNS)) * GRID_SIZE);
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
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SYSTEM_COMMAND_MODIFIER));
        saveMenuItem.addActionListener(this);
        fileMenu.add(saveMenuItem);

        fileMenu.addSeparator();

        quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setActionCommand("quit");
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SYSTEM_COMMAND_MODIFIER));
        quitMenuItem.addActionListener(this);
        fileMenu.add(quitMenuItem);

        editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setActionCommand("undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SYSTEM_COMMAND_MODIFIER));
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
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, SYSTEM_COMMAND_MODIFIER));
        gridMenuItem.addActionListener(this);
        gridMenuItem.setSelected(isGridEnabled);
        viewMenu.add(gridMenuItem);

        overlayMenuItem = new JCheckBoxMenuItem("Show Tile Icons");
        overlayMenuItem.setActionCommand("overlay-toggle");
        overlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SYSTEM_COMMAND_MODIFIER));
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
                Icon primaryDisplayIcon = iconResolver.primaryDisplayIcon(tile);
                Icon secondaryDisplayIcon = iconResolver.secondaryDisplayIcon(tile);
                int panelX = x * GRID_SIZE;
                int panelY = y * GRID_SIZE;
                if (primaryDisplayIcon != null) {
                    Image primaryIconImage = iconLoader.getImageForIcon(primaryDisplayIcon);
                    g.drawImage(primaryIconImage, panelX, panelY, null);
                }
                if (isOverlayEnabled && secondaryDisplayIcon != null) {
                    Image secondaryIconImage = iconLoader.getImageForIcon(secondaryDisplayIcon);
                    g.drawImage(secondaryIconImage, panelX, panelY, null);
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
        g.drawRect(x, y, GRID_SIZE, GRID_SIZE);
    }

    private void drawTilePalette(Graphics g) {
        for (int i = 0; i < TILE_DEFINITIONS.size(); i++) {
            TileDefinition tileDef = TILE_DEFINITIONS.get(i);
            Tile tile = tileDef.prototype;
            Image primaryIconImage = iconLoader.getImageForIcon(iconResolver.primaryDisplayIcon(tile));
            int x = (i % PALETTE_COLUMNS) * GRID_SIZE;
            int y = (i / PALETTE_COLUMNS) * GRID_SIZE;
            g.drawImage(primaryIconImage, x, y, null);
        }
        // draw border around the selected tile and shade with transparent color
        if (selectedPaletteTileX != null && selectedPaletteTileY != null) {
            g.setColor(new Color(0, 0, 0));
            g.drawRect(selectedPaletteTileX * GRID_SIZE, selectedPaletteTileY * GRID_SIZE, GRID_SIZE, GRID_SIZE);
            g.setColor(new Color(0, 0, 0, 64));
            g.fillRect(selectedPaletteTileX * GRID_SIZE, selectedPaletteTileY * GRID_SIZE, GRID_SIZE, GRID_SIZE);
        }
    }

    private void drawPreview(Graphics g) {
        final int previewScaleFactor = 3;
        Tile selectedGridTile = getSelectedGridTile();
        Image primaryIcon = iconLoader.getImageForIcon(iconResolver.primaryDisplayIcon(selectedGridTile));
        g.drawImage(primaryIcon, 0, 0, GRID_SIZE * previewScaleFactor, GRID_SIZE * previewScaleFactor, null);
        Image secondaryIcon = iconLoader.getImageForIcon(iconResolver.secondaryDisplayIcon(selectedGridTile));
        g.drawImage(secondaryIcon, 0, 0, GRID_SIZE * previewScaleFactor, GRID_SIZE * previewScaleFactor, null);
    }

    public void doCommand(EditorCommand command) {
        command.execute();
        undoStack.push(command);
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
        levelPanelWidth = defaultWidth * GRID_SIZE;
        levelPanelHeight = defaultHeight * GRID_SIZE;

        undoStack.clear();
        repaintLevel();
        refreshAttributeControls();
    }

    private void loadExistingLevel(Level level) {
        this.level = level;
        levelPanelWidth = level.getWidth() * GRID_SIZE;
        levelPanelHeight = level.getHeight() * GRID_SIZE;

        undoStack.clear();
        repaintLevel();
        refreshAttributeControls();
    }

    public void changeLevelDimensions(int width, int height) {
        level.setDimensions(width, height);
        levelPanelWidth = width * GRID_SIZE;
        levelPanelHeight = height * GRID_SIZE;

        // TODO: Validate that no tiles are being deleted

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
                    EditorCommand command = new ChangeTileCommand(x, y, new Tile(selectedPaletteTile), oldTile, this);
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
        selectedPaletteTileX = x;
        selectedPaletteTileY = y;
        repaintLevel();
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
        selectedTilePreviewWrapperPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        selectedTilePanel.add(selectedTilePreviewWrapperPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(52, -1), new Dimension(52, -1), new Dimension(52, -1), 0, true));
        selectedTilePreviewWrapperPanel.add(selectedTilePreviewPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(52, 52), new Dimension(52, 52), new Dimension(52, 52), 0, false));
        selectedTilePreviewPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, new Color(-4473925)));
        final Spacer spacer1 = new Spacer();
        selectedTilePreviewWrapperPanel.add(spacer1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
