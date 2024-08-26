/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import aspectedit.palette.Palette;
import java.awt.image.BufferedImage;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class PaletteFromImageWorker extends SwingWorker<Palette, String> {

    private BufferedImage image;

    public PaletteFromImageWorker(BufferedImage image) {
        if(image == null) {
            throw new IllegalArgumentException("Image cannot be null.");
        }
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    @Override
    protected Palette doInBackground() throws Exception {
        Quantiser quantiser = new Quantiser();

        image = quantiser.quantise(image);

        return quantiser.getResultingPalette();
    }

}
