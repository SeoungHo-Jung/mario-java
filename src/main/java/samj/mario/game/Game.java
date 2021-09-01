package samj.mario.game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;

import static java.lang.System.exit;
import static samj.mario.game.Application.CANVAS_HEIGHT;
import static samj.mario.game.Application.CANVAS_WIDTH;
import static java.awt.event.KeyEvent.*;

public class Game extends Canvas implements Runnable, KeyListener {
    public Mario mario = new Mario();
    private Mushroom shroom;

    //List of sprite sheets in res
    public final static SpriteSheet PLAYER_SPRITE_SHEET = new SpriteSheet("player");
    public final static SpriteSheet PLAYERL_SPRITE_SHEET = new SpriteSheet("playerl");
    public final static SpriteSheet TILES_SPRITE_SHEET = new SpriteSheet("tiles");
    public final static SpriteSheet ENEMY_SPRITE_SHEET = new SpriteSheet("enemy");
    public final static SpriteSheet ENEMYR_SPRITE_SHEET = new SpriteSheet("enemyr");
    public final static SpriteSheet ITEM_SPRITE_SHEET = new SpriteSheet("items");
    
    // Sprite Sheet
    private BufferedImage marioImg;
    private BufferedImage blockImg;

    private int ticks = 0;
    private int frames = 0;
    private double prevTime;

    //Size of grid is 16 pixel
    private static final int GRID_SIZE = 16;
    
    //Frame size
    private int frameWidth = CANVAS_WIDTH;
    private int frameHeight = CANVAS_HEIGHT;
    private int levelWidth;
    private int levelHeight;

    public int marioFrameHelper = 0;
    //Level and tiles
    List<List<Tile>> tiles;

    private Screen screen = new Screen();

    //mario's status /// ADD IT TO MARIO.JAVA
    enum MarioStatus{
        STANDING, JUMPING, FALLING, DEAD;
    }
    MarioStatus status = MarioStatus.STANDING;

    enum Last_Key_Pressed{
        RIGHT, LEFT, UP, DOWN, NULL;
    }

    enum Warning_Collide{
        RIGHT, LEFT, UP, DOWN, NULL;
    }
    Warning_Collide collisionLocation = Warning_Collide.NULL;

    //Block size;
    public final static int BLOCK_WIDTH = 16;
    public final static int BLOCK_HEIGHT = 16;

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

    //Mario's animation related variables
    int[] horizontalAnimation = {96, 112, 128};
    int horizontalAnimationIndex = 0;
    boolean showPowerUp = false;
    int powerUpBlockX = 0;
    int powerUpBlockY = 0;

    //Checks which key was pressed last
    Last_Key_Pressed lastKeyPressed = Last_Key_Pressed.NULL;

    public void init() {
        //Load level from json
        Level level;
        URL jsonFile = getClass().getClassLoader().getResource("levels/test-level.json");
        level = jsonParser.levelLoader(jsonFile);

        tiles = level.tiles;

        //Resize level size depending on the input
        levelHeight = tiles.size() * GRID_SIZE;
        levelWidth = tiles.get(0).size() * GRID_SIZE;

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

        //clear the previous image that was drawn.
        g.clearRect(0, 0, frameWidth, frameHeight);

        screen.setGraphics(g);
        drawBackground(g);
        drawSprites(screen);
        g.dispose();
        bs.show();
        frames++;
    }

    private void drawBackground(Graphics g) {
        // Draw the background to the screen
    }

    private void drawSprites(Screen screen) {
        //ANIMATION
        boolean getOppositeImg = false;
        if (status != MarioStatus.JUMPING && marioVerticalSpeed == 0) {
            if (horizontalAnimationIndex >= horizontalAnimation.length - 1) {
                horizontalAnimationIndex = 0;
                marioFrameHelper = 0;
            } else {
                if (marioFrameHelper >= 4) {
                    marioFrameHelper = 0;
                    horizontalAnimationIndex++;
                }
            }
        }

        if (status != MarioStatus.JUMPING && Math.round(marioVerticalSpeed) == 0 && marioHorizontalSpeed != 0) {
            if ((right_key_pressed && marioHorizontalSpeed < 0) || (left_key_pressed && marioHorizontalSpeed > 0)) {
                mario.marioImgX = 144;
                getOppositeImg = true;
            } else {
                mario.marioImgX = horizontalAnimation[horizontalAnimationIndex];
            }
        } else if (status == MarioStatus.JUMPING && up_key_pressed) {
            mario.marioImgX = 160;
        } else if (Math.round(marioHorizontalSpeed) == 0 && Math.round(marioVerticalSpeed) == 0) {
            mario.marioImgX = 80;
        }
        marioFrameHelper++;

        // Check if we want the right side image or the left side image
        rightOrLeft(getOppositeImg);

        int redrawFromHere = 0;
        if (mario.marioX > 128) {
            redrawFromHere = (int) mario.marioX - (frameWidth / 2);
        }
        screen.setXOffset(redrawFromHere);
        
        for (int row = 0; row < tiles.size(); row++) {
            for (int col = 0; col < tiles.get(row).size(); col++) {
                if (tiles.get(row).get(col).type != Tile.TileType.EMPTY) {
                    blockImg = TILES_SPRITE_SHEET.loadedSpriteSheet.getSubimage(tiles.get(row).get(col).x * GRID_SIZE, tiles.get(row).get(col).y * GRID_SIZE, BLOCK_WIDTH, BLOCK_HEIGHT);
                    screen.drawToScreen(blockImg, col * GRID_SIZE, row * GRID_SIZE);
                }
            }
        }
        //draw new Mario image
        screen.drawToScreen(marioImg, (int) mario.marioX, (int) mario.marioY);

        // draw Mushroom
        if (shroom != null) {
            shroom.render(screen);
        }
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
                mario.marioImgX = 144;
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


        int gridXscale = (int)(mario.marioX/ GRID_SIZE);
        int gridYscale = (int)(mario.marioY/ GRID_SIZE);

        //Checks if mario's position is between two grids
        boolean XinBetween = false;
        boolean YinBetween = false;
        if(mario.marioX % GRID_SIZE != 0){
            XinBetween = true;
        }
        if(mario.marioY % GRID_SIZE != 0){
            YinBetween = true;
        }

        List<String> collisionCheckList = new ArrayList<String>();
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
                if(collisionLocation != Warning_Collide.DOWN){
                    status = MarioStatus.FALLING;
                }
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

        //Deal with animation
        //System.out.println("Collsion is : " + collisionLocation);
        //System.out.println("Mario is : " + status);

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
            if(marioHorizontalSpeed > 0 && tiles.get(gridY + 1).get((int)((mario.marioX + mario.marioWidth) / GRID_SIZE)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;
            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY + 1).get((int)(mario.marioX / GRID_SIZE)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;
            }

            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((mario.marioY + mario.marioHeight)/ GRID_SIZE)).get(gridX).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;
                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(mario.marioY / GRID_SIZE)).get(gridX).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.UP;
                if(tiles.get((int)(mario.marioY / GRID_SIZE)).get(gridX).type == Tile.TileType.CONTAINER && tiles.get((int)(mario.marioY / GRID_SIZE)).get(gridX).containerType == Tile.ContainerType.POWER_UP){
                    shroom = new Mushroom(gridX * GRID_SIZE, (gridY - 1) * GRID_SIZE);
                }
            }
        }

        if(XinBetween){
            if(marioHorizontalSpeed > 0 && tiles.get(gridY).get((int)((mario.marioX + mario.marioWidth) / GRID_SIZE)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.RIGHT;

            }
            if(marioHorizontalSpeed < 0 && tiles.get(gridY).get((int)(mario.marioX / GRID_SIZE)).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.LEFT;

            }
            if(marioVerticalSpeed > 0){
                if(tiles.get((int)((mario.marioY + mario.marioHeight)/ GRID_SIZE)).get(gridX + 1).type != Tile.TileType.EMPTY){
                    safeToMove = false;
                    collisionLocation = Warning_Collide.DOWN;

                }
            }
            if(marioVerticalSpeed < 0 && tiles.get((int)(mario.marioY / GRID_SIZE)).get(gridX + 1).type != Tile.TileType.EMPTY){
                safeToMove = false;
                collisionLocation = Warning_Collide.UP;
                if(tiles.get((int)(mario.marioY / GRID_SIZE)).get(gridX + 1).containerType != Tile.ContainerType.POWER_UP){
                    shroom = new Mushroom(gridX * GRID_SIZE, (gridY - 1) * GRID_SIZE);
                }
            }
        }
        //tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY
        if(!YinBetween && !XinBetween){
            if(tiles.get(gridY).get(gridX).type != Tile.TileType.EMPTY){
                safeToMove = false;
                if(tiles.get(gridY).get(gridX).containerType != Tile.ContainerType.POWER_UP && tiles.get(gridY).get(gridX).containerType != Tile.ContainerType.POWER_UP){
                    shroom = new Mushroom(gridX * GRID_SIZE, (gridY - 1) * GRID_SIZE);
                }
            }
        }
        if(safeToMove){
            collisionLocation = Warning_Collide.NULL;
        }

        return safeToMove;
    }

    public void rightOrLeft(boolean getOppositeImg){
        if(marioHorizontalSpeed > 0){
            if(getOppositeImg){
                marioImg = PLAYERL_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
            }
            else{
                marioImg = PLAYER_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
            }
        }
        if(marioHorizontalSpeed < 0){
            if(getOppositeImg){
                marioImg = PLAYER_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
            }
            else{
                marioImg = PLAYERL_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
            }
        }
        if(marioHorizontalSpeed == 0){
            if(lastKeyPressed == Last_Key_Pressed.RIGHT || lastKeyPressed == Last_Key_Pressed.NULL){
                marioImg = PLAYER_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
            }
            if(lastKeyPressed == Last_Key_Pressed.LEFT){
                marioImg = PLAYERL_SPRITE_SHEET.loadedSpriteSheet.getSubimage(mario.marioImgX, mario.marioImgY, mario.marioWidth, mario.marioHeight);
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
