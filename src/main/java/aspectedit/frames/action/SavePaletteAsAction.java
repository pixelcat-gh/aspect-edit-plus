/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.frames.action;

import aspectedit.frames.action.accessories.SavePaletteOptionsAccessory;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BasicFileFilter;
import aspectedit.io.filefilters.BinaryDataFilter;
import aspectedit.palette.GGPalette;
import aspectedit.palette.Palette;
import aspectedit.palette.PaletteFormat;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.SavePaletteWorker;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;

/**
 *
 * @author mark
 */
public class SavePaletteAsAction extends AsynchronousAction<SavePaletteWorker> {

    private static final BasicFileFilter PALETTE_FILTER = new BinaryDataFilter();
    private static final BasicFileFilter GPL_FILTER = new BasicFileFilter("gpl", "GIMP Palette Files (*.gpl)");


    private Palette palette;
    private SavePaletteOptionsAccessory accessory;

    public SavePaletteAsAction() {
        this("Save Palette As", IconManager.getIcon(IconManager.SAVE_AS));
    }

    public SavePaletteAsAction(String name, Icon icon) {
        super(SavePaletteWorker.class, name, icon);
        accessory = new SavePaletteOptionsAccessory();
    }


    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }


    @Override
    protected void doAction(SavePaletteWorker worker) {
        if(palette == null) return;

        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Save Palette As")
                .filters(PALETTE_FILTER)
                .defaultFilter(PALETTE_FILTER)
                .withCurrentFolderKey(OpenPaletteAction.PALETTE_DIR)
                .build();


        if(palette instanceof GGPalette) {
            accessory.setPaletteFormat(PaletteFormat.GG);
        } else {
            accessory.setPaletteFormat(PaletteFormat.SMS);
        }

        chooser.setAccessory(accessory);

        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = chooser.getSelectedFile();

            if(accessory.getPaletteFormat() == PaletteFormat.GIMP) {
                file = GPL_FILTER.checkExtension(file);

            } else if(chooser.getFileFilter() instanceof BasicFileFilter) {
                file = ((BasicFileFilter) chooser.getFileFilter()).checkExtension(file);
            }

            if(accessory.getPaletteFormat() != PaletteFormat.GIMP) {
                palette.setName(file.getName());
                palette.setFileName(file.getAbsolutePath());
            }

            worker.setFormat(accessory.getPaletteFormat());
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
