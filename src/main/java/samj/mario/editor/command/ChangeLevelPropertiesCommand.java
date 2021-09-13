package samj.mario.editor.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.LevelEditor;

import java.awt.*;

public class ChangeLevelPropertiesCommand implements EditorCommand {

    private static final Logger logger = LoggerFactory.getLogger(ChangeLevelPropertiesCommand.class);

    private final int oldWidth;
    private final int oldHeight;
    private final int newWidth;
    private final int newHeight;
    private final String oldName;
    private final String newName;
    private final Color oldColor;
    private final Color newColor;
    private final int oldTimeLimit;
    private final int newTimeLimit;
    private final LevelEditor levelEditor;

    public ChangeLevelPropertiesCommand(int oldWidth, int oldHeight, int newWidth, int newHeight, String oldName, String newName, Color oldColor, Color newColor, int oldTimeLimit, int newTimeLimit, LevelEditor levelEditor) {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
        this.oldName = oldName;
        this.newName = newName;
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.oldTimeLimit = oldTimeLimit;
        this.newTimeLimit = newTimeLimit;
        this.levelEditor = levelEditor;
    }

    @Override
    public void execute() {
        levelEditor.changeLevelDimensions(newWidth, newHeight);
        levelEditor.changeLevelName(newName);
        levelEditor.changeLevelBackgroundColor(newColor);
        levelEditor.changeTimeLimit(newTimeLimit);
    }

    @Override
    public void undo() {
        levelEditor.changeLevelDimensions(oldWidth, oldHeight);
        levelEditor.changeLevelName(oldName);
        levelEditor.changeLevelBackgroundColor(oldColor);
        levelEditor.changeTimeLimit(oldTimeLimit);
    }
}
