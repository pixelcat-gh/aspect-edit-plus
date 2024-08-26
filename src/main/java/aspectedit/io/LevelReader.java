
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
     * @param in The InputStream.
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
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Vector<Integer> data = new Vector<Integer>();

        int value = -1;

        while( (value = in.read()) != -1) {
            data.add(value);

            if(data.size() > 3
                    && (value & 0xFF) == 0x00
                    && (data.get(data.size() - 2) & 0xFF) == 0xFF
                    && (data.get(data.size() - 3) & 0xFF) == 0xFF) {
                break;
            }
        }


        int decompressedCount = 0;

        Vector<Integer> uncompressed = new Vector<Integer>();

        for(int i=0; i<data.size(); i++) {

            value = data.get(i) & 0xFF;

            // check for the end of data marker
            if(i < data.size() - 3
                    && value == 0xFF
                    && (data.get(i+1) & 0xFF) == 0xFF
                    && (data.get(i+2) & 0xFF) == 0x00) {
                i = data.size();
                break;
            }


            // check to see if the byte is a compression marker and
            // decompress accordingly
            if(value == compressionMarker) {
                int count = 0;

                // in SC compression the value comes before the count
                if(scMode) {
                    value = data.get(++i) & 0xFF;
                    count = data.get(++i) & 0xFF;
                } else {
                    count = data.get(++i) & 0xFF;
                    value = data.get(++i) & 0xFF;
                }
                

                for(int j=0; j<count; j++) {
                    ++decompressedCount;
                    uncompressed.add(value);
                }

            } else {
                ++decompressedCount;
                uncompressed.add(data.get(i) & 0xFF);
            }
            
        }

        // estimate the level width if required
        if(width == -1) width = estimateLevelWidth(decompressedCount);

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
