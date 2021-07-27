package samj.mario.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static samj.mario.game.Application.CANVAS_HEIGHT;
import static samj.mario.game.Application.CANVAS_WIDTH;
import static java.awt.event.KeyEvent.*;

public class Game extends Canvas implements Runnable, KeyListener {


    // Sprite Sheet
    private BufferedImage spriteSheet;
    private BufferedImage marioImg;
    private BufferedImage blockImg;

    private BufferedImage tileSpriteSheet;

    private int ticks = 0;
    private int frames = 0;
    private double prevTime;

    //Frame size
    private int frameWidth = CANVAS_WIDTH;
    private int frameHeight = CANVAS_HEIGHT;

    //mario's status
    enum MarioStatus{
        STANDING, JUMPING, FALLING, DEAD;
    }
    MarioStatus status = MarioStatus.STANDING;

    enum Last_Key_Pressed{
        RIGHT, LEFT, UP, DOWN;
    }

    enum Warning_Collide{
        RIGHT, LEFT, UP, DOWN, NULL;
    }
    Warning_Collide collisionLocation = Warning_Collide.NULL;

    //Mario's positions : initialized as
    float marioX = 32;
    float marioY = 192;


    //Mario's size : initialized as 16 X 16
    int marioWidth = 16;
    int marioHeight = 16;

    //Block size;
    private final int blockWidth = 16;
    private final int blockHeight = 16;

    // Keys
    boolean right_key_pressed = false;
    boolean left_key_pressed= false;
    boolean up_key_pressed = false;
    boolean down_key_pressed = false;


    //Mario's speed : default is 4px/tic
    double marioHorizontalSpeed = 0;
    double marioVerticalSpeed = 0;
    double acceleration = 0.1;
    double jumpAcceleration = 0.07;
    double gravity = 0.12;
    double marioMaxSpeed = 2.50;
    double marioMinSpeed = 0;


    //Column numbers for run()
    int colStart = 80;
    int colEnd = 416;
    int colCurr = 80;

    //Checks if Mario is out of the screen or not
    boolean outOfFrame;

    //Checks which key was pressed last
    Last_Key_Pressed lastKeyPressed;

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
                    "   ###          " +
                    "   ###          " +
                    "        ###     " +
                    "#              #" +
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
                    "#       ###    #" +
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
        String spriteFile = "image/player.png";
        URL imageURL = getClass().getClassLoader().getResource(spriteFile);

        String tileSpriteFile = "image/tiles.png";
        URL tileImageURL = getClass().getClassLoader().getResource(tileSpriteFile);
        if (imageURL == null || tileImageURL == null) { //tileImageURL == null
            System.err.println("Couldn't find sprite file: " + spriteFile);
        } else {
            try {
                BufferedImage in = ImageIO.read(imageURL);
                spriteSheet = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
                spriteSheet.getGraphics().drawImage(in, 0, 0, null);

                BufferedImage in2 = ImageIO.read(tileImageURL);
                tileSpriteSheet = new BufferedImage(in2.getWidth(), in2.getHeight(), BufferedImage.TYPE_INT_ARGB);
                tileSpriteSheet.getGraphics().drawImage(in2, 0, 0, null);
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


            int  tileTransparentColor = 0;
            for (int i = 0; i < tileSpriteSheet.getWidth(); i++) {
                for (int j = 0; j < tileSpriteSheet.getHeight(); j++) {
                    int[] tilePixel = tileSpriteSheet.getRaster().getPixel(i, j, (int[]) null);
                    int tileRgbColor = (tilePixel[0] << 16) | tilePixel[1] << 8 | tilePixel[2];
                    int[] tilePixelCopy = Arrays.copyOf(tilePixel, tilePixel.length);
                    tilePixelCopy[3] = tileRgbColor == transparentColor ? 0x00 : 0xFF;
                    tileSpriteSheet.getRaster().setPixel(i, j, tilePixelCopy);
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
        //Mystery numbers. Must fix
        blockImg = tileSpriteSheet.getSubimage(0, 0, blockWidth, blockHeight);
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
        g.clearRect(0, 0, frameWidth, frameHeight);
        for(int i = 0; i < reachableOrNot.length; i++){
            for (int j = 0; j < reachableOrNot[i].length; j++){
                if(reachableOrNot[i][j] == '#'){
                    g.drawImage(blockImg, j * blockWidth, i * blockHeight, null);
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
                reachableOrNot[i][j] = demoLevel.charAt((i*reachableOrNot[i].length) + j);
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

        if(status == MarioStatus.JUMPING){
            colCurr = 160;
        }
        if(status == MarioStatus.FALLING || status == MarioStatus.STANDING){
            colCurr = 80;
        }

        //Checks if there is collision
        boolean noCollision = false;

        //Performs different task depending on keyboard input
        if(right_key_pressed){
            lastKeyPressed = Last_Key_Pressed.RIGHT;
            if (marioHorizontalSpeed <= marioMaxSpeed) {
                marioHorizontalSpeed += acceleration;
            }
        }
        if(left_key_pressed){
            lastKeyPressed = Last_Key_Pressed.LEFT;
            if(marioHorizontalSpeed < 0){
                if(Math.abs(marioHorizontalSpeed) <= marioMaxSpeed) {
                    marioHorizontalSpeed -= acceleration;
                }
            }
            else{
                marioHorizontalSpeed -= acceleration;
            }
        }
        if(up_key_pressed){
            //System.out.println("mario speed is : " + marioVerticalSpeed);
            lastKeyPressed = Last_Key_Pressed.UP;
            if(status == MarioStatus.STANDING){
                marioVerticalSpeed = -3.5;
                status = MarioStatus.JUMPING;
            }
            if(status == MarioStatus.JUMPING) {
                marioVerticalSpeed += jumpAcceleration;
            }
            if(marioVerticalSpeed >= 0){
                status = MarioStatus.FALLING;
            }

        }
        /*
        if(down_key_pressed){
            noCollision = safeToMove(gridXscale, gridYscale, "down", XinBetween, YinBetween);
            if(noCollision){
                marioY += marioHorizontalSpeed;
            }
        }

         */

        //When released

        if(status == MarioStatus.JUMPING && !up_key_pressed){
            status = MarioStatus.FALLING;
        }

        if(!right_key_pressed && !left_key_pressed && !up_key_pressed && !down_key_pressed){
            status = MarioStatus.FALLING;
            if(marioHorizontalSpeed >= marioMinSpeed){
                marioHorizontalSpeed -= acceleration;
                if(marioHorizontalSpeed < 0){
                    marioHorizontalSpeed = 0;
                }
            }
            if(marioHorizontalSpeed <= marioMinSpeed){
                marioHorizontalSpeed += acceleration;
                if(marioHorizontalSpeed > 0){
                    marioHorizontalSpeed = 0;
                }
            }

            if(status == MarioStatus.FALLING){
                marioVerticalSpeed += gravity;
            }
        }

        if(status == MarioStatus.FALLING){
            marioVerticalSpeed += gravity;
        }

        float marioTempX = marioX;
        float marioTempY = marioY;

        marioX += marioHorizontalSpeed;
        marioY += marioVerticalSpeed;


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


        noCollision = safeToMove(gridXscale, gridYscale, XinBetween, YinBetween);

        //Not a safe place, move back to original position
        if(!noCollision){
            if(collisionLocation == Warning_Collide.DOWN){
                marioVerticalSpeed = 0;
                status = MarioStatus.STANDING;
            }
            if(collisionLocation == Warning_Collide.UP){
                marioVerticalSpeed = 0;
                status = MarioStatus.FALLING;
            }
            if(collisionLocation == Warning_Collide.LEFT || collisionLocation == Warning_Collide.RIGHT){
                marioHorizontalSpeed = 0;
                //status = MarioStatus.STANDING;
            }
            marioX = Math.round(marioTempX);
            marioY = Math.round(marioTempY);
        }

        //System.out.println("Mario is " + status);
        //System.out.println("\n mario Y is : " + marioY);
    }

    public boolean safeToMove(int gridX, int gridY, boolean XinBetween, boolean YinBetween){
        /*
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

         */

        boolean check = true;

        //If mario is out of frame, it returns true no matter what
        if(marioX < 0 || marioX > frameWidth - marioWidth || marioY < 0 || marioY > frameHeight - marioHeight){
            return true;
        }

        if(YinBetween){
            if(right_key_pressed && reachableOrNot[gridY + 1][(int)((marioX + marioWidth) / 16)] == '#'){
                check = false;
                collisionLocation = Warning_Collide.RIGHT;
            }
            if(left_key_pressed && reachableOrNot[gridY + 1][(int)(marioX / 16)] == '#'){
                check = false;
                collisionLocation = Warning_Collide.LEFT;
            }


            if(status == MarioStatus.FALLING){
                if(reachableOrNot[(int)((marioY + marioHeight)/16)][gridX] == '#'){
                    check = false;
                    collisionLocation = Warning_Collide.DOWN;
                }
            }
            if(status == MarioStatus.JUMPING && reachableOrNot[(int)(marioY / 16)][gridX] == '#'){
                //System.out.println("here");
                check = false;
                collisionLocation = Warning_Collide.UP;
            }
        }

        if(XinBetween){
            if(right_key_pressed && reachableOrNot[gridY][(int)((marioX + marioWidth) / 16)] == '#'){
                check = false;
                collisionLocation = Warning_Collide.RIGHT;
            }
            if(left_key_pressed && reachableOrNot[gridY][(int)(marioX / 16)] == '#'){
                check = false;
                collisionLocation = Warning_Collide.LEFT;
            }
            if(status == MarioStatus.FALLING){
                if(reachableOrNot[(int)((marioY + marioHeight)/16)][gridX + 1] == '#'){
                    check = false;
                    collisionLocation = Warning_Collide.DOWN;
                }
            }
            if(status == MarioStatus.JUMPING && reachableOrNot[(int)(marioY / 16)][gridX + 1] == '#'){
                check = false;
                collisionLocation = Warning_Collide.UP;
            }
        }

        if(!YinBetween){
            if(reachableOrNot[gridY][gridX] == '#'){
                return false;
            }
        }
        if(!XinBetween){
            if(reachableOrNot[gridY][gridX] == '#'){
                return false;
            }
        }

        return check;
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

}



