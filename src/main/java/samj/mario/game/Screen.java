package samj.mario.game;

import java.awt.*;
import java.awt.image.BufferedImage;

import static samj.mario.game.Application.CANVAS_HEIGHT;
import static samj.mario.game.Application.CANVAS_WIDTH;

public class Screen {

    private int frameWidth = CANVAS_WIDTH;
    private int frameHeight = CANVAS_HEIGHT;
    private int screenXOffsetToLevel = 0;

    private Graphics g;

    public void setGraphics(Graphics g) {
        this.g = g;
    }

    public void setXOffset(int offset) {
        this.screenXOffsetToLevel = offset;
    }

    /**
     * Draw an image to the "screen"
     * @param image
     * @param x The Game-space x coordinate of the sprite
     * @param y Game-space y coordinate
     */
    public void drawToScreen(BufferedImage image, int x, int y) {
        // Check if sprite is on screen
        if ((x + image.getWidth()) >= screenXOffsetToLevel && x <= (screenXOffsetToLevel + frameWidth)) {
            int offsetX = x - screenXOffsetToLevel;
            if (g != null) {
                g.drawImage(image, offsetX, y, null);
            }
        }
    }
}
