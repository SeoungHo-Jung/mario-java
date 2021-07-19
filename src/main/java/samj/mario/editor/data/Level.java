package samj.mario.editor.data;

public class Level {

    private int width;
    private int height;
    private TileMatrix tileMatrix;

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

    public TileMatrix getForegroundLayer() {
        return tileMatrix;
    }

    public void setForegroundLayer(TileMatrix tileMatrix) {
        this.tileMatrix = tileMatrix;
    }
}
