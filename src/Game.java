import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Game extends Canvas implements Runnable, KeyListener {

    // Sprite Sheet
    private BufferedImage spriteSheet;

    public void init() {
        // Load the sprite sheet image
        String spriteFile = "mario.png";
        URL imageURL = getClass().getClassLoader().getResource(spriteFile);
        if (imageURL == null) {
            System.err.println("Couldn't find sprite file: " + spriteFile);
        } else {
            try {
                BufferedImage in = ImageIO.read(imageURL);
                spriteSheet = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                spriteSheet.getGraphics().drawImage(in, 0, 0, null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // Set the alpha channel to 0 for pixels with the "transparency" color
            int transparentColor = 0;
            for (int x = 0; x < spriteSheet.getWidth(); x++) {
                for (int y = 0; y < spriteSheet.getHeight(); y++) {
                    int[] pixel = spriteSheet.getRaster().getPixel(x, y, (int[]) null);
                    int rgbColor = (pixel[0] << 16) | pixel[1] << 8 | pixel[2];
                    int[] pixelCopy = Arrays.copyOf(pixel, pixel.length);
                    pixelCopy[3] = rgbColor == transparentColor ? 0x00 : 0xFF;
                    spriteSheet.getRaster().setPixel(x, y, pixelCopy);
                }
            }
        }

        // Register the KeyListener for this Canvas
        addKeyListener(this);

        // Focus the Canvas to accept input immediately
        requestFocus();
    }

    public void tick() {
        // Update the game's state on a fixed-rate interval
    }

    public void render() {
        // Draw the graphics to the screen
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            // Use a double-buffering strategy
            createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        drawBackground(g);
        drawSprites(g);
        g.dispose();
        bs.show();
    }

    private void drawBackground(Graphics g) {
        // Draw the background to the screen
    }

    private void drawSprites(Graphics g) {
        // Draw the sprites on top of the background
    }

    @Override
    public void run() {
        init();

        while (true) {
            // Main game loop

            // TODO: Make it run at 60 ticks per second with no framerate cap.
            //  Log to the console every 5 seconds:
            //      - Ticks per second
            //      - Frames per second

            tick();
            render();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle key event
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle key down event
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key up event
    }
}
