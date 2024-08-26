
package aspectedit.frames.action;

import aspectedit.blocks.Blockset;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.SaveBlocksetWorker;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;

/**
 *
 * @author mark
 */
public class SaveBlocksetAsAction extends AsynchronousAction<SaveBlocksetWorker> {

    private static final BinaryDataFilter FILE_FILTER = new BinaryDataFilter();

    private Blockset blockset;

    public SaveBlocksetAsAction() {
        this("Save Mappings As", IconManager.getIcon(IconManager.SAVE_AS));
    }

    public SaveBlocksetAsAction(String name, Icon icon) {
        super(SaveBlocksetWorker.class, name, icon);
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        final boolean oldEnabled = isEnabled();

        this.blockset = blockset;

        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && blockset != null;
    }


    @Override
    protected void doAction(SaveBlocksetWorker worker) {
        // don't try to perform the action if the blockset has not been set
        if(blockset == null) return;

        if(blockset.size() == 0) {
            showErrorMessage("Nothing to save.");
            return;
        }

        // create the file chooser
        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Save Mappings as")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(OpenBlocksetAction.BLOCKSET_DIR)
                .build();

        // ask the user to pick a file
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            File file = chooser.getSelectedFile();
            
            // set the file extension if the filter was selected but the 
            // extension was not specified
            if(chooser.getFileFilter() == FILE_FILTER) {
                file = FILE_FILTER.checkExtension(file);
            }

            // set the fileName property on the blockset
            blockset.setFileName(file.getAbsolutePath());

            worker.setFile(file);
            worker.setBlockset(blockset);

            worker.execute();
        }
        
    }

    @Override
    protected void doAfterAction(SaveBlocksetWorker worker) {
        if(worker.isIncomplete()) {
            
            Throwable t = worker.getException();
            
            showErrorMessage(String.format(
                    "Failed to save file. Error given was:\n%s",
                    t.getMessage()));
        } else {
            blockset.setModified(false);
        }
    }

}
