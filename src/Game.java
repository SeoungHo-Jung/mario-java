import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class Game extends Canvas implements Runnable, KeyListener {

    // Sprite Sheet
    private BufferedImage spriteSheet;
    private BufferedImage marioImg;

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

        //      1/60 is approx 0.01666....7 thus 1 tic would be approx 16 milliseconds
        //      Here, I will set it to 166 for the sake of visibility.
        try {
            sleep(166);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        //  x = 120 and y = 100 is completely arbitrary. Will need to replace with actual variables.

        //clear the previous image that was drawn.
        g.clearRect(120, 100, 16, 32);
        //draw new image
        g.drawImage(marioImg, 120, 100, null);


    }
    /*
    private BufferedImage getSpecificImage(String command){
        switch (command){
            case "normal":

        }
    }*/

    @Override
    public void run() {
        init();

        //The actual images start from the 80th pixel.
        int colNum = 80;
        while (true) {
            // Main game loop

            // TODO: Make it run at 60 ticks per second with no framerate cap.
            //  Log to the console every second:
            //      - Ticks per second
            //      - Frames per second
            marioImg = spriteSheet.getSubimage(colNum, 0, 16, 32);
            tick();
            render();

            //Goofy implementation (Only for this time)
            //The width of the spritesheet is 416. If it reaches the end of the row, it will start from the beginning
            if(colNum < 400){
                colNum += 16;
            }
            else{
                colNum = 80;
            }
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
