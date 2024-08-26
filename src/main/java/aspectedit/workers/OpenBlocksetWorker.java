
package aspectedit.workers;

import aspectedit.blocks.Blockset;
import aspectedit.io.BlocksetReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 * @author mark
 */
public class OpenBlocksetWorker extends SwingWorker<Blockset, Void> {

    private File file;
    private boolean incomplete = false;
    private Throwable exception;
    private int offset;

    public OpenBlocksetWorker() {
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
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
        if(file == null) throw new RuntimeException("File not set for OpenBlocksetWorker.");

        Blockset blockset = null;

        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);

            BlocksetReader reader = new BlocksetReader(fin);

            reader.setOffset(offset);

            blockset = reader.read();

            //generate the ASM output file name from the source file
            StringBuffer sb = new StringBuffer();
            String asmFile = file.getAbsolutePath();
            asmFile.substring(0, asmFile.lastIndexOf("."));
            asmFile += ".asm";

            if(offset == 0) {
                blockset.setFileName(file.getAbsolutePath());
                blockset.setAsmFileName(asmFile);
                blockset.setName(file.getName());
            }

            blockset.setOffset(offset);
            //default to an offset of 256 VRAM tiles
            blockset.setTileOffset(256);

            blockset.setModified(false);
            
        } catch (IOException ex) {
            exception = ex;
            incomplete = true;
            
        } finally {
            try { if(fin != null) fin.close(); } catch (IOException ex) {}
        }

        return blockset;
    }

}
