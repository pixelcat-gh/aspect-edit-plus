
package aspectedit.workers;

import aspectedit.io.CompressedTilesetWriter;
import aspectedit.io.TilesetWriter;
import aspectedit.tiles.Tileset;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class SaveTilesetWorker extends SwingWorker<Tileset, Void> {

    private File file;
    private Tileset tileset;
    private boolean incomplete = false;
    private Throwable exception;


    public SaveTilesetWorker() {
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isIncomplete() {
        return incomplete;
    }


    @Override
    protected Tileset doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file set for SaveTilesetWorker.");
        if(tileset == null) throw new RuntimeException("No tileset set for SaveTilesetWorker.");

        FileOutputStream fout = null;
        try {

            fout = new FileOutputStream(file);

            TilesetWriter writer = null;

            if(tileset.isCompressed()) {
                writer = new CompressedTilesetWriter(fout);
            } else {
                writer = new TilesetWriter(fout);
            }

            writer.write(tileset);

        } catch (IOException ex) {
            exception = ex;
            incomplete = true;
            
        } finally {
            try { if(fout != null) fout.close(); } catch (IOException ex) {}
        }

        return tileset;
    }

}
