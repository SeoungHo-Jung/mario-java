package samj.mario.editor.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import samj.mario.editor.data.*;
import samj.mario.editor.io.json.JsonColor;
import samj.mario.editor.io.json.JsonEnemySpawn;
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
                if (tile.getEnemyType() != null) {
                    jsonTile.enemySpawn = new JsonEnemySpawn(tile.getEnemyType());
                }
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

        JsonLevel jsonLevel;

        try {
            jsonLevel = OBJECT_MAPPER.readValue(bytes, JsonLevel.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to deserialize level", e);
        }

        if (jsonLevel.tiles == null) {
            throw new RuntimeException("Failed to deserialize level: `tiles` is null");
        }

        List<Tile> tiles = new ArrayList<>();
        int width = 0;
        int height = jsonLevel.tiles.size();

        for (int y = 0; y < height; y++) {
            if (jsonLevel.tiles.get(y) == null) {
                throw new RuntimeException("Failed to deserialize level: `tiles[" + y +"]` is null");
            }
            if (y == 0) {
                // Get the level width from the first row
                width = jsonLevel.tiles.get(0).size();
            } else {
                // ensure all rows are the same length
                if (width != jsonLevel.tiles.get(y).size()) {
                    throw new RuntimeException("Failed to deserialize level: rows of `tiles` have uneven lengths");
                }
            }
            for (int x = 0; x < width; x++) {
                JsonTile jsonTile = jsonLevel.tiles.get(y).get(x);
                if (jsonTile == null) {
                    throw new RuntimeException("Failed to deserialize level: `tiles[" + y + "][" + x + "]` is null");
                }

                Tile.Builder builder = Tile.builder()
                        .setTileX(jsonTile.x)
                        .setTileY(jsonTile.y)
                        .setAnimated(jsonTile.isAnimated != null ? jsonTile.isAnimated : false)
                        .setType(jsonTile.type)
                        .setContainerType(jsonTile.containerType)
                        .setDirection(jsonTile.direction)
                        .setCount(jsonTile.containerCount);

                if (jsonTile.x != null && jsonTile.y != null) {
                    builder.setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, jsonTile.x, jsonTile.y));
                }

                if (jsonTile.enemySpawn != null) {
                    builder.setEnemyType(jsonTile.enemySpawn.type);
                }

                tiles.add(builder.build());
            }
        }

        if (height == 0) {
            throw new RuntimeException("Failed to deserialize level: `tiles` has zero height");
        }
        if (width == 0) {
            throw new RuntimeException("Failed to deserialize level: `tiles` has zero width");
        }

        TileMatrix tileMatrix = new TileMatrix(width, height, tiles);

        Level level = new Level();
        level.setDimensions(width, height);
        level.setTileMatrix(tileMatrix);

        return level;
    }
}
