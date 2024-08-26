
package aspectedit.frames.action;

import aspectedit.components.tileview.TileView;
import aspectedit.images.IconManager;
import aspectedit.io.filefilters.BasicFileFilter;
import aspectedit.io.filefilters.BmpFileFilter;
import aspectedit.io.filefilters.GifFileFilter;
import aspectedit.io.filefilters.JpgFileFilter;
import aspectedit.io.filefilters.PngFileFilter;
import aspectedit.util.FileChooserBuilder;
import aspectedit.workers.TileViewToImageWorker;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 */
public class ExportToImageAction extends AsynchronousAction<TileViewToImageWorker> {

    public static final String IMAGE_EXPORT_DIR = "ImageExportDir";

    private static BasicFileFilter[] fileFilters;

    static {
        fileFilters = new BasicFileFilter[4];
        fileFilters[0] = new PngFileFilter();
        fileFilters[1] = new JpgFileFilter();
        fileFilters[2] = new GifFileFilter();
        fileFilters[3] = new BmpFileFilter();
    }

    private TileView tileView;

    public ExportToImageAction() {
        this("Export to Image",
                IconManager.getIcon(IconManager.EXPORT_IMAGE));
    }

    public ExportToImageAction(String name, Icon icon) {
        super(TileViewToImageWorker.class, name, icon);
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        final boolean oldEnabled = isEnabled();

        this.tileView = tileView;

        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && tileView != null;
    }


    @Override
    protected void doAction(TileViewToImageWorker worker) {
        if(tileView == null) return;

        JFileChooser chooser = FileChooserBuilder.buildDialog()
                .title("Export to Image")
                .filters(fileFilters)
                .defaultFilter(fileFilters[0])
                .withCurrentFolderKey(IMAGE_EXPORT_DIR)
                .build();

        if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            FileFilter filter = chooser.getFileFilter();

            String format = null;

            for(BasicFileFilter f : fileFilters) {
                if(f == filter) {
                    file = f.checkExtension(file);
                    format = f.getExtension();
                }
            }

            if(format == null) {
                String path = file.getAbsolutePath();
                format = path.substring(path.length() - 3);
            }

            System.out.println(format);

            worker.setFile(file);
            worker.setFormat(format);
            worker.setTileView(tileView);

            worker.execute();
        }
    }

    @Override
    protected void doAfterAction(TileViewToImageWorker worker) {
        if(worker.isIncomplete()) {

            Throwable t = worker.getException();

            showErrorMessage(String.format(
                    "Failed to save file. Error given was:\n%s",
                    t.getMessage()));
        }
    }

}
