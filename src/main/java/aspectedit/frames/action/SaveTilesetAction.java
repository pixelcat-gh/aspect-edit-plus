
package aspectedit.frames.action;

import aspectedit.frames.AspectEdit;
import aspectedit.images.IconManager;
import aspectedit.tiles.Tileset;

import aspectedit.workers.SaveTilesetWorker;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 *
 * @author mark
 */
public class SaveTilesetAction extends AbstractAction {

    /** If the tileset does not have a file the action will be delegated to this. */
    private SaveTilesetAsAction saveAsDelegate;
    
    private Tileset tileset;
    private SaveTilesetWorker worker;
    private PropertyChangeListener workerListener;

    /**
     * Construct
     */
    public SaveTilesetAction() {
        super("Save Tileset", IconManager.getIcon(IconManager.SAVE));

        saveAsDelegate = new SaveTilesetAsAction();

        workerListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                workerPropertyChanged(evt);
            }
        };
    }


    private void workerPropertyChanged(PropertyChangeEvent evt) {
        if("state".equals(evt.getPropertyName()) && worker.isIncomplete()) {

            Throwable t = worker.getException();

            showMessage(String.format(
                    "Could not save file. Error given was:\n%s",
                    t.getMessage()));
        }
    }


    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        boolean oldEnabled = isEnabled();

        this.tileset = tileset;
        saveAsDelegate.setTileset(tileset);
        
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
        
        //check the filename and delegate to Save As action if necessary
        if(tileset.getFileName() == null || tileset.getFileName().trim().length() == 0) {
            saveAsDelegate.actionPerformed(e);

        } else {

            File file = new File(tileset.getFileName());

            //make sure that the file is writable
            if(!file.canWrite()) {
                showMessage(String.format("Unable to write to file:\n'%s'", tileset.getFileName()));

            } else {
                worker = new SaveTilesetWorker();
                
                //write the file on a background thread
                worker.setFile(file);
                worker.setTileset(tileset);

                worker.execute();
            }
        }
    }


    private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, AspectEdit.APP_NAME, JOptionPane.ERROR_MESSAGE);
    }
}
