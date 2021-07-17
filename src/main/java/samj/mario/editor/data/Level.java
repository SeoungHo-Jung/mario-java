package samj.mario.editor.data;

import samj.mario.editor.data.ForegroundLayer;

public class Level {

    private int width;
    private int height;
    private ForegroundLayer foregroundLayer;

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

    public ForegroundLayer getForegroundLayer() {
        return foregroundLayer;
    }

    public void setForegroundLayer(ForegroundLayer foregroundLayer) {
        this.foregroundLayer = foregroundLayer;
    }
}
