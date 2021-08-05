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
    public Mario mario = new Mario();

    //List of sprite sheets in res
    public SpriteSheet playerSpriteSheet = new SpriteSheet("player");
    public SpriteSheet playerlSpriteSheet = new SpriteSheet("playerl");
    public SpriteSheet tilesSpriteSheet = new SpriteSheet("tiles");
    public SpriteSheet enemySpriteSheet = new SpriteSheet("enemy");
    public SpriteSheet enemyrSpriteSheet = new SpriteSheet("enemyr");

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
        marioImg = playerSpriteSheet.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);

        //clear the previous image that was drawn.
        g.clearRect(0, 0, frameWidth, frameHeight);
        int redrawFromHere = 0;
        if(mario.marioX > 128){
            redrawFromHere = (int)mario.marioX - (frameWidth / 2);
        }

        // Figure out why
        //tiles.get(y).get(x).type != Tile.TileType.EMPTY
        int offset = redrawFromHere % gridSize;
        for(int row = 0; row < tiles.size(); row++){
            for (int col = 0; col < tiles.get(row).size(); col++){
                if(col >= redrawFromHere / gridSize && col < (frameWidth + redrawFromHere) / gridSize + 1) {
                    if (tiles.get(row).get(col).type != Tile.TileType.EMPTY) {
                        blockImg = tilesSpriteSheet.loadedSpriteSheet.getSubimage(tiles.get(row).get(col).x * gridSize, tiles.get(row).get(col).y * gridSize, blockWidth, blockHeight);
                        g.drawImage(blockImg, (col - (redrawFromHere / gridSize)) * gridSize - offset, row * gridSize, null);
                    }
                }
            }
        }

        //draw new image
        g.drawImage(marioImg, (int)(mario.marioX - redrawFromHere), (int)mario.marioY, null);

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
            mario.marioImgX = 160;
        }
        if(status == MarioStatus.FALLING || status == MarioStatus.STANDING){
            mario.marioImgX = 80;
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

        float marioTempX = mario.marioX;
        float marioTempY = mario.marioY;

        mario.marioX += marioHorizontalSpeed;
        mario.marioY += marioVerticalSpeed;


        int gridXscale = (int)(mario.marioX/gridSize);
        int gridYscale = (int)(mario.marioY/gridSize);

        //Checks if mario's position is between two grids
        boolean XinBetween = false;
        boolean YinBetween = false;
        if(mario.marioX % gridSize != 0){
            XinBetween = true;
        }
        if(mario.marioY % gridSize != 0){
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
                mario.marioY = Math.round(marioTempY);
            }
            if(collisionLocation == Warning_Collide.UP){
                marioVerticalSpeed = 0;
                status = MarioStatus.FALLING;
                mario.marioY = Math.round(marioTempY);
            }
            if(collisionLocation == Warning_Collide.LEFT || collisionLocation == Warning_Collide.RIGHT){
                marioHorizontalSpeed = 0;
                mario.marioX = Math.round(marioTempX);
            }
        }
        else{
            if(status != MarioStatus.JUMPING)
                status = MarioStatus.FALLING;
        }

        if(mario.marioY >= frameHeight + mario.marioHeight){
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
        if(mario.marioX < 0 || mario.marioX > levelWidth - mario.marioWidth || mario.marioY < 0 || mario.marioY > levelHeight - mario.marioHeight){
            return true;
        }

        if(tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY){
            safeToMove = false;
        }

        if(YinBetween){
            if(marioHorizontalSpeed > 0 && tiles.get(gridY + 1).get((int)((mario.marioX + mario.marioWidth) / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;
            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY + 1).get((int)(mario.marioX / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;
            }

            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((mario.marioY + mario.marioHeight)/gridSize)).get(gridX).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;

                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(mario.marioY / gridSize)).get(gridX).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.UP;

            }
        }

        if(XinBetween){
            if(marioHorizontalSpeed > 0 && tiles.get(gridY).get((int)((mario.marioX + mario.marioWidth) / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;

            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY).get((int)(mario.marioX / gridSize)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;

            }
            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((mario.marioY + mario.marioHeight)/gridSize)).get(gridX + 1).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;

                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(mario.marioY / gridSize)).get(gridX + 1).type != Tile.TileType.EMPTY){
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
