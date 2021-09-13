package samj.mario.editor.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import samj.mario.editor.command.ChangeContainerCountCommand;
import samj.mario.editor.data.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {

    private static final Logger logger = LoggerFactory.getLogger(FileIO.class);

    private final LevelFormat levelFormat;

    public FileIO(LevelFormat levelFormat) {
        this.levelFormat = levelFormat;
    }

    public Level readLevelFile(File file) {
        Level level = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] encodedLevel = inputStream.readAllBytes();
            level = levelFormat.decode(encodedLevel);
            logger.info("Opened file {}", file);
        } catch (IOException e) {
            logger.error("Couldn't open file {}", file, e);
        }
        return level;
    }

    public void writeLevelFile(File file, Level level) {
        byte[] encodedLevel = levelFormat.encode(level);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(encodedLevel);
            logger.info("Saved file {}", file);
        } catch (IOException e) {
            logger.error("Couldn't save file {}", file, e);
        }
    }
}
