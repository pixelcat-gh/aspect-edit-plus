
package aspectedit.io;

import aspectedit.palette.Palette;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class PaletteReader extends ResourceReader<Palette> {

    public PaletteReader(InputStream in) {
        super(in);
    }


    @Override
    public Palette read() throws IOException {
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Palette palette = new Palette();

        for(int i=0; i<16; i++) {
            int data = in.read();

            if(data == -1) {
                throw new IOException("Unexpected end of stream.");
            }

            palette.setColour(i, data);
        }

        palette.setModified(false);
        
        return palette;
    }

}
