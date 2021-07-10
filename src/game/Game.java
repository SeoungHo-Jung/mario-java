package game;

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
    private BufferedImage blockImg;

    private int ticks = 0;
    private int frames = 0;
    private double prevTime;

    //Mario's positions : initialized as
    float marioX = 32;
    float marioY = 192;


    //Mario's size : initialized as 16 X 16
    int marioWidth = 16;
    int marioHeight = 16;

    // Keys
    boolean right_key_pressed = false;
    boolean left_key_pressed= false;
    boolean up_key_pressed = false;
    boolean down_key_pressed = false;

    //Mario's speed : default is 4px/tic
    float marioSpeed = 2;


    //Column numbers for run()
    int colStart = 80;
    int colEnd = 416;
    int colCurr = 80;


    //Grid : Each tile is sized 16 X 16
    private char[][] reachableOrNot = new char[15][16];

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
            "                " +
            "  ##            " +
            "  ##            " +
            "        ###     " +
            "               #" +
            "################" +
            "################";

    private String demoLevelTwo =
                    "################" +
                    "###       ####  " +
                    "                " +
                    "     ######   ##" +
                    "        ####    " +
                    "                " +
                    "     ######     " +
                    "#####      ##   " +
                    "           ##   " +
                    "# ##            " +
                    "# ####          " +
                    "# ####  ###     " +
                    "        ###    #" +
                    "################" +
                    "################";

    private String demoLevelThree =
            "################" +
            "            ####" +
            "#       ##      " +
            "#    ##    #####" +
            "##########     #" +
            "#        ##### #" +
            "#  ##### #   # #" +
            "#      # # # # #" +
            "###### #   # # #" +
            "#    # ##### # #" +
            "# ## # #     # #" +
            "#  # # # ##### #" +
            "## #   #       #" +
            "################" +
            "################";

    public void init() {
        // Load the sprite sheet image
        String spriteFile = "player.png";
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
        moveMario();

        ticks ++;
    }

    public void render() {
        // Draw the graphics to the screen
        marioImg = spriteSheet.getSubimage(colCurr, 32, marioWidth, marioHeight);
        blockImg = spriteSheet.getSubimage(colCurr, 80, 16, 16);
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
        for(int i = 0; i < reachableOrNot.length; i++){
            for (int j = 0; j < reachableOrNot[i].length; j++){
                if(reachableOrNot[i][j] == '#'){
                    g.drawImage(blockImg, j * 16, i * 16, null);
                }
            }
        }
        //draw new image
        g.drawImage(marioImg, (int)marioX, (int)marioY, null);

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
        
        //Just fpr this implementation. Will need to move it to tick()
        for(int i = 0; i < reachableOrNot.length; i++){
            for (int j = 0; j < reachableOrNot[i].length; j++){
                reachableOrNot[i][j] = demoLevelThree.charAt((i*reachableOrNot[i].length) + j);
            }
        }

        //The actual images start from the 80th pixel.
        prevTime = System.currentTimeMillis();
        int framesPerSec;
        int number_of_ticks = 60;
        double tickInterval = 1000 / number_of_ticks;

        //This keeps track of how much time elapsed
        double start;
        double end;
        double elapsed;

        //All images starts from 80px


        start = System.currentTimeMillis() / 1000;
        while (true) {
            // Main game loop

            // TODO: Make it run at 60 ticks per second with no framerate cap.
            //  Log to the console every second:
            //      - Ticks per second
            //      - Frames per second

            double now = System.currentTimeMillis();
            double timeDiff = now - prevTime;
            render();
            //As loop goes on, it will ignore if the time difference is less than the tick interval
            if(timeDiff >= tickInterval){
                now = System.currentTimeMillis();
                tick();

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
        }
    }

    public void moveMario(){
        int gridXscale = (int)(marioX/16);
        int gridYscale = (int)(marioY/16);

        //Checks if mario's position is between two grids
        boolean XinBetween = false;
        boolean YinBetween = false;
        if(marioX % 16 != 0){
            XinBetween = true;
        }
        if(marioY % 16 != 0){
            YinBetween = true;
        }

        //Checks if there is collision
        boolean noCollision;


        //Performs different task depending on keyboard input
        if(right_key_pressed){
            noCollision = safeToMove(gridXscale, gridYscale, "right", XinBetween, YinBetween);
            if(noCollision){
                marioX += marioSpeed;
            }
        }
        if(left_key_pressed){
            noCollision = safeToMove(gridXscale, gridYscale, "left", XinBetween, YinBetween);
            if(noCollision){
                marioX -= marioSpeed;
            }
        }
        if(up_key_pressed){
            noCollision = safeToMove(gridXscale, gridYscale, "up", XinBetween, YinBetween);
            if(noCollision){
                marioY -= marioSpeed;
            }
        }
        if(down_key_pressed){
            noCollision = safeToMove(gridXscale, gridYscale, "down", XinBetween, YinBetween);
            if(noCollision){
                marioY += marioSpeed;
            }
        }
    }

    public boolean safeToMove(int gridX, int gridY, String direction, boolean XinBetween, boolean YinBetween){
        //Boundaries for mario
        float marioUpperLineStart;
        float marioUpperLineEnd;
        float marioLowerLineStart;
        float marioLowerLineEnd;
        float marioLeftLineStart;
        float marioLeftLineEnd;
        float marioRightLineStart;
        float marioRightLineEnd;

        //Boundaries for blocks
        float blockUpperLineStart;
        float blockUpperLineEnd;
        float blockLowerLineStart;
        float blockLowerLineEnd;
        float blockLeftLineStart;
        float blockLeftLineEnd;
        float blockRightLineStart;
        float blockRightLineEnd;

        //First we need to check if mario's current position overlaps between two grids
        //Next, solve it in a trivial way if Mario is perfectly inside one grid
        //Otherwise check the NEXT grid's collision as well (int int casting drops the decimals)
        if(direction.equals("right")){
            marioRightLineStart = marioY;
            marioRightLineEnd = marioRightLineStart + 16;

            if(marioX >= 240 || marioX < 0){
                return true;
            }

            else if(!YinBetween && marioX < 240 && reachableOrNot[gridY][(int)((marioX + 16) / 16)] == '#'){
                blockLeftLineStart = gridY * 16;
                blockLeftLineEnd = blockLeftLineStart + 16;
                if(blockLeftLineEnd - marioRightLineStart >= 0 && marioRightLineEnd - blockLeftLineStart >= 0){
                    return false;
                }
            }
            else if(YinBetween && marioX < 240 && reachableOrNot[gridY][(int)((marioX + marioWidth) / 16)] == '#'){
                blockLeftLineStart = gridY * 16;
                blockLeftLineEnd = blockLeftLineStart + 16;
                if(blockLeftLineEnd - marioRightLineStart >= 0 && marioRightLineEnd - blockLeftLineStart >= 0){
                    return false;
                }
            }
            else if(YinBetween && marioX < 240 && reachableOrNot[gridY + 1][(int)((marioX + marioWidth) / 16)] == '#'){
                blockLeftLineStart = (gridY + 1) * 16;
                blockLeftLineEnd = blockLeftLineStart + 16;
                if(blockLeftLineEnd - marioRightLineStart >= 0 && marioRightLineEnd - blockLeftLineStart >= 0){
                    return false;
                }
            }

            if(reachableOrNot[gridY][(int)((marioX + 16) / 16)] != '#'){
                return true;
            }
        }
        else if(direction.equals("left")){
            marioLeftLineStart = marioY;
            marioLeftLineEnd = marioLeftLineStart + 16;
            if(marioX >= 240 || marioX < 0){
                return true;
            }

            else if(!YinBetween && marioX < 240 && reachableOrNot[gridY][(int)((marioX - marioSpeed) / 16)] == '#'){
                blockRightLineStart = gridY * 16;
                blockRightLineEnd = blockRightLineStart + 16;
                if(blockRightLineEnd - marioLeftLineStart >= 0 && marioLeftLineEnd - blockRightLineStart >= 0){
                    return false;
                }
            }
            else if(YinBetween && marioX < 240 && reachableOrNot[gridY][(int)((marioX - marioSpeed) / 16)] == '#'){
                blockRightLineStart = gridY * 16;
                blockRightLineEnd = blockRightLineStart + 16;
                if(blockRightLineEnd - marioLeftLineStart >= 0 && marioLeftLineEnd - blockRightLineStart >= 0){
                    return false;
                }
            }
            else if(YinBetween && marioX < 240 && reachableOrNot[gridY + 1][(int)((marioX + marioSpeed) / 16)] == '#'){
                blockRightLineStart = (gridY + 1) * 16;
                blockRightLineEnd = blockRightLineStart + 16;
                if(blockRightLineEnd - marioLeftLineStart >= 0 && marioLeftLineEnd - blockRightLineStart >= 0){
                    return false;
                }
            }

            if(reachableOrNot[gridY][(int)((marioX + marioWidth) / 16)] != '#'){
                return true;
            }
        }
        else if(direction.equals("up")){
            marioUpperLineStart = marioX;
            marioUpperLineEnd = marioUpperLineStart + 16;

            if(marioY <= 0 || marioY > 240){
                return true;
            }
            else if(!XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY - marioSpeed)/16)][gridX] == '#'){
                blockLowerLineStart = gridX * 16;
                blockLowerLineEnd = blockLowerLineStart + 16;
                if(marioUpperLineEnd - blockLowerLineStart >= 0 && blockLowerLineEnd - marioUpperLineStart >= 0){
                    return false;
                }
            }
            else if(XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY - marioSpeed)/16)][gridX] == '#'){
                blockLowerLineStart = gridX * 16;
                blockLowerLineEnd = blockLowerLineStart + 16;
                if(marioUpperLineEnd - blockLowerLineStart >= 0 && blockLowerLineEnd - marioUpperLineStart >= 0){
                    return false;
                }

            }
            else if(XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY - marioSpeed)/16)][gridX + 1] == '#'){
                blockLowerLineStart = (gridX + 1) * 16;
                blockLowerLineEnd = blockLowerLineStart + 16;
                if(marioUpperLineEnd - blockLowerLineStart >= 0 && blockLowerLineEnd - marioUpperLineStart >= 0){
                    return false;
                }

            }

            else if(reachableOrNot[(int)(marioY - marioSpeed)/16][gridX] != '#'){
                return true;
            }
        }

        else if(direction.equals("down")){
            marioLowerLineStart = marioX;
            marioLowerLineEnd = marioLowerLineStart + 16;

            if(marioY > 224 || marioY <= 0){
                return true;
            }
            //If Mario is perfectly in the grid
            else if(!XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY + marioHeight)/16)][gridX] == '#'){
                blockUpperLineStart = gridX * 16;
                blockUpperLineEnd = blockUpperLineStart + 16;
                if(marioLowerLineEnd - blockUpperLineStart >= 0 && blockUpperLineEnd - marioLowerLineStart >= 0){
                    return false;
                }
            }
            //If Mario is off the grid by a bit
            else if(XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY + marioHeight)/16)][gridX] == '#'){
                blockUpperLineStart = gridX * 16;
                blockUpperLineEnd = blockUpperLineStart + 16;
                if(marioLowerLineEnd - blockUpperLineStart >= 0 && blockUpperLineEnd - marioLowerLineStart >= 0){
                    return false;
                }
            }
            else if(XinBetween && marioY >= 0 && reachableOrNot[(int)((marioY + marioHeight)/16)][gridX + 1] == '#'){
                blockUpperLineStart = (gridX + 1) * 16;
                blockUpperLineEnd = blockUpperLineStart + 16;
                if(marioLowerLineEnd - blockUpperLineStart >= 0 && blockUpperLineEnd - marioLowerLineStart >= 0){
                    return false;
                }
            }

            else if(reachableOrNot[(int)((marioY + marioHeight)/16)][gridX] != '#'){
                return true;
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Handle key event
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle key down event
        int pressedKeyCode = e.getKeyCode();

        if(pressedKeyCode == VK_RIGHT){
            right_key_pressed = true;
        }


        if(pressedKeyCode == VK_LEFT){
            left_key_pressed = true;
        }


        if(pressedKeyCode == VK_UP){
            up_key_pressed = true;
        }


        if(pressedKeyCode == VK_DOWN){
            down_key_pressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Handle key up event

        //Make up_key_pressed = true ....
        //When key is released set them to false
        //No need to have 2 keycodes
        int releasedKeyCode = e.getKeyCode();

        if(releasedKeyCode == VK_RIGHT){
            right_key_pressed = false;
        }
        else if(releasedKeyCode == VK_LEFT){
            left_key_pressed = false;
        }
        else if(releasedKeyCode == VK_UP){
            up_key_pressed = false;
        }
        else if(releasedKeyCode == VK_DOWN){
            down_key_pressed = false;
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



