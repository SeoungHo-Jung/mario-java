package samj.mario.editor.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import samj.mario.editor.data.Level;
import samj.mario.editor.data.Tile;
import samj.mario.editor.io.json.JsonColor;
import samj.mario.editor.io.json.JsonLevel;
import samj.mario.editor.io.json.JsonTile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static samj.mario.editor.util.Json.OBJECT_MAPPER;

public class JsonLevelFormat implements LevelFormat {

    @Override
    public byte[] encode(Level level) {

        List<List<JsonTile>> jsonTiles = new ArrayList<>();
        for (int y = 0; y < level.getHeight(); y++) {
            List<JsonTile> jsonTilesRow = new ArrayList<>();
            for (int x = 0; x < level.getWidth(); x++) {
                Tile tile = level.getTileMatrix().getTile(x, y);
                JsonTile jsonTile = new JsonTile();
                jsonTile.type = tile.getType();
                jsonTile.containerType = tile.getContainerType();
                jsonTile.enemyType = tile.getEnemyType();
                jsonTile.direction = tile.getDirection();
                jsonTile.containerCount = tile.getCount();
                jsonTile.x = tile.getTileX();
                jsonTile.y = tile.getTileY();
                jsonTile.isAnimated = tile.isAnimated();
                jsonTilesRow.add(jsonTile);
            }
            jsonTiles.add(jsonTilesRow);
        }

        JsonLevel jsonLevel = new JsonLevel();
        jsonLevel.tiles = jsonTiles;
        jsonLevel.backgroundColor = new JsonColor(0, 0, 0); // TODO
        jsonLevel.name = "World 1-1"; // TODO
        jsonLevel.seconds = 300; // TODO

        try {
            return OBJECT_MAPPER.writeValueAsBytes(jsonLevel);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to serialize level", e);
        }
    }

    @Override
    public Level decode(byte[] bytes) {

        try {
            OBJECT_MAPPER.readValue(bytes, JsonLevel.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to deserialize level", e);
        }

        return null;
    }
}
