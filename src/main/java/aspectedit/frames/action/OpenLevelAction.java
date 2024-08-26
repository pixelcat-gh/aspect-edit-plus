
package aspectedit.frames.action;

import aspectedit.frames.AspectEdit;
import aspectedit.frames.action.accessories.OpenLevelOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.level.Level;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.OpenLevelWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 */
public class OpenLevelAction extends AsynchronousAction<OpenLevelWorker> {

    public static final String LEVEL_DIR = "LevelDir";
    private static final FileFilter FILE_FILTER = new BinaryDataFilter();

    private Level level;
    private SaveLevelAction saveDelegate;
    private boolean warnIfModified = true;
    private OpenLevelOptionsAccessory accessory;

    public OpenLevelAction() {
        this("Open Level", IconManager.getIcon(IconManager.OPEN));
    }

    public OpenLevelAction(String name, Icon icon) {
        super(OpenLevelWorker.class, name, icon);

        saveDelegate = new SaveLevelAction();
        accessory = new OpenLevelOptionsAccessory();
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if(this.level == level) return;
        
        final Level oldLevel = this.level;

        this.level = level;
        saveDelegate.setLevel(level);
        
        firePropertyChange("level", oldLevel, level);
    }

    public boolean isWarnIfModified() {
        return warnIfModified;
    }

    public void setWarnIfModified(boolean warnIfModified) {
        this.warnIfModified = warnIfModified;
    }


    @Override
    protected void doAction(OpenLevelWorker worker) {
        // check to see if the blockset has been modified and
        // warn the user if necessary
        if(level != null && level.isModified() && warnIfModified) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "The current level has been modified.\nDo you wish to save the changes?",
                    AspectEdit.APP_NAME,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            switch(result) {
                case JOptionPane.CANCEL_OPTION: return;

                case JOptionPane.OK_OPTION:
                    saveDelegate.actionPerformed(
                            new ActionEvent(this,
                            ActionEvent.ACTION_PERFORMED,
                            "save"));
            }

        }

        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Open Level")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(LEVEL_DIR)
                .build();

        accessory.setOffset(0);
        chooser.setAccessory(accessory);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            worker.setFile(file);
            worker.setFormat( accessory.getLevelFormat() );
            worker.setOffset(accessory.getOffset());
            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(OpenLevelWorker worker) {
        if(worker.isIncomplete()) {

            Throwable t = worker.getException();

            showErrorMessage(String.format(
                    "Failed to read file. Error given was:\n%s",
                    t.getMessage()));
        } else {
            try {
                Level level = worker.get();
                level.setModified(false);

                setLevel(level);
                
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

}
