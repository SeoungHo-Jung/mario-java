package editor;

import javax.swing.*;
import java.awt.event.*;

public class PropertiesDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JSpinner heightSpinner;
    private JSpinner widthSpinner;

    private final LevelEditor levelEditor;

    public PropertiesDialog(LevelEditor levelEditor) {
        this.levelEditor = levelEditor;

        widthSpinner.setValue(levelEditor.getLevel().getWidth());
        heightSpinner.setValue(levelEditor.getLevel().getHeight());

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        int oldWidth = levelEditor.getLevel().getWidth();
        int oldHeight = levelEditor.getLevel().getHeight();
        int newWidth = (int) widthSpinner.getValue();
        int newHeight = (int) heightSpinner.getValue();
        ResizeLevelCommand resizeLevelCommand = new ResizeLevelCommand(oldWidth, oldHeight, newWidth, newHeight, levelEditor);
        levelEditor.doCommand(resizeLevelCommand);
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
