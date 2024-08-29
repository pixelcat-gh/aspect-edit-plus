
package aspectedit.workers;

import aspectedit.io.LevelReader;
import aspectedit.io.ResourceReader;
import aspectedit.io.TTLevelReader;
import aspectedit.io.UncLevelReader;
import aspectedit.level.Level;
import aspectedit.level.LevelFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 */
public class OpenLevelWorker extends SwingWorker<Level, Void> {

    private File file;
    private boolean incomplete = false;
    private Throwable exception;
    private LevelFormat format;
    private int offset;

    public OpenLevelWorker() {
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isIncomplete() {
        return incomplete;
    }

    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public LevelFormat getFormat() {
        return format;
    }

    public void setFormat(LevelFormat format) {
        this.format = format;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }


    @Override
    protected Level doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("File not set for OpenLevelWorker.");

        Level level = null;
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(file);

            ResourceReader<Level> reader = null;

            switch(format) {
                case S2:
                    reader = new LevelReader(fin);
                    break;
                case SC:
                    reader = new LevelReader(fin);
                    ((LevelReader) reader).useScCompression(true);
                    break;
                case TT:
                    reader = new TTLevelReader(fin);
                    break;
                default:
                    reader = new UncLevelReader(fin);
            }

            reader.setOffset(offset);
            level = reader.read();

            if(offset == 0) {
                level.setFileName(file.getAbsolutePath());
                level.setName(file.getName());
            }

            level.setLevelFormat(format);
            level.setOffset(offset);
            level.setModified(false);
            
        } catch (IOException ex) {
            this.exception = ex;
            incomplete = true;

        } finally {
            try { if(fin != null) fin.close(); } catch (IOException ex) {}
        }

        return level;
    }

}
