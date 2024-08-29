
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
     * @param in The InputStream.
     * @param width The level width.
     */
    public UncLevelReader(InputStream in, int width) {
        super(in);
        this.width = width;
    }


    @Override
    public Level read() throws IOException {
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Vector<Integer> data = new Vector<Integer>();

        int value = -1;

        while( (value = in.read()) != -1) {
            data.add(value);
        }

        Vector<Integer> uncompressed = new Vector<Integer>();

        for (Integer datum : data) {
            uncompressed.add(datum & 0xFF);
        }

        // estimate the level width if required
        if(width == -1) width = estimateLevelWidth(uncompressed.size());

        try {
            Level level = new Level(width, uncompressed.size() / width);
            for(int y=0; y<uncompressed.size() / width; y++) {
                for(int x=0; x<width; x++) {
                    level.setMappingValue(x, y, uncompressed.get(y * width + x));
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


    /**
     * Estimates the width of the level based on the pre-configured
     * widths specified in the base Sonic 2 engine and the size of
     * the decompressed level data.
     *
     * @param decompressedSize Decompressed data length.
     * @return An estimated level width. Defaults to 168 if no match was found.
     */
    protected int estimateLevelWidth(int decompressedSize) {
        // loop thru each of the preconfigured level widths and try
        // to find one that could be valid. Start with the largest value
        // and work backwards to the smallest.
        for(int i = Level.VALID_WIDTHS.length-1; i >= 0; i--) {
            if(decompressedSize % Level.VALID_WIDTHS[i] == 0) {
                return Level.VALID_WIDTHS[i];
            }
        }

        // return a default size
        return 168;
    }

}
