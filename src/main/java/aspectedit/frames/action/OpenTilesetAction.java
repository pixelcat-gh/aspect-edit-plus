
package aspectedit.frames.action;

import aspectedit.frames.AspectEdit;
import aspectedit.frames.action.accessories.OpenTilesetOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.CompressedTilesetFileFilter;
import aspectedit.io.filefilters.UncompressedTilesetFileFilter;
import aspectedit.tiles.Tileset;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.OpenTilesetWorker;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * A generic action to open either a compressed or uncompressed tileset. If the
 * current tileset has been modified the user is given the option to save first.
 * 
 * @author Mark Barnett
 */
public class OpenTilesetAction extends AsynchronousAction<OpenTilesetWorker> {

    public static final String TILESET_DIR = "TilesetDir";

    private static final CompressedTilesetFileFilter COMPRESSED_FILTER =
            new CompressedTilesetFileFilter();
    private static final UncompressedTilesetFileFilter UNCOMPRESSED_FILTER =
            new UncompressedTilesetFileFilter();


    private boolean warnIfModified = true;
    private Tileset tileset;
    public static final String PROP_TILESET = "tileset";
    private OpenTilesetOptionsAccessory accessory;

    /**
     * If the current tileset has been modified give the user the option to
     * delegate to this action to save the changes.
     */
    private SaveTilesetAction saveDelegate;
    

    /**
     * Construct
     */
    public OpenTilesetAction() {
        this("Open Tileset", IconManager.getIcon(IconManager.OPEN));
    }

    public OpenTilesetAction(String name, Icon icon) {
        super(OpenTilesetWorker.class, name, icon);

        saveDelegate = new SaveTilesetAction();
        accessory = new OpenTilesetOptionsAccessory();
    }

    /**
     * Get the value of tileset
     *
     * @return the value of tileset
     */
    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Set the value of tileset
     *
     * @param tileset new value of tileset
     */
    public void setTileset(Tileset tileset) {
        Tileset oldTileset = this.tileset;
        
        this.tileset = tileset;
        saveDelegate.setTileset(tileset);
        
        firePropertyChange(PROP_TILESET, oldTileset, tileset);
    }

    public boolean isWarnIfModified() {
        return warnIfModified;
    }

    public void setWarnIfModified(boolean warnIfModified) {
        this.warnIfModified = warnIfModified;
    }


    @Override
    protected void doAction(OpenTilesetWorker worker) {

        //if the tileset has been modified ask the user to save the changes
        if(tileset != null && tileset.isModified() && warnIfModified) {
            int result = JOptionPane.showConfirmDialog(
                    null,
                    "The current tileset has been modified.\nDo you wish to save the changes?",
                    AspectEdit.APP_NAME,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            switch(result) {
                case JOptionPane.CANCEL_OPTION: return;

                case JOptionPane.YES_OPTION: saveDelegate.actionPerformed(
                        new ActionEvent(worker, ActionEvent.ACTION_PERFORMED, "save"));
            }
        }

        //show the open dialog
        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Browse for Tileset")
                //.filters(COMPRESSED_FILTER, UNCOMPRESSED_FILTER)
                //.defaultFilter(COMPRESSED_FILTER)
                //.disableAcceptAllFilter()
                .withCurrentFolderKey(TILESET_DIR)
                .build();

        accessory.setOffset(0);
        accessory.setTileCount(0);
        chooser.setAccessory(accessory);

        // ask the user to choose a file
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            // create the worker to load the tileset
            worker.setFile(file);
            worker.setCompressed(accessory.isCompressed());
            worker.setTileCount(accessory.getTileCount());
            worker.setOffset(accessory.getOffset());
            
            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(OpenTilesetWorker worker) {
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
                Tileset tileset = worker.get();
                tileset.setModified(false);

                setTileset(tileset);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

}
