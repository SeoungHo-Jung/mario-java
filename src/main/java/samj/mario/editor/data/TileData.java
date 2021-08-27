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
    public static List<TileDefinition> TILE_DEFINITIONS;
    public static List<Tile> TILES;

    static {
        TILE_DEFINITIONS = getTileDefinitions();

        List<Tile> fgTiles = new ArrayList<>();
        fgTiles.add(Tile.EMPTY_TILE);

        for (TileDefinition tileDef : TILE_DEFINITIONS) {
            List<TileType> types = tileDef.allowedTypes;
            final int paletteCount = tileDef.paletteCount;
            for (TileType type : types) {
                List<ContainerType> containerTypes = type == TileType.CONTAINER ? tileDef.allowedContainerTypes : Collections.singletonList(null);
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
                        builder.setSecondaryDisplayTileIcon(getSecondaryDisplayIcon(builder));
                        Tile tile = builder.build();
                        fgTiles.add(tile);
                    }
                }
            }
        }

        TILES = Collections.unmodifiableList(fgTiles);
    }

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

        // Create a prototypical default Tile for each tile definition
        for (TileDefinition tileDef : tileDefs) {
            int x = tileDef.x;
            int y = tileDef.y;
            tileDef.prototype = Tile.builder()
                    .setTileX(x)
                    .setTileY(y)
                    .setPrimaryDisplayTileIcon(new Icon(IconSheet.TILES, x, y))
                    .setType(tileDef.allowedTypes.get(0)) // Use the first allowed type as the default type for this tile
                    .setAllowedTileTypes(tileDef.allowedTypes)
                    .build();
        }

        return tileDefs;
    }

    private static Icon getSecondaryDisplayIcon(Tile.Builder tileBuilder) {
        switch (tileBuilder.getType()) {
            case BREAKABLE -> {
                return new Icon(IconSheet.EDITOR, 2, 0);
            }
            case BOUNCE -> {
                return new Icon(IconSheet.EDITOR, 1, 0);
            }
            case CONTAINER -> {
                switch (tileBuilder.getContainerType()) {
                    case COIN -> {
                        return new Icon(IconSheet.EDITOR, 3, 2);
                    }
                    case POWER_UP -> {
                        return new Icon(IconSheet.EDITOR, 0, 2);
                    }
                    case STAR -> {
                        return new Icon(IconSheet.EDITOR, 1, 2);
                    }
                    case ONE_UP -> {
                        return new Icon(IconSheet.EDITOR, 2, 2);
                    }
                    default -> {
                        return null;
                    }
                }
            }
            case TRANSPORT_ENTRANCE -> {
                // TODO
                return null;
            }
            case TRANSPORT_EXIT -> {
                // TODO
                return null;
            }
            case MARIO_SPAWN -> {
                return new Icon(IconSheet.EDITOR, 0, 3);
            }
            case ENEMY_SPAWN -> {
                switch (tileBuilder.getEnemyType()) {
                    case LITTLE_GOOMBA -> {
                        return new Icon(IconSheet.EDITOR, 1, 1);
                    }
                    case GREEN_KOOPA_TROOPA -> {
                        return new Icon(IconSheet.EDITOR, 0, 1);
                    }
                    case BULLET_BILL -> {
                        return new Icon(IconSheet.EDITOR, 2, 1);
                    }
                    default -> {
                        return null;
                    }
                }
            }
            default -> {
                return null;
            }
        }
    }
}
