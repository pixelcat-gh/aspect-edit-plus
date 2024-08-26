
package aspectedit.frames.action;

import aspectedit.images.IconManager;
import aspectedit.level.Level;
import aspectedit.workers.SaveLevelWorker;
import java.io.File;

/**
 *
 */
public class SaveLevelAction extends AsynchronousAction<SaveLevelWorker> {

    private SaveLevelAsAction delegate = new SaveLevelAsAction();

    private Level level;

    public SaveLevelAction() {
        super(SaveLevelWorker.class, "Save Level",
                IconManager.getIcon(IconManager.SAVE));
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        final Level oldLevel = this.level;
        final boolean oldEnabled = isEnabled();

        this.level = level;

        if(oldLevel != level) {
            firePropertyChange("level", oldLevel, level);
        }

        if(!oldEnabled && level != null) {
            firePropertyChange("enabled", oldEnabled, isEnabled());
        }
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && level != null;
    }

    @Override
    protected void doAction(SaveLevelWorker worker) {

        String fileName = level.getFileName();

        if(fileName == null || fileName.trim().length() == 0) {
            delegate.doAction(worker);
        } else {

            File file = new File(fileName);

            worker.setLevel(level);
            worker.setFile(file);

            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(SaveLevelWorker worker) {
        if(worker.isIncomplete()) {

            Throwable t = worker.getException();

            showErrorMessage(String.format(
                    "Failed to save file. Error given was:\n%s",
                    t.getMessage()));
        } else {
            
            level.setModified(false);
        }
    }



}
