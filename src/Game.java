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
    private int ticks = 0;
    private int frames = 0;
    private double prevTime;

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

        ticks ++;
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
        frames++;
    }

    private void drawBackground(Graphics g) {
        // Draw the background to the screen
    }

    private void drawSprites(Graphics g) {
        // Draw the sprites on top of the background

        //  x = 120 and y = 100 is completely arbitrary. Will need to replace with actual variables.

        //clear the previous image that was drawn.
        g.clearRect(0, 0, 256, 240);
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
        prevTime = System.currentTimeMillis();
        int framesPerSec;
        int number_of_ticks = 60;
        double tickInterval = 1000 / number_of_ticks;

        //This keeps track of how much time elapsed
        double start;
        double end;
        double elapsed;

        start = System.currentTimeMillis() / 1000;
        while (true) {
            // Main game loop

            // TODO: Make it run at 60 ticks per second with no framerate cap.
            //  Log to the console every second:
            //      - Ticks per second
            //      - Frames per second

            double now = System.currentTimeMillis();
            double timeDiff = now - prevTime;

            //As loop goes on, it will ignore if the time difference is less than the tick interval
            if(timeDiff >= tickInterval){
                now = System.currentTimeMillis();
                marioImg = spriteSheet.getSubimage(colNum, 32, 16, 16);
                tick();
                render();
                prevTime = now;
                end = System.currentTimeMillis() / 1000;
                elapsed = end - start;
                //Again, pretty goofy implementation. I'm sure there is a better way
                if(elapsed % 1 == 0 && ticks % 60 == 0){
                    framesPerSec = (int)(frames / elapsed);
                    System.out.println(elapsed + " seconds  |" + ticks + " ticks    |" + framesPerSec + " fps");
                }
               // System.out.println(timeDiff / 1000 + "seconds and " + ticks + " ticks");

            }



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
