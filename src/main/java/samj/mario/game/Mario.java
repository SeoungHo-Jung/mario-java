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

    public Mario(){
        //small mario's size is 16 X 16 pixels
        this.marioWidth = 16;
        this.marioHeight = 16;
        //mario's initial position, may change
        this.marioX = 32;
        this.marioY = 192;
        //this.marioStatus = "STANDING";
        //small mario position on sprite sheet
        this.marioImgX = 80;
        this.marioImgY = 32;
    }
}
