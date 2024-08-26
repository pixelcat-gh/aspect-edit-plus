
package aspectedit.workers;

import aspectedit.io.GGPaletteReader;
import aspectedit.io.PaletteReader;
import aspectedit.palette.Palette;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 */
public class OpenPaletteWorker extends SwingWorker<Palette, Void> {

    private File file;
    private boolean incomplete = false;
    private Throwable exception;
    private int offset = 0;
    private boolean ggPalette = false;

    public OpenPaletteWorker() {
    }


    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public boolean isGgPalette() {
        return ggPalette;
    }

    public void setGgPalette(boolean ggPalette) {
        this.ggPalette = ggPalette;
    }



    @Override
    protected Palette doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file set for OpenPaletteWorker.");
        
        FileInputStream fin = null;
        Palette p = null;

        try {
            fin = new FileInputStream(file);

            PaletteReader reader = null;
            if(isGgPalette()) {
                reader = new GGPaletteReader(fin);
            } else {
                reader = new PaletteReader(fin);
            }

            reader.setOffset(offset);
            
            p = reader.read();

            if(offset == 0) {
                p.setName(file.getName());
                p.setFileName(file.getAbsolutePath());
            }
            
            p.setOffset(offset);
            p.setModified(false);

        } catch (IOException ex) {
            this.exception = ex;
            incomplete = true;

        } finally {
            try { if(fin != null) fin.close(); } catch (IOException ex) {}
        }

        return p;
    }

}
