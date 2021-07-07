package editor;

import java.nio.charset.StandardCharsets;

public class LevelEncoderV1 implements LevelEncoder {
    @Override
    public byte[] encode(Level level) {
        // Write the foreground layer as text
        ForegroundLayer fgLayer = level.getForegroundLayer();
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < level.getHeight(); y++) {
            for (int x = 0; x < level.getWidth(); x++) {
                ForegroundTile tile = fgLayer.getTile(x, y);
                sb.append(tile.getTileIndex());
            }
            sb.append('\n');
        }
        String output = sb.toString();
        return output.getBytes(StandardCharsets.US_ASCII);
    }
}
