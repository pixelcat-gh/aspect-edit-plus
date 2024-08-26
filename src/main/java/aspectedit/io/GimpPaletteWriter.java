/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.io;

import aspectedit.palette.Palette;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author mark
 */
public class GimpPaletteWriter extends ResourceWriter<Palette> {

    private static final String HEADER = "GIMP Palette\n";


    public GimpPaletteWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Palette palette) throws IOException {

        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(out));

            bw.write(HEADER);
            bw.write("Name: MS Palette\n#\n");

            for (int i = 0; i < palette.size(); i++) {
                Color c = palette.getJavaColour(i);
                bw.write(String.format(
                        "%d\t%d\t%d\tUntitled\n",
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue()));
            }
        } catch (IOException ex) {
            throw ex;
            
        } finally {
            if(bw != null) { 
                try { bw.close(); } catch (IOException ex) {}
            }
        }
    }

}
