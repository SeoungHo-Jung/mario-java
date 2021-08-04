package samj.mario.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static java.lang.System.exit;
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

    //Size of grid is 16 pixel
    private final int gridSize = 16;
    //Frame size
    private int frameWidth = CANVAS_WIDTH;
    private int frameHeight = CANVAS_HEIGHT;
    private int levelWidth;
    private int levelHeight;

    //Level and tiles
    List<List<Tile>> tiles;


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
    boolean[] collisionList = new boolean[4];

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

    public void init() {
        //Load level from json
        Level level;
        URL jsonFile = getClass().getClassLoader().getResource("levels/test-level.json");
        level = jsonParser.levelLoader(jsonFile);

        tiles = level.tiles;

        //Resize level size depending on the input
        levelHeight = tiles.size() * gridSize;
        levelWidth = tiles.get(0).size() * gridSize;


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
        // Draw the graphics to the screen
        marioImg = spriteSheet.getSubimage(colCurr, 32, marioWidth, marioHeight);

        //clear the previous image that was drawn.
        g.clearRect(0, 0, frameWidth, frameHeight);
        int redrawFromHere = 0;
        if(marioX > 128){
            redrawFromHere = (int)marioX - (frameWidth / 2);
        }

        // Figure out why
        //tiles.get(y).get(x).type != Tile.TileType.EMPTY
        int offset = redrawFromHere % gridSize;
        for(int row = 0; row < tiles.size(); row++){
            for (int col = 0; col < tiles.get(row).size(); col++){
                if(col >= redrawFromHere / gridSize && col < (frameWidth + redrawFromHere) / gridSize + 1) {
                    if (tiles.get(row).get(col).type != Tile.TileType.EMPTY) {
                        blockImg = tileSpriteSheet.getSubimage(tiles.get(row).get(col).x * gridSize, tiles.get(row).get(col).y * gridSize, blockWidth, blockHeight);
                        g.drawImage(blockImg, (col - (redrawFromHere / gridSize)) * gridSize - offset, row * gridSize, null);
                    }
                }
            }
        }

        //draw new image
        g.drawImage(marioImg, (int)(marioX - redrawFromHere), (int)marioY, null);

    }


    @Override
    public void run() {
        init();


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
        if(!right_key_pressed && !left_key_pressed){
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
        }

        if(status == MarioStatus.FALLING){
            marioVerticalSpeed += gravity;
        }

        float marioTempX = marioX;
        float marioTempY = marioY;

        marioX += marioHorizontalSpeed;
        marioY += marioVerticalSpeed;


        int gridXscale = (int)(marioX/gridSize);
        int gridYscale = (int)(marioY/gridSize);

        //Checks if mario's position is between two grids
        boolean XinBetween = false;
        boolean YinBetween = false;
        if(marioX % gridSize != 0){
            XinBetween = true;
        }
        if(marioY % gridSize != 0){
            YinBetween = true;
        }


        noCollision = safeToMove(gridXscale, gridYscale, XinBetween, YinBetween);

        //Not a safe place, move back to original position
        if(!noCollision){
            if(collisionLocation != Warning_Collide.DOWN){
                status = MarioStatus.FALLING;
            }
            if(collisionLocation == Warning_Collide.DOWN){
                marioVerticalSpeed = 0;
                status = MarioStatus.STANDING;
                marioY = Math.round(marioTempY);
            }
            if(collisionLocation == Warning_Collide.UP){
                marioVerticalSpeed = 0;
                status = MarioStatus.FALLING;
                marioY = Math.round(marioTempY);
            }
            if(collisionLocation == Warning_Collide.LEFT || collisionLocation == Warning_Collide.RIGHT){
                marioHorizontalSpeed = 0;
                marioX = Math.round(marioTempX);
            }
        }
        else{
            if(status != MarioStatus.JUMPING)
                status = MarioStatus.FALLING;
        }

        if(marioY >= frameHeight + marioHeight){
            status = MarioStatus.DEAD;
            exit(0);
        }

        /*
        float midPoint = (redrawFromHere + frameWidth)/2;
        if(marioX > midPoint){
            marioX = marioTempX;
        }

         */

        //System.out.println("Mario is " + status);
        //System.out.println("collision location is : " + collisionLocation);
        //System.out.println("\n mario Y is : " + marioY);
    }

    public boolean safeToMove(int gridX, int gridY, boolean XinBetween, boolean YinBetween){

        boolean safeToMove = true;

        //If mario is out of frame, it returns true no matter what
        if(marioX < 0 || marioX > levelWidth - marioWidth || marioY < 0 || marioY > levelHeight - marioHeight){
            return true;
        }

        if(tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY){
            safeToMove = false;
        }

        if(YinBetween){
            if(marioHorizontalSpeed > 0 && tiles.get(gridY + 1).get((int)((marioX + marioWidth) / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;
            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY + 1).get((int)(marioX / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;
            }

            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((marioY + marioHeight)/gridSize)).get(gridX).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;

                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(marioY / gridSize)).get(gridX).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.UP;

            }
        }

        if(XinBetween){
            if(marioHorizontalSpeed > 0 && tiles.get(gridY).get((int)((marioX + marioWidth) / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;

            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY).get((int)(marioX / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;

            }
            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((marioY + marioHeight)/gridSize)).get(gridX + 1).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;

                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(marioY / gridSize)).get(gridX + 1).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.UP;

            }
        }
        //tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY
        if(!YinBetween || !XinBetween){
            if(tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY){
                safeToMove = false;
            }
        }
        if(safeToMove){
            collisionLocation = Warning_Collide.NULL;
        }

        return safeToMove;
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


//Dealing with levels
/*
1. "empty" and "solid" blocks.
2. Solid blocks will draw the correct tiles based on x and y coordinates.
3. Background = "black" (file will support bg color, disregard for now).

a. Work on retrieving stuff from json (parsing).
   Look up for "jackson"
b. Actually use the retrieved data to build the level
c. Test.
 */
