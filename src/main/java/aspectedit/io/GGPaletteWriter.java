/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.io;

import aspectedit.palette.GGPalette;
import aspectedit.palette.Palette;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 */
public class GGPaletteWriter extends PaletteWriter {

    public GGPaletteWriter(OutputStream out) {
        super(out);
    }

    /**
     * This method only exists to implement a double-dispatch
     * to the "write(GGPalette)" method.
     * @param palette The Palette. Must be an instance of GGPalette.
     * @throws java.io.IOException
     */
    @Override
    public final void write(Palette palette) throws IOException {
        // we're only interested in GGPalette types here
        if(! (palette instanceof GGPalette)) {
            throw new IllegalArgumentException();
        } else {
            write((GGPalette) palette);
        }
    }

    /**
     * Write a GGPalette to an output stream.
     * @param palette The Palette.
     * @throws java.io.IOException
     */
    public void write(GGPalette palette) throws IOException {
        for(int i=0; i<palette.size(); i++) {
            int colour = palette.getColour(i);

            // write LSB
            out.write(colour & 0xFF);
            // write MSB
            out.write((colour >> 8) & 0xF);
        }
    }
}
