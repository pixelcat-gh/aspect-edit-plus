
package aspectedit.frames.action;

import aspectedit.blocks.Blockset;
import aspectedit.images.IconManager;
import aspectedit.workers.SaveBlocksetWorker;
import java.io.File;
import javax.swing.Icon;

/**
 *
 * @author mark
 */
public class SaveBlocksetAction extends AsynchronousAction<SaveBlocksetWorker> {

    private SaveBlocksetAsAction delegate;

    private Blockset blockset;

    public SaveBlocksetAction() {
        this("Save Mappings", IconManager.getIcon(IconManager.SAVE));
    }

    public SaveBlocksetAction(String name, Icon icon) {
        super(SaveBlocksetWorker.class, name, icon);

        delegate = new SaveBlocksetAsAction();
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        final boolean oldEnabled = isEnabled();

        this.blockset = blockset;
        delegate.setBlockset(blockset);

        firePropertyChange("enabled", oldEnabled, isEnabled());
    }


    @Override
    public boolean isEnabled() {
        return super.isEnabled() && blockset != null;
    }


    @Override
    protected void doAction(SaveBlocksetWorker worker) {
        if(blockset == null || blockset.size() == 0) {
            showErrorMessage("Nothing to save.");
            return;
        }

        String filename = blockset.getFileName();

        // check to see if the Blockset has a valid file name
        if(filename == null || filename.trim().length() == 0) {
            // blockset does not have a valid filename - delegate to the Save As action
            delegate.doAction(worker);

        } else {

            File file = new File(blockset.getFileName());

            worker.setBlockset(blockset);
            worker.setFile(file);

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
