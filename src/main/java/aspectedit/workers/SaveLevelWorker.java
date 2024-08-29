
package aspectedit.workers;

import aspectedit.io.LevelWriter;
import aspectedit.io.TTLevelWriter;
import aspectedit.io.UncLevelWriter;
import aspectedit.level.Level;
import aspectedit.level.LevelFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.SwingWorker;

/**
 *
 */
public class SaveLevelWorker extends SwingWorker<Level, Void> {

    private File file;
    private boolean incomplete = false;
    private Throwable exception;
    private Level level;

    /**
     * Construct.
     */
    public SaveLevelWorker() {
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

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }


    @Override
    protected Level doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("File not set for SaveLevelWorker.");
        if(level == null) throw new RuntimeException("Level not set for SaveLevelWorker.");

        LevelFormat format = level.getLevelFormat();
        try (FileOutputStream fout = new FileOutputStream(file)) {

            switch (format) {
                case S2:
                    new LevelWriter(fout).write(level);
                    break;
                case SC:
                    LevelWriter writer = new LevelWriter(fout);
                    writer.setS2Compression(false);
                    writer.write(level);
                    break;
                case TT:
                    new TTLevelWriter(fout).write(level);
                    break;
                default:
                    new UncLevelWriter(fout).write(level);
            }

        } catch (IOException ex) {
            this.exception = ex;
            incomplete = true;

        }

        return level;
    }

}
