
package aspectedit.frames.action;

import aspectedit.frames.action.accessories.OpenPaletteOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.palette.Palette;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.OpenPaletteWorker;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 */
public class OpenPaletteAction extends AsynchronousAction<OpenPaletteWorker> {

    public static final String PALETTE_DIR = "PaletteDir";
    private static final FileFilter FILE_FILTER = new BinaryDataFilter();

    private Palette palette;
    private OpenPaletteOptionsAccessory accessory;

    public static final String PROP_PALETTE = "palette";


    /**
     * Construct
     */
    public OpenPaletteAction() {
        this("Open Palette", IconManager.getIcon(IconManager.OPEN));
    }

    
    public OpenPaletteAction(String name, Icon icon) {
        super(OpenPaletteWorker.class, name, icon);
        accessory = new OpenPaletteOptionsAccessory();
    }

    /**
     * Get the value of palette
     *
     * @return the value of palette
     */
    public Palette getPalette() {
        return palette;
    }

    /**
     * Set the value of palette
     *
     * @param palette new value of palette
     */
    public void setPalette(Palette palette) {
        Palette oldPalette = this.palette;
        this.palette = palette;
        firePropertyChange(PROP_PALETTE, oldPalette, palette);
    }


    @Override
    protected void doAction(OpenPaletteWorker worker) {
        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Browse for Palette")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(PALETTE_DIR)
                .build();

        accessory.setOffset(0);
        chooser.setAccessory(accessory);

        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            worker.setGgPalette(accessory.isGGPalette());
            worker.setOffset(accessory.getOffset());
            worker.setFile(file);
            worker.execute();

        }
    }

    @Override
    protected void doAfterAction(OpenPaletteWorker worker) {
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
                setPalette(worker.get());

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

}
