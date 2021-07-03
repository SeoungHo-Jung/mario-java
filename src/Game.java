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

import static java.awt.event.KeyEvent.*;
import static java.lang.Thread.sleep;

public class Game extends Canvas implements Runnable, KeyListener {


    // Sprite Sheet
    private BufferedImage spriteSheet;
    private BufferedImage marioImg;

    private int ticks = 0;
    private int frames = 0;
    private double prevTime;

    //Mario's positions : initialized as
    int marioX = 160;
    int marioY = 160;

    //Mario's size : initialized as 16 X 16
    int marioWidth = 16;
    int marioHeight = 32;

    // Keys
    int pressedKeyCode;
    int releasedKeyCode;
    int rightKeyCounter = 0;
    int leftKeyCounter = 0;

    //Grid : Each tile is sized 16 X 16
    private char[][] reachableOrNot = new char[16][14];

    //Test cases
    private String demoLevel =
            "######    ######" +
            "          ####  " +
            "                " +
            "        ######  " +
            "        ######  " +
            "                " +
            "                " +
            "                " +
            "  ##            " +
            "  ##            " +
            "        ###     " +
            "               #" +
            "################" +
            "################";


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
        if(reachableOrNot[marioX/16][marioY/16] != '#'){
            g.drawImage(marioImg, marioX, marioY, null);
        }




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

        //All images starts from 80px
        int colStart = 80;
        int colEnd = 416;
        int colCurr = 80;

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
                marioImg = spriteSheet.getSubimage(colCurr, 0, marioWidth, marioHeight);
                tick();
                render();

                ///////////////////////CHECK TIME//////////////////////////////////////////////
                prevTime = now;
                end = System.currentTimeMillis() / 1000;
                elapsed = end - start;
                //Again, pretty goofy implementation. I'm sure there is a better way
                if(elapsed % 1 == 0 && ticks % 60 == 0){
                    framesPerSec = (int)(frames / elapsed);
                    System.out.println(elapsed + " seconds elapsed  |" + ticks + " ticks    |" + framesPerSec + " fps");
                }
                ///////////////////////CHECK TIME//////////////////////////////////////////////

            }


            if(pressedKeyCode == VK_RIGHT){
                colStart = 96;
                colEnd = 128;
            }

            else if(pressedKeyCode == VK_LEFT){
                colStart = 96;
                colEnd = 128;
            }
            else if(pressedKeyCode == VK_UP){
                colStart = 160;
                colEnd = 160;
            }
            else if(pressedKeyCode == VK_DOWN){
                colStart = 176;
                colEnd = 176;
            }
             /*
            if(releasedKeyCode == VK_UP || releasedKeyCode == VK_DOWN || releasedKeyCode == VK_LEFT || releasedKeyCode == VK_RIGHT){
                colStart = 80;
                colEnd = 80;

            }
            */


            colCurr = colEnd;
            if(colCurr < colEnd){

                colCurr += 16;
            }
            else{
                colCurr = colStart;
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
        pressedKeyCode = e.getKeyCode();

        //Have to deal with diagonal movements as well
        if(pressedKeyCode == VK_RIGHT){
            //System.out.println("right was pressed");
            if(marioX < 240){
                marioX += 16;
            }
            //rightKeyCounter++;
        }
        else if(pressedKeyCode == VK_LEFT){
            //System.out.println("left was pressed");
            if(marioX > 0){
                marioX -= 16;
            }
            //leftKeyCounter++;
        }
        else if(pressedKeyCode == VK_UP){
            //System.out.println("up was pressed");
            if(marioY > 0){
                marioY -= 16;
            }
        }
        else if(pressedKeyCode == VK_DOWN){
            //System.out.println("down was pressed");
            if(marioY < 208){
                marioY += 16;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key up event
        releasedKeyCode = e.getKeyCode();

        if(releasedKeyCode == VK_RIGHT){
            //System.out.println("right was released");
        }
        else if(releasedKeyCode == VK_LEFT){
            //System.out.println("left was released");
        }
        else if(releasedKeyCode == VK_UP){
            //System.out.println("up was released");
        }
        else if(releasedKeyCode == VK_DOWN){
            //System.out.println("down was released");
        }
    }
    
    public class Tiles{

        public Tiles(String imgName){
            int tileX = 0;
            int tileY = 0;
            int tileWidth = 16;
            int tileHeight = 16;

            URL imageURL = getClass().getClassLoader().getResource(imgName);
            URL url;
            BufferedImage tile;
        }
    }
}



