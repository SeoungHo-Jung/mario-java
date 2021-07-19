package samj.mario.editor.io;

import samj.mario.editor.data.TileMatrix;
import samj.mario.editor.data.Tile;
import samj.mario.editor.data.Level;
import samj.mario.editor.data.TileData;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BinaryLevelFormat implements LevelFormat {

    /*
     * File Header
     * - Magic number (32 bit)
     * - Version number (32 bit)
     * - CNC? (32 bit)
     * - Data Offset (32 bit)
     * - Data Length (32 bit)
     * - Padding (3 X 32 bit)
     *
     * Data
     * - Level Width (32 bit)
     * - Level Height (32 bit)
     * - Reserved (32 bit)
     * - Reserved (32 bit)
     * - Foreground Tile Offset (32 bit)
     * - Foreground Tile Length (32 bit)
     * - Background Tile Offset (32 bit)
     * - Background Tile Length (32 bit)
     * - Foreground Tiles (16 bit array)
     * - Background Tiles (16 bit array)
     */

    private static final int HEADER_SIZE = 32;
    private static final int DATA_DESCRIPTION_SIZE = 32;
    public static final int MAGIC_NUMBER = 0x1234;
    public static final int VERSION = 1;

    @Override
    public byte[] encode(Level level) {
        final int fgTilesSize = 2 * level.getHeight() * level.getWidth();
        final int totalSize = HEADER_SIZE + DATA_DESCRIPTION_SIZE + fgTilesSize;
        final int dataOffset = HEADER_SIZE;
        final int dataSize = totalSize - HEADER_SIZE;
        final int fgTilesOffset = HEADER_SIZE + DATA_DESCRIPTION_SIZE;

        final byte[] bytes = new byte[totalSize];
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        // write the header
        bb.putInt(MAGIC_NUMBER);// Magic number
        bb.putInt(VERSION);     // File Format Version
        bb.putInt(0);           // Checksum TODO
        bb.putInt(dataOffset);  // Offset of Data portion
        bb.putInt(dataSize);    // Data portion Size
        bb.putInt(0);           // Padding
        bb.putInt(0);           // Padding
        bb.putInt(0);           // Padding

        // write the data info
        bb.putInt(level.getWidth());    // Level width dimension
        bb.putInt(level.getHeight());   // Level height dimension
        bb.putInt(0);                   // Padding
        bb.putInt(0);                   // Padding
        bb.putInt(fgTilesOffset);       // Offset of Foreground Tiles portion
        bb.putInt(fgTilesSize);         // Size of Foreground Tiles portion
        bb.putInt(0);                   // Offset of Background Tiles portion TODO
        bb.putInt(0);                   // Size of Background Tiles portion TODO

        // write the tiles
        for (Tile tile : level.getTileMatrix()) {
            bb.putShort(tile.getTileIndex());
        }

        // TODO: BACKGROUND TILES

        return bb.array();
    }

    @Override
    public Level decode(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        int magic = bb.getInt();
        assert magic == MAGIC_NUMBER;

        int version = bb.getInt();
        assert version == VERSION;

        int checksum = bb.getInt(); // TODO: verify checksum

        int dataOffset = bb.getInt();
        int dataLength = bb.getInt();
        assert dataLength + HEADER_SIZE <= bytes.length;
        assert dataLength + HEADER_SIZE <= bb.capacity();

        // Now we can start reading in the data
        bb.position(dataOffset);

        int levelWidth = bb.getInt();
        int levelHeight = bb.getInt();
        bb.getInt();    // padding
        bb.getInt();    // padding
        int fgTileOffset = bb.getInt();
        int fgTileLength = bb.getInt();
        int bgTileOffset = bb.getInt();
        int bgTileLength = bb.getInt();

        List<Tile> fgTiles = new ArrayList<>();
        for (int i = 0; i < fgTileLength; i += 2) {
            short tileIndex = bb.getShort();
            fgTiles.add(TileData.TILES_BY_INDEX.get(tileIndex));
        }

        TileMatrix fgLayer = new TileMatrix(levelWidth, levelHeight, fgTiles);

        Level level = new Level();
        level.setDimensions(levelWidth, levelHeight);
        level.setTileMatrix(fgLayer);

        return level;
    }
}
