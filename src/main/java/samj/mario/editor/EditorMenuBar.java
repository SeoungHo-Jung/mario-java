package samj.mario.editor;

import samj.mario.editor.util.OsUtil;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class EditorMenuBar extends JMenuBar {

    public static final int SYSTEM_COMMAND_MODIFIER = OsUtil.isMac() ? InputEvent.META_DOWN_MASK : InputEvent.CTRL_DOWN_MASK;

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

    public EditorMenuBar(LevelEditor levelEditor) {
        super();
        fileMenu = new JMenu("File");
        this.add(fileMenu);

        newMenuItem = new JMenuItem("New");
        newMenuItem.setActionCommand("new");
        newMenuItem.addActionListener(levelEditor);
        fileMenu.add(newMenuItem);

        fileMenu.addSeparator();

        openMenuItem = new JMenuItem("Open");
        openMenuItem.setActionCommand("open");
        openMenuItem.addActionListener(levelEditor);
        fileMenu.add(openMenuItem);

        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setActionCommand("save");
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SYSTEM_COMMAND_MODIFIER));
        saveMenuItem.addActionListener(levelEditor);
        fileMenu.add(saveMenuItem);

        fileMenu.addSeparator();

        quitMenuItem = new JMenuItem("Quit");
        quitMenuItem.setActionCommand("quit");
        quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SYSTEM_COMMAND_MODIFIER));
        quitMenuItem.addActionListener(levelEditor);
        fileMenu.add(quitMenuItem);

        editMenu = new JMenu("Edit");
        this.add(editMenu);

        undoMenuItem = new JMenuItem("Undo");
        undoMenuItem.setActionCommand("undo");
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, SYSTEM_COMMAND_MODIFIER));
        undoMenuItem.addActionListener(levelEditor);
        editMenu.add(undoMenuItem);

        editMenu.addSeparator();

        propertiesMenuItem = new JMenuItem("Properties");
        propertiesMenuItem.setActionCommand("properties");
        propertiesMenuItem.addActionListener(levelEditor);
        editMenu.add(propertiesMenuItem);

        viewMenu = new JMenu("View");
        this.add(viewMenu);

        gridMenuItem = new JCheckBoxMenuItem("Show Grid");
        gridMenuItem.setActionCommand("grid-toggle");
        gridMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, SYSTEM_COMMAND_MODIFIER));
        gridMenuItem.addActionListener(levelEditor);
        gridMenuItem.setSelected(levelEditor.isGridEnabled());
        viewMenu.add(gridMenuItem);

        overlayMenuItem = new JCheckBoxMenuItem("Show Tile Icons");
        overlayMenuItem.setActionCommand("overlay-toggle");
        overlayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, SYSTEM_COMMAND_MODIFIER));
        overlayMenuItem.addActionListener(levelEditor);
        overlayMenuItem.setSelected(levelEditor.isOverlayEnabled());
        viewMenu.add(overlayMenuItem);
    }
}
