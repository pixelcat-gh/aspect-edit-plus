
package aspectedit.io;

import aspectedit.level.Level;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 *
 */
public class UncLevelReader extends ResourceReader<Level> {

    private int width;

    /**
     * Construct a LevelReader for the given InputStream.
     * The level width will be estimated after the data has been
     * decompressed.
     *
     * @param in The InputStream.
     */
    public UncLevelReader(InputStream in) {
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
    public UncLevelReader(InputStream in, int width) {
        super(in);
        this.width = width;
    }


    @Override
    public Level read() throws IOException {
        if (in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Vector<Integer> data = new Vector<Integer>();

        int value;

        while ((value = in.read()) != -1) {
            data.add(value & 0xFF);
        }

        if (data.size() > Level.DATA_ARRAY_LENGTH) {
            data.setSize(Level.DATA_ARRAY_LENGTH);
        }

        // estimate the level width if required
        if (width == -1) width = estimateLevelWidth(data.size());

        try {
            Level level = new Level(width, data.size() / width);
            for (int y = 0; y < data.size() / width; y++) {
                for (int x = 0; x < width; x++) {
                    int pos = y * width + x;
                    level.setMappingValue(x, y, pos < data.size() ? data.get(y * width + x) : 255);
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
}
