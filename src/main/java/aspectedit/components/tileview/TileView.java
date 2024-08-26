
package aspectedit.components.tileview;

import aspectedit.components.tileview.layout.TileViewFlowLayout;
import aspectedit.components.tileview.layout.TileViewLayout;
import aspectedit.components.tileview.undo.AddTileUndoableEdit;
import aspectedit.components.tileview.undo.InsertTileUndoableEdit;
import aspectedit.components.tileview.undo.RemoveTileUndoableEdit;
import aspectedit.components.tileview.undo.SetTileUndoableEdit;
import aspectedit.tiles.ITile;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.undo.UndoableEditSupport;


/**
 * The TileView component is used to display implementations
 * of the ITile interface.
 */
public class TileView
        extends JComponent
        implements Scrollable {


    private static final String uiClassID = "TileViewUI";

    /** Used to place this TileView into edit mode (i.e. tiles can be modified). */
    public static final int MODE_EDIT = 1;
    /** Used to place this TileView into select mode. */
    public static final int MODE_SELECT = 2;

    public static final float MAX_ZOOM_FACTOR = 10f;

    protected int mode = MODE_SELECT;
    protected float zoomFactor = 1.0f;
    protected TileViewCellRenderer renderer;
    protected TileViewModel<ITile> model;
    protected TileViewLayout layout;

    /** Index of tile to use for edit mode */
    protected int editTileIndex = 0;

    /** Model to use as a source for editing. */
    protected TileViewModel<ITile> editingModel;

    /* Selected Tile properties */
    protected ITile selectedTile;
    protected int selectedTileIndex = -1;

    protected List<TileSelectionListener> listeners;

    /* Undo support */
    protected UndoableEditSupport undoSupport;

    /**
     * Construct a new TileView component
     */
    public TileView() {
        undoSupport = new UndoableEditSupport();

        //use a null-safe default model
        model = new NullTileViewModel();

        //set up the cell renderer
        renderer = new DefaultTileViewCellRenderer();

        //set up the listener list
        listeners = new ArrayList<TileSelectionListener>();

        //default to a flow layout
        layout = new TileViewFlowLayout();

        this.updateUI();
    }


    @Override
    public String getUIClassID() {
        return uiClassID;
    }

    public void setUI(TileViewUI ui) {
        final ComponentUI oldUI = getUI();

        super.setUI(ui);

        invalidate();

        firePropertyChange("UI", oldUI, ui);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateUI() {
        if(UIManager.get(getUIClassID()) != null) {
            setUI( (TileViewUI) UIManager.getUI(this));
        } else {
            setUI( new BasicTileViewUI() );
        }
    }

    @SuppressWarnings("unchecked")
    public TileViewUI getUI() {
        return (TileViewUI) ui;
    }

    
    /**
     * Fire a TileSelectionEvent to all subscribed listeners.
     * @param tile
     */
    protected void fireTileSelected(int index, ITile tile) {
        TileSelectionEvent evt = new TileSelectionEvent(this, tile, index);

        for(TileSelectionListener l : listeners) {
            l.notifyTileSelected(evt);
        }
    }

    /**
     * Get the currently selected ITile.
     * @return The tile.
     */
    public ITile getSelectedTile() {
        return selectedTile;
    }

    /**
     * Get the index of the currently selected tile in the model.
     * @return The tile index.
     */
    public int getSelectedTileIndex() {
        return selectedTileIndex;
    }

    /**
     * Set the selected tile index.
     * @param index The index of the tile.
     */
    public void setSelectedTileIndex(int index) {
        final int oldSelectedIndex = selectedTileIndex;

        this.selectedTileIndex = index;
        this.selectedTile = index < 0 ? null : model.getTile(index);

        //fire the property change
        firePropertyChange("selectedTileIndex", oldSelectedIndex, index);

        //update the listeners
        fireTileSelected(index, selectedTile);
    }

    /**
     * Set the tile to be used while in edit mode.
     * @param index The index of the tile.
     */
    public void setEditTileIndex(int index) {
        if(editingModel != null && index >= 0 && index < editingModel.size()) {
            final int oldEditTileIndex = this.editTileIndex;

            this.editTileIndex = index;

            firePropertyChange("editTileIndex", oldEditTileIndex, index);
        }
    }


    /**
     * Get the index of the tile to be used for edit mode.
     * @return The tile's index.
     */
    public int getEditTileIndex() {
        return editTileIndex;
    }

    /**
     * Get the model to use as a source of {@link ITile}s when editing.
     *
     * @return The editing TileViewModel
     */
    public TileViewModel getEditingModel() {
        return editingModel;
    }

    /**
     * Set the model to use as a source for editing.
     *
     * @param editingModel The new editing TileViewModel.
     */
    public void setEditingModel(TileViewModel editingModel) {
        final TileViewModel oldEditingModel = this.editingModel;

        this.editingModel = editingModel;

        firePropertyChange("editingModel", oldEditingModel, editingModel);
    }


    /**
     * Set the TileViewLayout to use to position the tiles
     * within the TileView.
     * @param layout The TileViewLayout instance.
     */
    public void setTileViewLayout(TileViewLayout layout) {
        final TileViewLayout oldLayout = this.layout;

        this.layout = layout;

        firePropertyChange("tileViewLayout", oldLayout, layout);
    }

    /**
     * Get the current TileViewLayout.
     * @return The TileViewLayout
     */
    public TileViewLayout getTileViewLayout() {
        return layout;
    }

    /**
     * Calculate the tile at a particular point in the view.
     * @param point The point.
     * @return The tile at the point or null.
     */
    public ITile getTileAtPoint(Point point) {
        int tileIndex = layout.getTileIndexFromPoint(this, model, point);

        // Check to see if the tile is valid. Return null if it isn't.
        if(tileIndex >= 0  && tileIndex < model.size()) {
            return model.getTile(tileIndex);
        } else {
            return null;
        }
    }

    /**
     * Set the tile at a given point.
     * @param point The point.
     * @param tile The tile.
     */
    public void setTileAtPoint(Point point, ITile tile) {
        int tileIndex = layout.getTileIndexFromPoint(this, model, point);

        setTileAtIndex(tileIndex, tile);
    }


	/**
	* Set the tile at a given index in the model.
	* @param index The index.
	* @param tile The tile
	*/
	public void setTileAtIndex(int index, ITile tile) {
            if(index < model.size()) {
            
                // FIXME: the "oldTile" param should probably be a
                // clone of the original since the original may be
                // modified by other means.

                //add an undo action
                undoSupport.postEdit(new SetTileUndoableEdit(
                        model,
                        index,
                        model.getTile(index),
                        tile,
                        true));

                //set the tile
                model.setTile(index, tile);
            }
	}
	
    /**
     * Add a tile into the model (if supported).
     * @param tile The tile.
     */
    public void addTile(ITile tile) {
        if(model.isAddSupported()) {

            //add an undoable edit
            undoSupport.postEdit(new AddTileUndoableEdit(model, tile));
            
            //add the tile to the model
            model.addTile(tile);

            revalidate();
        }
    }


    /**
     * Insert a tile into the model at the specified index.
     * @param tile The tile.
     * @param index The index.
     */
    public void insertTile(ITile tile, int index) {
        if(model.isAddSupported()) {

            //add an undoable edit
            undoSupport.postEdit(new InsertTileUndoableEdit(model, tile, index));

            //add the tile to the model
            model.insertTile(index, tile);

            revalidate();
        }
    }


    /**
     * Remove a tile from the model (if supported).
     * @param tile The tile.
     */
    public void removeTile(ITile tile) {
        if(model.isRemoveSupported()) {

            //add an undoable edit
            undoSupport.postEdit(new RemoveTileUndoableEdit(model, tile));

            //remove the tile from the model
            model.removeTile(tile);

            revalidate();
        }
    }

    public ITile removeTile(int index) {
        if(model.isRemoveSupported()) {
            ITile tile = model.getTile(index);

            removeTile(tile);

            return tile;

        } else {
            throw new RuntimeException("Current model does not support tile removal.");
        }
    }

    /**
     * Register a TileSelectionListener with this TileView.
     * @param l The TileSelectionListener.
     */
    public void addTileSelectionListener(TileSelectionListener l) {
        if(!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    /**
     * Unregister a TileSelectionListener with this TileView.
     * @param l The TileSelectionListener.
     */
    public void removeTileSelectionListener(TileSelectionListener l) {
        listeners.remove(l);
    }

    /**
     * Get the current mode of operation (select/edit).
     * @return The mode.
     */
    public int getMode() {
        return mode;
    }

    /**
     * Set the mode of operation.
     * @param mode The mode.
     */
    public void setMode(int mode) {
        final int oldMode = this.mode;

        this.mode = mode;

        //change the cursor
        if(mode == MODE_EDIT) {

            // change but don't fire a TileSelectionEvent since we're no longer
            // in select mode
            selectedTile = null;
            selectedTileIndex = -1;
            
        } else {
            
            // change selected tile and fire an event
            if(model != null && model.size() > 0) {
                setSelectedTileIndex(0);
            }
        }

        firePropertyChange("mode", oldMode, mode);
    }

    /**
     * Get the TileViewModel.
     * @return
     */
    public TileViewModel getModel() {
        return model;
    }

    /**
     * Set the TileView's model.
     * @param model
     */
    public void setModel(TileViewModel model) {
        if(model == null) throw new IllegalArgumentException("Model cannot be null.");

        final TileViewModel oldModel = this.model;
        
        this.model = model;

        firePropertyChange("model", oldModel, model);
    }


    /**
     * Get the TileViewCellRenderer currently in use by this instance.
     * 
     * @return The cell renderer.
     */
    public TileViewCellRenderer getCellRenderer() {
        return renderer;
    }

    /**
     * Set the TileViewCellRenderer that should be used to draw tiles
     * onto this TileView.
     *
     * @param renderer The new cell renderer.
     */
    public void setCellRenderer(TileViewCellRenderer renderer) {
        if(renderer == null) throw new IllegalArgumentException("Cell renderer cannot be null.");

        final TileViewCellRenderer oldRenderer = this.renderer;

        this.renderer = renderer;

        firePropertyChange("cellRenderer", oldRenderer, renderer);
    }

    /**
     * Get the zoom level.
     * @return The current zoom level.
     */
    public float getZoomFactor() {
        return zoomFactor;
    }

    /**
     * Set the zoom level.
     * @param zoomFactor The new zoom level.
     */
    public void setZoomFactor(float zoomFactor) {
        if(zoomFactor < 1) throw new IllegalArgumentException("Zoom factor cannot be less than 1.");

        float oldZoom = this.zoomFactor;

        this.zoomFactor = zoomFactor;

        firePropertyChange("zoomFactor", oldZoom, zoomFactor);
    }
    

    @Override
    public Dimension getPreferredSize() {
        // if the TileView is not in a JViewport use the default preferred size
        if(! (getParent() instanceof JViewport)) {
            return super.getPreferredSize();
        }

        // the TileView is part of a viewport.
        // delegate to the tile layout manager to calculate the preferred size
        Dimension d = layout.calculatePreferredSize(this, model, new Dimension(super.getPreferredSize()));

        return d;
    }

    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();

        //return layout.calculatePreferredSize(this, model, null);
    }


    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        if(orientation == SwingConstants.HORIZONTAL) {
            return (int)(model.getTileWidth() * zoomFactor);
        } else {
            return (int)(model.getTileHeight());
        }
    }


    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        if(orientation == SwingConstants.HORIZONTAL) {
            return (int)(model.getTileWidth() * zoomFactor * 5);
        } else {
            return (int)(model.getTileHeight() * zoomFactor * 5);
        }
    }


    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }


    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


    public void removeUndoableEditListener(UndoableEditListener l) {
        undoSupport.removeUndoableEditListener(l);
    }

    public void addUndoableEditListener(UndoableEditListener l) {
        undoSupport.addUndoableEditListener(l);
    }

}
