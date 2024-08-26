
package aspectedit.frames.action;

import aspectedit.frames.AspectEdit;
import aspectedit.frames.action.accessories.SaveTilesetOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.*;
import aspectedit.tiles.Tileset;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.SaveTilesetWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author mark
 */
public class SaveTilesetAsAction extends AbstractAction {

    private static final BinaryDataFilter FILE_FILTER =
            new BinaryDataFilter();

    private Tileset tileset;
    private SaveTilesetWorker worker;
    private SaveTilesetOptionsAccessory accessory;

    /**
     * Construct
     */
    public SaveTilesetAsAction() {
        super("Save Tileset As", IconManager.getIcon(IconManager.SAVE_AS));
        accessory = new SaveTilesetOptionsAccessory();
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        boolean oldEnabled = isEnabled();

        this.tileset = tileset;

        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && tileset != null;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileset == null) return;

        if(tileset.size() == 0) {
            showMessage("Nothing to save.");
            return;
        }
        
        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Save Tileset As")
                .filters(FILE_FILTER)
                .defaultFilter(FILE_FILTER)
                .withCurrentFolderKey(OpenTilesetAction.TILESET_DIR)
                .build();

        accessory.setCompressed(tileset.isCompressed());
        chooser.setAccessory(accessory);

        //show the dialog
        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            if(chooser.getFileFilter() instanceof BasicFileFilter) {
                file = ((BasicFileFilter) chooser.getFileFilter()).checkExtension(file);
            }

            //set the tileset properties based on the new file
            tileset.setCompressed(accessory.isCompressed());
            tileset.setFileName(file.getAbsolutePath());
            tileset.setName(file.getName());

            worker = new SaveTilesetWorker();
            //write the file on a background thread
            worker.setFile(file);
            worker.setTileset(tileset);
            worker.execute();

        }
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, AspectEdit.APP_NAME, JOptionPane.ERROR_MESSAGE);
    }
}
