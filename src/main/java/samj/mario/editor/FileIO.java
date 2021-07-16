package samj.mario.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileIO {

    private final LevelEncoder levelEncoder;
    private final LevelDecoder levelDecoder;

    public FileIO(LevelEncoder levelEncoder, LevelDecoder levelDecoder) {
        this.levelEncoder = levelEncoder;
        this.levelDecoder = levelDecoder;
    }

    public Level readLevelFile(File file) {
        Level level = null;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] encodedLevel = inputStream.readAllBytes();
            level = levelDecoder.decode(encodedLevel);
            System.out.println("Opened file: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return level;
    }

    public void writeLevelFile(File file, Level level) {
        byte[] encodedLevel = levelEncoder.encode(level);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(encodedLevel);
            System.out.println("Saved file: " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
