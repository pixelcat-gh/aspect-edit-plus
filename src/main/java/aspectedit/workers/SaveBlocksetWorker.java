
package aspectedit.workers;

import aspectedit.blocks.Blockset;
import aspectedit.io.BlocksetWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class SaveBlocksetWorker extends SwingWorker<Blockset, Void> {

    private File file;
    private Blockset blockset;
    private boolean incomplete = false;
    private Throwable exception;

    public SaveBlocksetWorker() {

    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        this.blockset = blockset;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isIncomplete() {
        return incomplete;
    }


    @Override
    protected Blockset doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file specified for SaveBlocksetWorker.");
        if(blockset == null) throw new RuntimeException("No blockset specified for SaveBlocksetWorker.");

        FileOutputStream fout = null;

        try {
            fout = new FileOutputStream(file);

            BlocksetWriter writer = new BlocksetWriter(fout);

            writer.write(blockset);


        } catch (IOException ex) {
            incomplete = true;
            exception = ex;

        } finally {
            try {if(fout != null) fout.close(); } catch (IOException ex) {}
        }

        return blockset;
    }

}
