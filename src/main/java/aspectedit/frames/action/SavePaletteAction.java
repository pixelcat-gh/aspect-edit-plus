/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.frames.action;

import aspectedit.images.IconManager;
import aspectedit.palette.Palette;
import aspectedit.workers.SavePaletteWorker;
import java.io.File;
import javax.swing.Icon;

/**
 *
 * @author mark
 */
public class SavePaletteAction extends AsynchronousAction<SavePaletteWorker> {

    private Palette palette;
    private SavePaletteAsAction delegate;


    public SavePaletteAction() {
        this("Save Palette", IconManager.getIcon(IconManager.SAVE));
    }


    public SavePaletteAction(String name, Icon icon) {
        super(SavePaletteWorker.class, name, icon);

        delegate = new SavePaletteAsAction();
    }


    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
        delegate.setPalette(palette);
    }


    @Override
    protected void doAction(SavePaletteWorker worker) {
        if(palette.getFileName() == null || palette.getFileName().trim().length() == 0) {
            delegate.doAction(worker);

        } else {

            File file = new File(palette.getFileName());

            worker.setFile(file);
            worker.setPalette(palette);

            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(SavePaletteWorker worker) {
        if(worker.isIncomplete()) {

            Throwable t = worker.getException();

            showErrorMessage(String.format(
                    "Failed to save file. Error given was:\n%s",
                    t.getMessage()));

        } else {
            palette.setModified(false);
        }
    }

}
