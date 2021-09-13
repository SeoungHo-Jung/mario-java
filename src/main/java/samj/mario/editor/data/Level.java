package samj.mario.editor.data;

import java.awt.*;

public class Level {

    private int width;
    private int height;
    private String name;
    private Color backgroundColor;
    private TileMatrix tileMatrix;
    private int timeLimit;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public TileMatrix getTileMatrix() {
        return tileMatrix;
    }

    public void setTileMatrix(TileMatrix tileMatrix) {
        this.tileMatrix = tileMatrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
