package aspectedit.io;

import aspectedit.level.Level;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 *
 */
public class LevelReader extends ResourceReader<Level> {

    private static final int S2_COMPRESSION_MARKER = 0xFD;
    private static final int SC_COMPRESSION_MARKER = 0xFF;

    private int width;
    private int compressionMarker = S2_COMPRESSION_MARKER;
    private boolean scMode = false;

    /**
     * Construct a LevelReader for the given InputStream.
     * The level width will be estimated after the data has been
     * decompressed.
     *
     * @param in The InputStream.
     */
    public LevelReader(InputStream in) {
        super(in);
        this.width = -1;
    }

    /**
     * Construct a LevelReader for the given InputStream with
     * a manually specified level width.
     *
     * @param in    The InputStream.
     * @param width The level width.
     */
    public LevelReader(InputStream in, int width) {
        super(in);
        this.width = width;
    }

    public void useScCompression(boolean value) {
        compressionMarker = value ? SC_COMPRESSION_MARKER : S2_COMPRESSION_MARKER;
        scMode = value;
    }

    @Override
    public Level read() throws IOException {
        if (in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Vector<Integer> data = new Vector<Integer>();

        int value;

        while ((value = in.read()) != -1) {
            data.add(value);
            if (data.size() > 3
                    && (value & 0xFF) == 0x00
                    && (data.get(data.size() - 2) & 0xFF) == 0xFF
                    && (data.get(data.size() - 3) & 0xFF) == 0xFF
            ) {
                data.setSize(data.size() - 3);
                break;
            }
        }

        Vector<Integer> uncompressed = new Vector<Integer>();

        for (int i = 0; i < data.size(); i++) {

            value = data.get(i) & 0xFF;

            // check to see if the byte is a compression marker and
            // decompress accordingly
            if (value == compressionMarker) {
                int count;

                // in SC compression the value comes before the count
                if (scMode) {
                    value = data.get(++i) & 0xFF;
                    count = data.get(++i) & 0xFF;
                } else {
                    count = data.get(++i) & 0xFF;
                    value = data.get(++i) & 0xFF;
                }

                for (int j = 0; j < count; j++) {
                    uncompressed.add(value);
                }
            } else {
                uncompressed.add(data.get(i) & 0xFF);
            }

        }

        if (uncompressed.size() > Level.DATA_ARRAY_LENGTH) {
            uncompressed.setSize(Level.DATA_ARRAY_LENGTH);
        }

        // estimate the level width if required
        if (width == -1) width = estimateLevelWidth(uncompressed.size());
        int height = getLevelHeight(uncompressed.size(), width);

        try {
            Level level = new Level(width, height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pos = y * width + x;
                    if (pos < uncompressed.size()) {
                        level.setMappingValue(x, y, uncompressed.get(y * width + x));
                    }
                }
            }

            level.setModified(false);

            return level;

        } catch (ArrayIndexOutOfBoundsException ex) {
            // catch this and rethrow as a checked exception
            throw new IOException("Too much data for a level layout.", ex);
        } catch (IllegalArgumentException ex) {
            // this is thrown by the Level constructor if the width or height
            // is < 1
            throw new IOException(ex.getMessage(), ex);
        }
    }

    protected int getLevelHeight(int decompressedSize, int width) {
        int height = decompressedSize / width;
        if (decompressedSize % width != 0) {
            height += 1;
        }
        return height;
    }
}
