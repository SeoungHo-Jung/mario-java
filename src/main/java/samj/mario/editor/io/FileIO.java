package samj.mario.editor.io;

import samj.mario.editor.data.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {

    private final LevelFormat levelFormat;

    public FileIO(LevelFormat levelFormat) {
        this.levelFormat = levelFormat;
    }

    public Level readLevelFile(File file) {
        Level level = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] encodedLevel = inputStream.readAllBytes();
            level = levelFormat.decode(encodedLevel);
            System.out.println("Opened file: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return level;
    }

    public void writeLevelFile(File file, Level level) {
        byte[] encodedLevel = levelFormat.encode(level);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(encodedLevel);
            System.out.println("Saved file: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
