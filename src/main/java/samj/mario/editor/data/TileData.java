package samj.mario.editor.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import samj.mario.editor.io.json.JsonContainerType;
import samj.mario.editor.io.json.JsonDirection;
import samj.mario.editor.io.json.JsonEnemyType;
import samj.mario.editor.io.json.JsonTileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TileData {
    public static List<Tile> TILES;
    public static Map<Short, Tile> TILES_BY_INDEX;
    public static Map<Character, Tile> TILES_BY_CHAR;

    static {
        List<Tile> fgTiles = new ArrayList<>();

//        fgTiles.add(Tile.EMPTY_TILE);

//        // Rock
//        fgTiles.add(Tile.builder()
//                .setType(JsonTileType.SOLID)
//                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 0, 0))
//                .setTileX(0)
//                .setTileY(0)
//                .setTileIndex((short) 0x0010)
//                .setTileChar('#')
//                .setName("Rock ?")
//                .build());
//
//        // Question Box
//        fgTiles.add(Tile.builder()
//                .setType(JsonTileType.CONTAINER)
//                .setContainerType(JsonContainerType.COIN)
//                .setCount(1)
//                .setTileX(24)
//                .setTileY(0)
//                .setAnimated(true)
//                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 24, 0))
//                .setTileIndex((short) 0x0020)
//                .setTileChar('?')
//                .setName("Question Mark Box")
//                .build());
//
//        // Coin
//        fgTiles.add(Tile.builder()
//                .setType(JsonTileType.COIN)
//                .setAnimated(true)
//                .setTileX(24)
//                .setTileY(1)
//                .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, 24, 1))
//                .setTileIndex((short) 0x0030)
//                .setTileChar('o')
//                .setName("Coin")
//                .build());

        String tileDefJson =
                """
                [
                    {"x": 0, "y": 0, "paletteCount": 4, "isAnimated": false, "allowedTypes": ["SOLID"]},
                    {"x": 1, "y": 0, "paletteCount": 4, "isAnimated": false, "allowedTypes": ["SOLID", "BOUNCE", "CONTAINER", "BREAKABLE"], "allowedContainerTypes": ["COIN"]},
                    {"x": 9, "y": 0, "paletteCount": 4, "isAnimated": false, "allowedTypes": ["ENEMY_SPAWN"], "enemyType": "BULLET_BILL"},
                    {"x": 24, "y": 0, "paletteCount": 4, "isAnimated": false, "allowedTypes": ["CONTAINER"], "allowedContainerTypes": ["COIN", "ONE_UP", "POWER_UP", "STAR"]}
                ]
                """;

        ObjectMapper objectMapper = new ObjectMapper();

        List<TileDefinition> tileDefs = null;
        try {
            tileDefs = objectMapper.readValue(tileDefJson, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't deserialize tile config string", e);
        }

//        tileDefs.add(new TileDefinition(0, 0, 4, false, List.of(JsonTileType.SOLID), null, null));
//        tileDefs.add(new TileDefinition(1, 0, 4, false, List.of(JsonTileType.SOLID, JsonTileType.BOUNCE, JsonTileType.CONTAINER, JsonTileType.BREAKABLE), List.of(JsonContainerType.COIN), null));
//        tileDefs.add(new TileDefinition(9, 0, 4, false, List.of(JsonTileType.ENEMY_SPAWN), null, JsonEnemyType.BULLET_BILL));
//        tileDefs.add(new TileDefinition(24, 0, 4, true, List.of(JsonTileType.CONTAINER), List.of(JsonContainerType.COIN, JsonContainerType.ONE_UP, JsonContainerType.POWER_UP, JsonContainerType.STAR), null));

        for (TileDefinition tileDef : tileDefs) {
            List<JsonTileType> types = tileDef.allowedTypes;
            List<JsonContainerType> containerTypes = tileDef.allowedContainerTypes;
            final int paletteCount = tileDef.paletteCount;
            if (containerTypes == null) {
                containerTypes = Collections.singletonList(null); // hack to still loop once
            }
            for (JsonTileType type : types) {
                for (JsonContainerType containerType : containerTypes) {
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
                            case TRANSPORT_ENTRANCE, TRANSPORT_EXIT -> builder.setDirection(JsonDirection.DOWNWARD);
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
}
