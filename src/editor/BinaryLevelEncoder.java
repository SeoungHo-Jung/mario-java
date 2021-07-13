package editor;

import java.io.BufferedOutputStream;
import java.nio.ByteBuffer;

public class BinaryLevelEncoder implements LevelEncoder {
    @Override
    public byte[] encode(Level level) {

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
         * - Foreground Tile Offset (32 bit)
         * - Foreground Tile Length (32 bit)
         * - Background Tile Offset (32 bit)
         * - Background Tile Length (32 bit)
         * - Foreground Tiles (16 bit array)
         * - Background Tiles (16 bit array)
         */

        final int headerSize = 32;
        final int dataDescriptionSize = 16;
        final int fgTilesSize = 2 * level.getHeight() * level.getWidth();
        final int totalSize = headerSize +  dataDescriptionSize + fgTilesSize;
        final int dataOffset = headerSize;
        final int dataSize = totalSize - headerSize;
        final int fgTilesOffset = headerSize + dataDescriptionSize;

        final byte[] bytes = new byte[totalSize];
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        // write the header
        bb.putInt(0x1234);      // Magic number
        bb.putInt(1);           // File Format Version
        bb.putInt(0);           // Checksum
        bb.putInt(dataOffset);  // Offset of Data portion
        bb.putInt(dataSize);    // Data portion Size
        bb.putInt(0);           // Padding
        bb.putInt(0);
        bb.putInt(0);

        // write the data info
        bb.putInt(fgTilesOffset);   // Offset of Foreground Tiles portion
        bb.putInt(fgTilesSize);     // Size of Foreground Tiles portion
        bb.putInt(0);               // Offset of Background Tiles portion TODO
        bb.putInt(0);               // Size of Background Tiles portion TODO

        // write the tiles
        for (ForegroundTile tile : level.getForegroundLayer()) {
            bb.putShort(tile.getTileIndex());
        }

        // TODO: BACKGROUND TILES

        return bb.array();
    }
}
