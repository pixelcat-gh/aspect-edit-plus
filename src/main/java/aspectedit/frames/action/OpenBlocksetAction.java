
package aspectedit.frames.action;

import aspectedit.blocks.Blockset;
import aspectedit.frames.AspectEdit;
import aspectedit.frames.action.accessories.OpenBlocksetOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.OpenBlocksetWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author mark
 */
public class OpenBlocksetAction extends AsynchronousAction<OpenBlocksetWorker> {

    /** Property key for the blockset directory. */
    public static final String BLOCKSET_DIR = "BlocksetDir";

    private SaveBlocksetAction saveDelegate;

    public static final String PROP_BLOCKSET = "blockset";
    private static final BinaryDataFilter FILE_FILTER = new BinaryDataFilter();

    private boolean warnIfModified = true;
    private OpenBlocksetOptionsAccessory accessory;
    private Blockset blockset;

    /**
     *
     */
    public OpenBlocksetAction() {
        this("Open Mappings", IconManager.getIcon(IconManager.OPEN));
    }

    public OpenBlocksetAction(String name, Icon icon) {
        super(OpenBlocksetWorker.class, name, icon);

        saveDelegate = new SaveBlocksetAction();
        accessory = new OpenBlocksetOptionsAccessory();
    }

    /**
     * Get the value of blockset
     *
     * @return the value of blockset
     */
    public Blockset getBlockset() {
        return blockset;
    }

    /**
     * Set the value of blockset
     *
     * @param blockset new value of blockset
     */
    public void setBlockset(Blockset blockset) {
        Blockset oldBlockset = this.blockset;
        
        this.blockset = blockset;
        saveDelegate.setBlockset(blockset);

        firePropertyChange(PROP_BLOCKSET, oldBlockset, blockset);
    }

    public boolean isWarnIfModified() {
        return warnIfModified;
    }

    public void setWarnIfModified(boolean warnIfModified) {
        this.warnIfModified = warnIfModified;
    }



    @Override
    protected void doAction(OpenBlocksetWorker worker) {

        // check to see if the blockset has been modified and
        // warn the user if necessary
        if(blockset != null && blockset.isModified() && warnIfModified) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "The current blockset has been modified.\nDo you wish to save the changes?",
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
                .title("Browse for Mappings")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(BLOCKSET_DIR)
                .build();

        accessory.setOffset(0);
        chooser.setAccessory(accessory);
        
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if(chooser.getFileFilter() == FILE_FILTER) {
                file = FILE_FILTER.checkExtension(file);
            }
            
            if(!file.canWrite()) {
                showErrorMessage(String.format("Unable to write to file:\n'%s'",
                        file.getAbsolutePath()));

            } else {

                worker.setOffset(accessory.getOffset());
                worker.setFile(file);

                worker.execute();
            }
        }
    }

    @Override
    protected void doAfterAction(OpenBlocksetWorker worker) {
        if(worker.isIncomplete()) {
            Throwable exception = worker.getException();

            // DEBUG
            exception.printStackTrace();
            // END DEBUG

            showErrorMessage(String.format(
                    "Failed to load file. Error given was:\n%s",
                    exception.getMessage()));

        } else {
            try {
                Blockset blockset = worker.get();
                blockset.setModified(false);

                setBlockset(blockset);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

}
