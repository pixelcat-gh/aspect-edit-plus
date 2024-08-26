
package aspectedit.io;

import aspectedit.level.Level;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mark
 */
public class LevelWriter extends ResourceWriter<Level> {

    private static final int S2_COMPRESSION_MARKER = 0xFD;
    private static final int SC_COMPRESSION_MARKER = 0xFF;

    private int compressionMarker = S2_COMPRESSION_MARKER;
    private boolean scMode = false;

    public LevelWriter(OutputStream out) {
        super(out);
    }

    public void setS2Compression(boolean value) {
        compressionMarker = value ? S2_COMPRESSION_MARKER : SC_COMPRESSION_MARKER;
        scMode = !value;
    }

    @Override
    public void write(Level level) throws IOException {
        // build a sequential array of level data
        int[] blocks = new int[level.getWidth() * level.getHeight()];

        int width = level.getWidth();

        for(int y=0; y<level.getHeight(); y++) {
            for(int x=0; x<level.getWidth(); x++) {
                blocks[y * width + x] = level.getMappingValue(x, y);
            }
        }

        // write the data
        for(int i=0; i<blocks.length; i++) {

            // check to see if the data can be compressed
            // mapping 0xFD must be encoded as compressed even if there
            // is only a single block since it will be confused with a
            // compression marker byte. This will result in 1 byte being
            // expaned to 3 bytes.
            if((i < blocks.length-2
                    && blocks[i] == blocks[i+1]
                    && blocks[i] == blocks[i+2]) 
                    || blocks[i] == compressionMarker){

                // compress the data
                int value = blocks[i];
                int count = 1;

                // count the number of bytes to compress (note that
                //  0 < repeat count < 256)
                for(; i < blocks.length-1 && blocks[i] == blocks[i+1] && count<255; i++) ++count;

                out.write(compressionMarker);    // write the compression marker

                if(scMode) {
                    out.write(value);
                    out.write(count);
                } else {
                    out.write(count);   // write the count & value
                    out.write(value);
                }

            } else {
                // data can't be compressed.
                out.write(blocks[i]);
            }
        }

        //write the end marker
        byte[] endMarker = new byte[] { (byte)0xFF, (byte)0xFF, 0x0 };
        out.write(endMarker);
    }

}
