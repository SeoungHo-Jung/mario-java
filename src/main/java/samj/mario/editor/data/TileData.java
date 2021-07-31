package samj.mario.editor.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static samj.mario.editor.util.Json.OBJECT_MAPPER;

public class TileData {
    public static List<Tile> TILES;
    public static Map<Short, Tile> TILES_BY_INDEX;
    public static Map<Character, Tile> TILES_BY_CHAR;

    static {
        List<TileDefinition> tileDefs = getTileDefinitions();

        List<Tile> fgTiles = new ArrayList<>();
        fgTiles.add(Tile.EMPTY_TILE);

        for (TileDefinition tileDef : tileDefs) {
            List<TileType> types = tileDef.allowedTypes;
            List<ContainerType> containerTypes = tileDef.allowedContainerTypes;
            final int paletteCount = tileDef.paletteCount;
            if (containerTypes == null) {
                containerTypes = Collections.singletonList(null); // hack to still loop once
            }
            for (TileType type : types) {
                for (ContainerType containerType : containerTypes) {
                    for (int palette = 0; palette < paletteCount; palette++) {
                        final int x = tileDef.x;
                        final int y = tileDef.y + (2 * palette); // each palette is spaced 2 rows apart
                        Tile.Builder builder = Tile.builder()
                                .setType(type)
                                .setContainerType(containerType)
                                .setEnemyType(tileDef.enemyType)
                                .setTileX(x)
                                .setTileY(y)
                                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, x, y))
                                .setAnimated(tileDef.isAnimated);

                        // set defaults
                        switch (type) {
                            case CONTAINER -> builder.setCount(1);
                            case TRANSPORT_ENTRANCE, TRANSPORT_EXIT -> builder.setDirection(Direction.DOWNWARD);
                        }

                        Tile tile = builder.build();
                        fgTiles.add(tile);
                    }
                }
            }
        }

        TILES = Collections.unmodifiableList(fgTiles);

        // Create a hashmap of tiles for quick lookup by char
        TILES_BY_CHAR = TILES.stream()
                .filter(tile -> tile.getTileChar() != '\0')
                .collect(Collectors.toUnmodifiableMap(Tile::getTileChar, Function.identity()));

        // Create a hashmap of tiles for quick lookup by index
        TILES_BY_INDEX = TILES.stream()
                .filter(tile -> tile.getTileIndex() != -1)
                .collect(Collectors.toUnmodifiableMap(Tile::getTileIndex, Function.identity()));
    }

    private static List<TileDefinition> getTileDefinitions() {
        String tileDefJson =
                """
                [
                    {"x": 0, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 2, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 0, "y": 1, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 0, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 9, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 0, "y": 10, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 10, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 24, "y": 0, "paletteCount": 1, "isAnimated": false, "allowedTypes": ["SOLID"]}
                ]
                """;

        List<TileDefinition> tileDefs;
        try {
            tileDefs = OBJECT_MAPPER.readValue(tileDefJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't deserialize tile config string", e);
        }
        return tileDefs;
    }
}
