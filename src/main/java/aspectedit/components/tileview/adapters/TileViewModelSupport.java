
package aspectedit.components.tileview.adapters;

import aspectedit.components.tileview.TileViewModelEvent;
import aspectedit.components.tileview.TileViewModelListener;
import aspectedit.tiles.ITile;
import java.util.ArrayList;
import java.util.List;

/**
 * A support class that provides common {@link TileViewModelListener} and
 * event management facilities for implementations of {@link TileViewModel}.
 */
public abstract class TileViewModelSupport {

    protected List<TileViewModelListener> listeners;

    public TileViewModelSupport() {
        listeners = new ArrayList<TileViewModelListener>();
    }

    public void addTileViewModelListener(TileViewModelListener l) {
        if(!listeners.contains(l)) listeners.add(l);
    }

    public void removeTileViewModelListener(TileViewModelListener l) {
        listeners.remove(l);
    }

    /**
     * Notify any listeners that a tile was added to the model.
     *
     * @param index The tile's index.
     * @param tile The tile.
     */
    protected void fireTileAdded(int index, ITile tile) {
        TileViewModelEvent evt = new TileViewModelEvent(
                this, TileViewModelEvent.TILE_ADDED, index, tile);

        for(TileViewModelListener l : listeners) {
            l.notifyTileViewModelEvent(evt);
        }
    }

    /**
     * Notify any listeners that a tile was removed from the model.
     *
     * @param index The tile's index.
     * @param tile The tile.
     */
    protected void fireTileRemoved(int index, ITile tile) {
        TileViewModelEvent evt = new TileViewModelEvent(
                this, TileViewModelEvent.TILE_REMOVED, index, tile);

        for(TileViewModelListener l : listeners) {
            l.notifyTileViewModelEvent(evt);
        }
    }

    /**
     * Notify any listeners that a tile was changed.
     * @param index The tile's index.
     * @param oldTile The previous tile.
     * @param newTile The new tile.
     */
    protected void fireTileChanged(int index, ITile oldTile, ITile newTile) {
        TileViewModelEvent evt = new TileViewModelEvent(this, 
                TileViewModelEvent.TILE_CHANGED, oldTile, newTile, index);

        for(TileViewModelListener l : listeners) {
            l.notifyTileViewModelEvent(evt);
        }
    }

    /**
     * Notify any listeners that the overall structure of the model
     * has changed.
     */
    protected void fireStructureChanged() {
        TileViewModelEvent evt = new TileViewModelEvent(
                this, TileViewModelEvent.STRUCTURE_CHANGED);

        for(TileViewModelListener l : listeners) {
            l.notifyTileViewModelEvent(evt);
        }
    }
    
}
