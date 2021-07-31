# Mario Java Level Editor

## File Format (JSON)
### Example `Level`
```json
{
  "name": "World 1-1",
  "seconds": 300,
  "backgroundColor": {
    "r": 0,
    "g": 255,
    "b": 255
  },
  "tiles": [
    {
      "type": "SOLID",
      "x": 0,
      "y": 0,
      "isAnimated": false
    },
    {
      "type": "CONTAINER",
      "containerType": "COIN",
      "itemCount": 7,
      "x": 24,
      "y": 0,
      "isAnimated": true
    },
    {
      "type": "BREAKABLE",
      "x": 1,
      "y": 6,
      "isAnimated": false,
    },
    {
      "type": "ENEMY_SPAWN",
      "enemyType": "BULLET_BILL",
      "x": 9,
      "y": 0,
      "isAnimated": false
    },
    {
      "type": "EMPTY",
      "isAnimated": false
    }
  ]
}
```

### Definitions

###### Level (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `name` | String |  _Optional_ | The name of the Level as it appears in-game. |
| `seconds` | Integer | _Optional_ | The time limit for the level allowed for the player in seconds. |
| `backgroundColor` | `Color` | _Required_ | The color of this level's background. |
| `tiles` | Array of Array of `Tile` | _Required_ | A two-dimensional array of Tile objects which represents the level in the form of a tile grid. Each inner array in `tiles` represents a row of tiles from left to right. The outer array's order is top to bottom. |

###### Tile (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `type` | `TileType` | _Required_ | The way this tile should behave. Each tile has a single `type`. |
| `containerType` | `ContainerType` | _Optional_ (unless `type` is `CONTAINER`) | The type of item this container dispenses. |
| `containerCount`| Integer | _Optional_ (unless `type` is `CONTAINER`) | The count of items this container dispenses (mainly used for coins). |
| `direction` | `Direction` | _Optional_ (unless `type` is `TRANSPORT_ENTRANCE` or `TRANSPORT_EXIT`) | The direction which Mario enters/exits this transport. | 
| `enemyType` | `EnemyType` | _Optional_ (unless `type` is `ENEMY_SPAWN`) | The type of this enemy - Eg. goomba, koopa, etc. | 
| `index` | Integer | _Optional_ (unless `type` is `MARIO_SPAWN`, `TRANSPORT_ENTRANCE`, or `TRANSPORT_EXIT`) | Zero indexed int which identifies in an ordered fashion which instance of a particular tile type this is. In the case of `MARIO_SPAWN`, the tile with `index=0` is the place where Mario first spawns. All subsequent `MARIO_SPAWN` tiles indicate checkpoint respawns. In the case of `TRANSPORT_ENTRANCE`/`TRANSPORT_EXIT` each pair of entrance & exit with index `n` are linked. `index` is guaranteed to be unique per tile type, zero-indexed, and consecutive. | 
| `x` | Integer | _Optional_ (only when `type` is `EMPTY`) | The x coordinate of this tile's graphic on the tile sheet. |
| `y` | Integer | _Optional_ (only when `type` is `EMPTY`) | The y coordinate of this tile's graphic on the tile sheet. |
| `isAnimated` | Boolean | _Required_ | Whether this particular tile should be animated. |

###### Color (Object)
| Property | Type | Nullability | Description |
| -------- | ---- | ----------- | ----------- |
| `r` | Integer | _Required_ | The red component of the RGB color - Only values 0-255 are allowed. |
| `g` | Integer | _Required_ | The green component of the RGB color - Only values 0-255 are allowed. |
| `b` | Integer | _Required_ | The blue component of the RGB color - Only values 0-255 are allowed. |

###### TileType (Enum)
| Value | Description |
| ----- | ----------- |
| `BACKGROUND` | A Tile which is visual only and doesn't interact whatsoever with any objects in the game. | 
| `EMPTY` | Same as `BACKGROUND`, but with no image rendered from the tilesheet. Should appear as a solid tile of `Level.backgroundColor` |
| `SOLID` | A fixed block that has collision. | 
| `BREAKABLE` | Same as `SOLID` but breaks when head-bonked by Mario. |
| `BOUNCE` | Same as `SOLID` but bounces when head-bonked by Mario. This can result in killing enemies or collecting coins that are above this tile. |
| `CONTAINER` | Same as `BOUNCE`, but releases items (coins and power-ups) when head-bonked by Mario. Containers can also contain more than one item (of a single type), usually for coins. Once all the items have been collected by the player, | the container will chang| it's appearance and become solid. |
| `COIN` | This is a free-standing coin which can be collected when Mario comes in contact with. |
| `TRANSPORT_ENTRANCE` | Transports Mario to a corresponding `TRANSPORT_EXIT` when Mario collides with it in the specified `Direction`. Usually, this tile type is used in conjunction with the "pipe" tiles. |
| `TRANSPORT_EXIT` | Destination when Mario enters a `TRANSPORT_ENTRANCE`. In this case, the `Direction` indicates the direction in which Mario emerges from the exit. |
| `MARIO_SPAWN` | Indicates the tile on which Mario should spawn at the start of a level. Can be used multiple times within a single level to re-spawn mario after he reaches a checkpoint. |
| `ENEMY_SPAWN` | Location where an enemy should spawn on the level. This can be any type of enemy as indicated by the `enemyType`. |

###### ContainerType (Enum)
| Value | Description |
| ----- | ----------- |
| `COIN` | https://www.mariowiki.com/Coin |
| `POWER_UP` | https://www.mariowiki.com/Super_Mushroom </br> https://www.mariowiki.com/Fire_Flower |
| `STAR` | https://www.mariowiki.com/Super_Star |
| `ONE_UP` | https://www.mariowiki.com/1-Up_Mushroom |

###### EnemyType (Enum)
| Value | Description |
| ----- | ----------- |
| `LITTLE_GOOMBA` | https://www.mariowiki.com/Goomba |
| `GREEN_KOOPA_TROOPA` | https://www.mariowiki.com/Koopa_Troopa |
| `BULLET_BILL` | https://www.mariowiki.com/Bullet_Bill |

###### Direction (Enum)
| Value | Description |
| ----- | ----------- |
| `UPWARD` | Mario enters/emerges from above |
| `RIGHTWARD` | Mario enters/emerges from the right |
| `DOWNWARD` | Mario enters/emerges from below |
| `LEFTWARD` | Mario enters/emerges from the left |