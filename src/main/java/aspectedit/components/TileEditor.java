
package aspectedit.components;

import aspectedit.tiles.ITile;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author mark
 */
public class TileEditor extends JComponent {

    private UndoableEditSupport undoSupport;

    private ITile tile;
    private float zoomFactor;
    private int colour;
    private boolean showGrid = true;

    private transient Point tileOrigin;


    /**
     * Construct a new TileEditor
     */
    public TileEditor() {
        undoSupport = new UndoableEditSupport();
        
        tileOrigin = new Point();

        //attach a mouse event listener
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                requestFocusInWindow();
                
                onMouseDown(evt);
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                onMouseDragged(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                onMouseUp(evt);
            }


        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);

        setFocusable(true);
    }


    //<editor-fold defaultstate="collapsed" desc="Event Handlers">

    //variables to hold the coordinate of the previous edit
    private int prevEditX, prevEditY;

    /**
     * Handle the press of a mouse button.
     * @param evt The mouse event.
     */
    private void onMouseDown(MouseEvent evt) {
		if(evt.getButton() != MouseEvent.BUTTON1) return;
		
        int clickx = (int)((evt.getX() - tileOrigin.x) / zoomFactor);
        int clicky = (int)((evt.getY() - tileOrigin.y) / zoomFactor);

        if(clickx >= 0
                && clicky >= 0
                && clickx < tile.getWidth()
                && clicky < tile.getHeight()) {

            prevEditX = clickx;
            prevEditY = clicky;

            undoSupport.postEdit(new PixelEdit(
                    clickx,
                    clicky,
                    tile.getPixel(clickx, clicky),
                    colour,
                    true));

            tile.setPixel(colour, clickx, clicky);
            
            repaint();
        }
    }

    /**
     * Handle the release of a mouse button.
     * @param evt The mouse event.
     */
    private void onMouseUp(MouseEvent evt) {
        prevEditX = -1;
        prevEditY = -1;
    }

    /**
     * Handle a mouse drag event. Sets a pixel in the image and posts an
     * undoable edit if the coordinate has changed.
     * @param evt The mouse event.
     */
    private void onMouseDragged(MouseEvent evt) {
        int clickx = (int)((evt.getX() - tileOrigin.x) / zoomFactor);
        int clicky = (int)((evt.getY() - tileOrigin.y) / zoomFactor);

        if((clickx != prevEditX          // check that coordinate has changed since
                || clicky != prevEditY)  // last edit and that the click is within the
                && clickx >= 0           // tile's bounds
                && clicky >= 0
                && clickx < tile.getWidth()
                && clicky < tile.getHeight()) {

            prevEditX = clickx;
            prevEditY = clicky;

            undoSupport.postEdit(new PixelEdit(
                    clickx,
                    clicky,
                    tile.getPixel(clickx, clicky),
                    colour,
                    false));

            tile.setPixel(colour, clickx, clicky);
            repaint();
        }
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Accessor/Mutators">
    public ITile getTile() {
        return tile;
    }

    public void setTile(ITile tile) {
        this.tile = tile;
        calculateTileOrigin();
        repaint();
    }

    public float getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(float zoomFactor) {
        if (zoomFactor < 1) {
            throw new IllegalArgumentException("Zoom factor cannot be < 1.");
        }

        this.zoomFactor = zoomFactor;
        calculateTileOrigin();
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;

        repaint();
    }

    public int getColour() {
        return colour;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Undo Support">
    public void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    public void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }
    //</editor-fold>

    
    /**
     * Calculate the position within this component's bounds at which
     * to draw the tile.
     */
    private void calculateTileOrigin() {
        if(tile != null) {
            tileOrigin.x = (int)(getWidth() - (tile.getWidth() * zoomFactor)) / 2;
            tileOrigin.y = (int)(getHeight() - (tile.getHeight() * zoomFactor)) / 2;
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        // clear the component
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        // check that we actually have something to draw
        if (tile == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }

        // calculate the size of the tile
        int tw = (int) (tile.getWidth() * zoomFactor);
        int th = (int) (tile.getHeight() * zoomFactor);


        g.drawImage(
                tile.getImage(),
                tileOrigin.x,
                tileOrigin.y,
                tw,
                th,
                null);
        
        // draw a border
        g.setColor(Color.BLACK);
        g.drawRect(
                tileOrigin.x - 1,
                tileOrigin.y - 1,
                tw + 2,
                th + 2);

        // draw a grid around each pixel, if required
        if (isShowGrid()) {
            g.setColor(Color.LIGHT_GRAY);
            int stepX = tw / tile.getWidth();
            int stepY = th / tile.getHeight();

            for (int y = tileOrigin.y; y < tileOrigin.y + th; y += stepY) {
                g.drawLine(tileOrigin.x, y, tileOrigin.x + tw, y);
            }

            for (int x = tileOrigin.x; x < tw + tileOrigin.x; x += stepX) {
                g.drawLine(x, tileOrigin.y, x, tileOrigin.y + th);
            }
        }
    }


    /**
     * Class that represents an edit action performed on the tile.
     */
    protected class PixelEdit extends AbstractUndoableEdit {

        private int x, y;
        private int oldColour;
        private int newColour;
        private boolean significant;
        /**
         * Construct
         * @param x The pixel's x-coordinate.
         * @param y The pixel's y-coordinate.
         * @param oldColour The original colour of the pixel.
         * @param newColour The pixel's new colour.
         */
        public PixelEdit(int x, int y, int oldColour, int newColour, boolean significant) {
            this.x = x;
            this.y = y;
            this.oldColour = oldColour;
            this.newColour = newColour;
            this.significant = significant;
        }

        @Override
        public boolean canRedo() {
            return true;
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public String getPresentationName() {
            return "Edit";
        }

        @Override
        public void redo() throws CannotRedoException {
            tile.setPixel(newColour, x, y);
        }

        @Override
        public void undo() throws CannotUndoException {
            tile.setPixel(oldColour, x, y);
        }

        @Override
        public boolean isSignificant() {
            return significant;
        }


    }

}
