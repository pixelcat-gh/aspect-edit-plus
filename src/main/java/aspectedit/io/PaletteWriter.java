
package aspectedit.io;

import aspectedit.palette.Palette;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mark
 */
public class PaletteWriter extends ResourceWriter<Palette> {

    public PaletteWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Palette palette) throws IOException {
        for(int i=0; i<palette.size(); i++) {
            out.write(palette.getColour(i));
        }
    }

}
