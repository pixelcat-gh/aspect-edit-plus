
package aspectedit.components.tileview;

import aspectedit.components.tileview.layout.TileViewLayout;
import aspectedit.tiles.ITile;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 *
 */
@SuppressWarnings("unchecked")
public class BasicTileViewUI extends TileViewUI {

    protected TileView tileView;
    protected MouseAdapter mouseHandler;
    protected KeyListener keyHandler;
    protected ModelListener modelListener;
    protected PropertyChangeListener propertyListener;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        this.tileView = (TileView) c;

        //default to double buffering
        tileView.setDoubleBuffered(true);
        
        installComponents();
        installListeners();
        installKeyListener();
        installMouseListener();
    }

    @Override
    public void uninstallUI(JComponent c) {
        uninstallMouseListener();
        uninstallKeyListener();
        uninstallListeners();
        uninstallComponents();

        super.uninstallUI(c);
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }


    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);

        if(d == null) d = new Dimension(0,0);
        
        TileViewModel model = tileView.getModel();

        Container parent = c.getParent();

        // honour any dimensions set by the model
        if(model.getWidth() > 0) {
            d.width = (int)(model.getWidth() * model.getTileWidth() * tileView.getZoomFactor());

        } else {
            // try to calculate a width that fills the parent container
            // if the parent is not available use the model's size.
            if(parent != null) {
                Insets parentInsets = parent.getInsets();
                int availableWidth = parent.getWidth() - parentInsets.left - parentInsets.right;

                if(availableWidth >= getDrawableTileWidth()) {
                    d.width = availableWidth;

                } else {
                    d.width = (int)(Math.sqrt(model.size()) * getDrawableTileWidth());
                }

            } else {
                d.width = getDrawableTileWidth();
            }
        }

        if(model.getHeight() > 0) {
            d.height = (int)(model.getHeight() * model.getTileHeight() * tileView.getZoomFactor());
        } else {

            // use the current width to calculate the height
            if(d.width > 0) {
                d.height = (int) Math.ceil( model.size() / (d.width / getDrawableTileWidth()) ) * getDrawableTileHeight();
            } else {
                d.height = (int)(Math.sqrt(model.size()) * model.getTileHeight() * tileView.getZoomFactor());
            }
        }

        return d;
    }

    protected void installComponents() {
    }

    protected void uninstallComponents() {
    }

    protected void installMouseListener() {
        mouseHandler = new DefaultMouseHandler();
        propertyListener = new PropertyHandler();

        tileView.addPropertyChangeListener(propertyListener);
        tileView.addMouseListener(mouseHandler);
        tileView.addMouseMotionListener(mouseHandler);
    }

    protected void uninstallMouseListener() {
        tileView.removePropertyChangeListener(propertyListener);
        tileView.removeMouseMotionListener(mouseHandler);
        tileView.removeMouseListener(mouseHandler);
    }

    /**
     * Install any key listeners on the component.
     */
    protected void installKeyListener() {
        keyHandler = new DefaultKeyHandler();
        tileView.addKeyListener(keyHandler);
    }

    /**
     * Remove any key listeners from the component.
     */
    protected void uninstallKeyListener() {
        tileView.removeKeyListener(keyHandler);
    }

    protected void installListeners() {
        modelListener = new ModelListener();
    }

    protected void uninstallListeners() {
    }

    /**
     * Calculate the width of a tile, taking zoom into account.
     * @return
     */
    private int getDrawableTileWidth() {
        TileViewModel model = tileView.getModel();

        if (model == null) {
            return 0;
        }
        return (int) (model.getTileWidth() * tileView.getZoomFactor());
    }

    /**
     * Calculate the height of a tile, taking zoom into account.
     * @return
     */
    private int getDrawableTileHeight() {
        TileViewModel model = tileView.getModel();

        if (model == null) {
            return 0;
        }
        return (int) (model.getTileHeight() * tileView.getZoomFactor());
    }

    /**
     * Paints the TileView UI.
     * @param g The Graphics object.
     * @param c The component.
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        g.setColor(Color.BLACK);

        int width = tileView.getWidth();
        int height = tileView.getHeight();

        g.fillRect(0, 0, width, width);

        TileViewModel model = tileView.getModel();
        TileViewLayout layout = tileView.getTileViewLayout();
        TileViewCellRenderer renderer = tileView.getCellRenderer();
        int selectedTileIndex = tileView.getSelectedTileIndex();

        if (model == null || width == 0 || height == 0 || model.size() == 0) {
            return;
        }

        Rectangle clip = g.getClipBounds();

        Graphics2D g2 = (Graphics2D) g.create();

        // calculate the tile width, taking zooming into account
        int tw = getDrawableTileWidth();
        int th = getDrawableTileHeight();

        // iterate over the tiles in the model and paint as necessary
        for (int i = 0; i < model.size(); i++) {

            //ask the layout for an origin coordinate for the tile
            Point point = layout.getCoordinateForTile(tileView, model, i);

            Rectangle tileBounds = new Rectangle(point.x, point.y, tw, th);

            //paint if the tile falls within the clip bounds
            if( clip.intersects(tileBounds) ) {

                //draw the tile
                ITile tile = model.getTile(i);

                renderer.render(g2, model, tile,
                        point.x,
                        point.y,
                        tw,
                        th,
                        selectedTileIndex == i);
            }

        }

        g2.dispose();
    }


    /**
     * Create an instanceof this UI class.
     * @return A new instance.
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicTileViewUI();
    }


    /**
     * A class to listen to events fired from the model. Performs repaints
     * as necessary.
     */
    protected class ModelListener implements TileViewModelListener {

        @Override
        public void notifyTileViewModelEvent(TileViewModelEvent evt) {
            switch(evt.getType()) {
                //the model has changed drastically - repaint everything
                case TileViewModelEvent.TILE_ADDED:
                case TileViewModelEvent.TILE_REMOVED:
                case TileViewModelEvent.STRUCTURE_CHANGED:
                    tileView.revalidate();
                    if(tileView.isShowing()) {
                        tileView.repaint();
                    }
                    break;

                //a single tile was changed - repaint it
                case TileViewModelEvent.TILE_CHANGED:
                    TileViewLayout layout = tileView.getTileViewLayout();

                    Point p = layout.getCoordinateForTile(
                            tileView,
                            tileView.getModel(),
                            evt.getTileIndex());

                    if(tileView.isShowing()) {
                        // repaint only the necessary region
                        tileView.repaint(new Rectangle(
                                p.x,
                                p.y,
                                getDrawableTileWidth(),
                                getDrawableTileHeight()));
                    }
                    break;

            }
        }

    }

    /**
     * Mouse input handler for the TileView component
     */
    protected class DefaultMouseHandler extends MouseAdapter {

        int mode = -1;
        int previousEditIndex = -1;
        TileViewLayout layout;
        TileViewModel model;
        ITile editTile;
        boolean editing = false;

        int previousOverIndex = -1;

        /**
         * Mouse entered the TileView bounds.
         * @param e The MouseEvent.
         */
        @Override
        public void mouseEntered(MouseEvent e) {
            // store the variables for later
            layout = tileView.getTileViewLayout();
            model = tileView.getModel();
            mode = tileView.getMode();

            TileViewModel editingModel = tileView.getEditingModel();
            if(editingModel != null && editingModel.size() > 0) {
                editTile = editingModel.getTile(tileView.getEditTileIndex());
            }
        }

        /**
         * Mouse left the TileView bounds.
         * @param e The MouseEvent
         */
        @Override
        public void mouseExited(MouseEvent e) {
            // repaint the last tile that the mouse was over
            Point p = layout.getCoordinateForTile(tileView, model, previousOverIndex);

            int th = getDrawableTileHeight();
            int tw = getDrawableTileWidth();

            Rectangle r = new Rectangle(p.x, p.y, tw, th);
            tileView.repaint(r);

            
            previousOverIndex = -1;
            editTile = null;
        }


        /**
         * Mouse moved over the TileView.
         * @param e The MouseEvent.
         */
        @Override
        public void mouseMoved(MouseEvent e) {
            // bail out if we're not editing
            if(mode != TileView.MODE_EDIT || editTile == null) return;

            // get the index of the tile under the mouse cursor
            int index = layout.getTileIndexFromPoint(tileView, model, e.getPoint());

            // if the cursor has moved to the next tile redraw the old one
            if(index != previousOverIndex) {

                TileViewModel editingModel = tileView.getEditingModel();
                if(editingModel != null && editingModel.size() > 0) {
                    editTile = editingModel.getTile(tileView.getEditTileIndex());
                }

                // draw the new tile
                Point p = layout.getCoordinateForTile(tileView, model, index);

                int th = getDrawableTileHeight();
                int tw = getDrawableTileWidth();

                // calculate the clip bounds for the graphics object
                Rectangle r = new Rectangle(p.x+1, p.y+1, tw-2, th-2);

                Graphics g = tileView.getGraphics();
                g.setClip(r);

                // draw the tile
                tileView.getGraphics().drawImage(editTile.getImage(),
                        r.x-1, r.y-1, r.width+2, r.height+2, null);


                // redraw the old tile
                p = layout.getCoordinateForTile(tileView, model, previousOverIndex);
                
                r.x = p.x; r.y = p.y; r.width = tw; r.height = th;
                tileView.repaint(r);

                // store the current tile index
                previousOverIndex = index;
            }

        }


        /**
         * Mouse was dragged over the TileView.
         * @param e The MouseEvent
         */
        @Override
        public void mouseDragged(MouseEvent e) {
            if(mode == TileView.MODE_EDIT && editing) {
               // which tile is the mouse over?
                int tileIndex = layout.getTileIndexFromPoint(tileView, model, e.getPoint());

                // only update if the tile index has changed
                if(previousEditIndex != tileIndex) {

                    if( tileIndex >= 0 && tileIndex < model.size()) {
                        model.setTile(tileIndex, editTile);
                    }

                    previousEditIndex = tileIndex;
                }
            }
        }


        @Override
        public void mousePressed(MouseEvent e) {
            tileView.requestFocusInWindow();

            
            // which tile is the mouse over?
            int tileIndex = layout.getTileIndexFromPoint(tileView, model, e.getPoint());

            mode = tileView.getMode();
            
            if(mode == TileView.MODE_EDIT && e.getButton() == MouseEvent.BUTTON1) {
                if(tileView.getEditingModel() == null) return;
                
                if(tileIndex >= 0 && tileIndex < model.size()) {

                    editing = true;

                    this.editTile = tileView.getEditingModel().getTile(
                            tileView.getEditTileIndex());

                    previousEditIndex = tileIndex;
                    model.setTile(tileIndex, editTile);
                }

            } else if(mode == TileView.MODE_SELECT && e.getButton() == MouseEvent.BUTTON1) {

                if(tileIndex >= 0 && tileIndex < model.size()) {
                    tileView.setSelectedTileIndex(tileIndex);
                }

            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(e.getButton() == MouseEvent.BUTTON1) {
                int tileIndex = layout.getTileIndexFromPoint(tileView, model, e.getPoint());

                if(tileIndex >= 0 && tileIndex < model.size()) {
                    tileView.setSelectedTileIndex(tileIndex);
                }
                //reset all state variables
                editing = false;

                previousEditIndex = -1;
            }
        }
    }


    protected class DefaultKeyHandler extends KeyAdapter {

    }

    /**
     * Input handler class for the TileView
     */
    protected class PropertyHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if("model".equals(evt.getPropertyName())) {
                //remove the listener from the old model and add it to the new one
                ((TileViewModel) evt.getOldValue()).removeTileViewModelListener(modelListener);
                ((TileViewModel) evt.getNewValue()).addTileViewModelListener(modelListener);

                tileView.revalidate();
                tileView.repaint();

            } else if("zoomFactor".equals(evt.getPropertyName())) {
                tileView.revalidate();
                tileView.repaint();

            } else if("selectedTileIndex".equals(evt.getPropertyName())) {
                if(tileView.isShowing()) {

                    TileViewLayout layout = tileView.getTileViewLayout();
                    TileViewModel model = tileView.getModel();
                    
                    //repaint the old tile
                    Point p = layout.getCoordinateForTile(
                            tileView,
                            model,
                            (Integer) evt.getOldValue());

                    int th = getDrawableTileHeight();
                    int tw = getDrawableTileWidth();

                    tileView.repaint(new Rectangle(p.x-2, p.y-2, th+2, tw+2));

                    //repaint the new tile
                    p = layout.getCoordinateForTile(
                            tileView,
                            model,
                            (Integer) evt.getNewValue());

                    tileView.repaint(new Rectangle(p.x-2, p.y-2, th+2, tw+2));
                }

            } else if("tileViewLayout".equals(evt.getPropertyName())) {
                tileView.revalidate();
                
            }
        }


    }
}
