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

    public TileMatrix getTileMatrix() {
        return tileMatrix;
    }

    public void setTileMatrix(TileMatrix tileMatrix) {
        this.tileMatrix = tileMatrix;
    }
}
