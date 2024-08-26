
package aspectedit.io;

import aspectedit.palette.GGPalette;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class GGPaletteReader extends PaletteReader {

    public GGPaletteReader(InputStream in) {
        super(in);
    }

    @Override
    public GGPalette read() throws IOException {
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        GGPalette palette = new GGPalette();

        // since the FileInputStream.skip() method doesnt stop at EOF
        // we need to check for the -1 byte at every iteration of the loop
        for(int i=0; i<16; i++) {
            int b = in.read();
            if(b == -1) {
                throw new IOException("Unexpected end of stream.");
            }

            int data = b | (in.read() << 8);
            palette.setColour(i, data);
        }

        return palette;
    }

}
