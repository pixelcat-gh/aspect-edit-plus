
package aspectedit.frames.action;

import aspectedit.blocks.Blockset;
import aspectedit.frames.AspectEdit;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.AssemblyFileFilter;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.SaveBlocksetAsAssemblyWorker;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author mark
 */
public class SaveBlocksetAsAssemblyAction
        extends AsynchronousAction<SaveBlocksetAsAssemblyWorker> {

    private static final AssemblyFileFilter FILE_FILTER = new AssemblyFileFilter();
    
    private Blockset blockset;

    public SaveBlocksetAsAssemblyAction() {
        this("Export to Assembly", IconManager.getIcon(IconManager.ASM_FILE));
    }

    public SaveBlocksetAsAssemblyAction(String name, Icon icon) {
        super(SaveBlocksetAsAssemblyWorker.class, name, icon);
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
    protected void doAction(SaveBlocksetAsAssemblyWorker worker) {
        if(blockset == null) return;

        if(blockset.size() == 0) {
            showErrorMessage("Nothing to save.");
            return;
        }
        
        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Export Assembly File")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(OpenBlocksetAction.BLOCKSET_DIR)
                .build();

        while(blockset.getName() == null || blockset.getName().isEmpty()) {
            promptForName();
        }

        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if(chooser.getFileFilter() == FILE_FILTER
                    && !file.getAbsolutePath().endsWith(".asm")) {
                file = new File(file.getAbsolutePath() + ".asm");
            }

            worker.setBlockset(blockset);
            worker.setFile(file);

            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(SaveBlocksetAsAssemblyWorker worker) {
        if(worker.isIncomplete()) {
            Throwable t = worker.getException();

            showErrorMessage(String.format(
                    "Could not save file. Error given was:\n%s",
                    t.getMessage()));
        }
    }


    private void promptForName() {
        String value = JOptionPane.showInputDialog(null,
                "Please enter a prefix for the labels",
                AspectEdit.APP_NAME, JOptionPane.QUESTION_MESSAGE);

        blockset.setName(value.trim());
    }

}
