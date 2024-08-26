
package aspectedit.workers;

import aspectedit.components.tileview.DefaultTileViewCellRenderer;
import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewCellRenderer;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;

/**
 *
 */
public class TileViewToImageWorker extends SwingWorker<TileView, Void> {

    private File file;
    private TileView tileView;
    private String format = "png";

    private boolean incomplete = false;
    private Throwable exception;


    /**
     * Construct.
     */
    public TileViewToImageWorker() {

    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        this.tileView = tileView;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean isIncomplete() {
        return incomplete;
    }


    @Override
    protected TileView doInBackground() throws Exception {
        if(file == null) throw new RuntimeException("File not set for TileViewToImageWorker.");
        if(tileView == null) throw new RuntimeException("TileView not set for TileViewToImageWorker.");

        BufferedImage img = new BufferedImage(tileView.getWidth(),
                tileView.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = img.getGraphics();

        // remove the tile selection box by nulling off the selected tile
        tileView.setSelectedTileIndex(-1);
        tileView.repaint();
        tileView.print(g);

        try {
            ImageIO.write(img, format, file);

        } catch (IOException ex) {
            this.exception = ex;
            incomplete = true;
        }

        return tileView;
    }


}
