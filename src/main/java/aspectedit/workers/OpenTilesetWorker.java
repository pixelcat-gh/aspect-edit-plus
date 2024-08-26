
package aspectedit.workers;

import aspectedit.io.CompressedTilesetReader;
import aspectedit.io.TilesetReader;
import aspectedit.tiles.Tileset;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 */
public class OpenTilesetWorker extends SwingWorker<Tileset, Void> {

    private File file;
    private boolean compressed;
    private Throwable exception;
    private boolean incomplete = false;
    private int offset;
    private int tileCount;

    /**
     * Default Constructor
     */
    public OpenTilesetWorker() {
    }

    /**
     * Construct and initialise fields.
     * @param file The tileset data file.
     * @param compressed
     */
    public OpenTilesetWorker(File file, boolean compressed) {
        this.file = file;
        this.compressed = compressed;
    }


    public int getTileCount() {
        return tileCount;
    }

    public void setTileCount(int tileCount) {
        this.tileCount = tileCount;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isIncomplete() {
        return incomplete;
    }


    @Override
    protected Tileset doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file set for OpenTilesetWorker.");

        Tileset tileset = null;
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);

            TilesetReader reader = null;

            if(compressed) {
                reader = new CompressedTilesetReader(fin);
            } else {
                reader = new TilesetReader(fin);
                reader.setTileCount(tileCount);
            }

            reader.setOffset(offset);
            tileset = reader.read();

            if(offset == 0) {
                tileset.setFileName(file.getAbsolutePath());
                tileset.setName(file.getName());
            }

            tileset.setOffset(offset);
            tileset.setModified(false);

        } catch (IOException ex) {
            this.exception = ex;
            incomplete = true;

        } finally {
            try { if(fin != null) fin.close(); } catch (IOException ex) {}
        }

        return tileset;
    }

}
