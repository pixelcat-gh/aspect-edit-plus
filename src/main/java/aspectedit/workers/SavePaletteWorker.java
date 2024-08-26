/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.workers;

import aspectedit.io.GGPaletteWriter;
import aspectedit.io.GimpPaletteWriter;
import aspectedit.io.PaletteWriter;
import aspectedit.io.ResourceWriter;
import aspectedit.palette.GGPalette;
import aspectedit.palette.Palette;
import aspectedit.palette.PaletteFormat;
import aspectedit.util.ColourUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class SavePaletteWorker extends SwingWorker<Palette, Void> {

    private File file;
    private Palette palette;
    private boolean incomplete = false;
    private Throwable exception;
    private PaletteFormat format = PaletteFormat.SMS;


    /**
     * Construct.
     */
    public SavePaletteWorker() {

    }

    public PaletteFormat getFormat() {
        return format;
    }

    public void setFormat(PaletteFormat format) {
        this.format = format;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isIncomplete() {
        return incomplete;
    }


    /**
     * Convert between SMS and GG palette types.
     */
    private void checkConvert() {
        Class<? extends Palette> clazz = palette.getClass();

        if(format == PaletteFormat.GG && Palette.class.equals(clazz)) {
            // convert from SMS palette to GG palette
            palette = ColourUtils.convertSMSPaletteToGG(palette);

        } else if(format == PaletteFormat.SMS && GGPalette.class.equals(clazz)) {
            // convert from GG palette to SMS palette
            palette = ColourUtils.convertGGPaletteToSMS((GGPalette) palette);
        }
    }

    @Override
    protected Palette doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file set for SavePaletteWorker.");
        if(palette == null) throw new RuntimeException("No palette set for SavePaletteWorker.");

        // check to see if we need to convert SMS<>GG palette
        checkConvert();

        FileOutputStream fout = null;
        try {

            fout = new FileOutputStream(file);

            ResourceWriter<Palette> writer = null;

            // instantiate the correct writer for the palette type
            switch(format) {
                case SMS:
                    writer = new PaletteWriter(fout);
                    break;

                case GG:
                    writer = new GGPaletteWriter(fout);
                    break;

                case GIMP:
                    writer = new GimpPaletteWriter(fout);
                    break;
            }

            // write the palette
            writer.write(palette);

        } catch (IOException ex) {
            exception = ex;
            incomplete = true;

        } finally {
            try { if(fout != null) fout.close(); } catch (IOException ex) {}
        }

        return palette;
    }

}
