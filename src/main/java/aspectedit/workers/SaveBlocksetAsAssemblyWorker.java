
package aspectedit.workers;

import aspectedit.blocks.Blockset;
import aspectedit.io.BlocksetAsmWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class SaveBlocksetAsAssemblyWorker extends SwingWorker<Blockset, Void> {

    private Blockset blockset;
    private File file;
    private Throwable exception;
    private boolean incomplete = false;

    public SaveBlocksetAsAssemblyWorker() {

    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        this.blockset = blockset;
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
    protected Blockset doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("No file specified for SaveBlocksetAsAssemblyWorker.");
        if(blockset == null) throw new RuntimeException("No blockset specified for SaveBlocksetAsAssemblyWorker.");

        FileOutputStream fout = null;

        try {
            fout = new FileOutputStream(file);

            BlocksetAsmWriter writer = new BlocksetAsmWriter(fout);

            writer.write(blockset);

        } catch (IOException ex) {
            incomplete = true;
            exception = ex;

        } finally {
            try { if(fout != null) fout.close(); } catch (IOException ex) {}
        }

        return blockset;
    }

}
