package samj.mario.game;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Sound {

    public enum SFX {
        ONE_UP("smb_1-up"),
        BOWSER_FALLS("smb_bowserfalls"),
        BOWSER_FIRE("smb_bowserfire"),
        BREAK_BLOCK("smb_breakblock"),
        BUMP("smb_bump"),
        COIN("smb_coin"),
        FIRE_BALL("smb_fireball"),
        FIREWORKS("smb_fireworks"),
        FLAGPOLE("smb_flagpole"),
        GAME_OVER("smb_gameover"),
        JUMP_SMALL("smb_jump-small"),
        JUMP_SUPER("smb_jump-super"),
        KICK("smb_kick"),
        MARIO_DIE("smb_mariodie"),
        PAUSE("smb_pause"),
        PIPE("smb_pipe"),
        POWER_UP("smb_powerup"),
        POWER_UP_APPEARS("smb_powerup_appears"),
        STAGE_CLEAR("smb_stage_clear"),
        STOMP("smb_stomp"),
        VINE("smb_vine"),
        WARNING("smb_warning"),
        WORLD_CLEAR("smb_world_clear");

        private final String fileName;

        SFX(String fileName) {
            this.fileName = fileName;
        }

        private String getFileName() {
            return fileName + ".wav";
        }
    }

    private static final String SFX_PATH = "sfx/";

    private static Map<SFX, Clip> soundClips;

    static {
        loadSoundClips();
    }

    public static void play(SFX sound) {
        Clip clip = soundClips.get(sound);
        if (clip != null) {
            // Restart the clip from the beginning
            clip.stop();
            clip.setFramePosition(0);
            // Play
            clip.start();
        } // Else the clip was never loaded
    }

    public static void stopAll() {
        for (Clip clip : soundClips.values()) {
            // Stop the clip and reset to the beginning
            clip.stop();
            clip.setFramePosition(0);
        }
    }

    private static void loadSoundClips() {
        soundClips = new HashMap<>();
        for (SFX sfx : SFX.values()) {
            String fileName = sfx.getFileName();
            URL url = Sound.class.getClassLoader().getResource(SFX_PATH + fileName);
            if (url != null) {
                try {
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                    DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
                    Clip clip = (Clip) AudioSystem.getLine(info);
                    clip.open(audioInputStream);
                    soundClips.put(sfx, clip);
                } catch (Exception e) {
                    System.out.println("SFX failed to load: " + fileName + " due to " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("SFX not found: " + fileName);
            }
        }
    }
}
