package samj.mario.game;
import java.util.*;
public class Mario {
    int marioWidth;
    int marioHeight;
    float marioX;
    float marioY;
    //String marioStatus;
    int marioImgX;
    int marioImgY;
    int lifeCount;
    int earnedPoints;


    public Mario(){
        //this.marioStatus = "STANDING";

        //small mario's size is 16 X 16 pixels
        marioWidth = 16;
        marioHeight = 16;
        //mario's initial position, may change
        marioX = 32;
        marioY = 192;
        //small mario position on sprite sheet
        marioImgX = 80;
        marioImgY = 32;
        //Player info
        lifeCount = 3;
        earnedPoints = 0;
    }
}
