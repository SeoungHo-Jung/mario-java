package samj.mario.editor.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static samj.mario.editor.util.Json.OBJECT_MAPPER;

public class TileData {
    public static List<TileDefinition> TILE_DEFINITIONS = getTileDefinitions();

    private static List<TileDefinition> getTileDefinitions() {

        String tileDefJson =
                """
                [
                    {"x": 11, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 12, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 13, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 14, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 11, "y": 1, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 12, "y": 1, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 13, "y": 1, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 8, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 9, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 10, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 8, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 9, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 10, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 11, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 12, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 13, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 16, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 16, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 0, "y": 20, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 1, "y": 20, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 2, "y": 20, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 0, "y": 21, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 1, "y": 21, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 2, "y": 21, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["BACKGROUND"]},
                    {"x": 0, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID", "BOUNCE", "BREAKABLE", "CONTAINER"], "allowedContainerTypes": ["COIN", "STAR", "ONE_UP", "POWER_UP"]},
                    {"x": 0, "y": 1, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 0, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 0, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 2, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 3, "y": 8, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 2, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 3, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 24, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["CONTAINER", "SOLID"], "allowedContainerTypes": ["COIN", "STAR", "ONE_UP", "POWER_UP"]},
                    {"paletteCount": 1, "isAnimated": true, "allowedTypes": ["COIN"]}
                ]
                """;

        List<TileDefinition> tileDefs;
        try {
            tileDefs = OBJECT_MAPPER.readValue(tileDefJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't deserialize tile config string", e);
        }

        // Create a prototypical default Tile for each tile definition
        for (TileDefinition tileDef : tileDefs) {
            Integer x = tileDef.x;
            Integer y = tileDef.y;
            tileDef.prototype = Tile.builder()
                    .setTileX(x)
                    .setTileY(y)
                    .setType(tileDef.allowedTypes.get(0)) // Use the first allowed type as the default type for this tile
                    .setAllowedTileTypes(tileDef.allowedTypes)
                    .setAllowedContainerTypes(tileDef.allowedContainerTypes)
                    .build();
        }

        return tileDefs;
    }
}
