
package aspectedit.frames.action;

import aspectedit.frames.action.accessories.SaveLevelOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.level.Level;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.SaveLevelWorker;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 */
public class SaveLevelAsAction extends AsynchronousAction<SaveLevelWorker> {

    private static final BinaryDataFilter BINARY_FILTER = 
            new BinaryDataFilter();

    private Level level;
    private SaveLevelOptionsAccessory accessory;


    public SaveLevelAsAction() {
        super(SaveLevelWorker.class, "Save Level As",
                IconManager.getIcon(IconManager.SAVE_AS));

        accessory = new SaveLevelOptionsAccessory();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        final Level oldLevel = this.level;
        final boolean oldEnabled = isEnabled();

        this.level = level;

        firePropertyChange("level", oldLevel, level);

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
        if(level == null) return;

        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Save Level as")
                .filters(BINARY_FILTER)
                .defaultFilter(BINARY_FILTER)
                .withCurrentFolderKey(OpenLevelAction.LEVEL_DIR)
                .build();


        accessory.setLevelFormat(level.getLevelFormat());
        chooser.setAccessory(accessory);
        
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            if(chooser.getFileFilter() == BINARY_FILTER) {
                file = BINARY_FILTER.checkExtension(file);
            }

            level.setFileName(file.getAbsolutePath());
            level.setLevelFormat(accessory.getLevelFormat());
            
            worker.setFile(file);
            worker.setLevel(level);
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
