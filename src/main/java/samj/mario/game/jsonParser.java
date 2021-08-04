package samj.mario.game;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class jsonParser {


    public static Level levelLoader(URL fileName){
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(fileName.getPath());
        try {
            return objectMapper.readValue(file, Level.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to load level", e);
        }

    }
}
