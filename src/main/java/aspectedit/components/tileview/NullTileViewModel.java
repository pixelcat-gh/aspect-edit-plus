
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;
import aspectedit.tiles.Tile;

/**
 * A simple, do-nothing, implementation of a TileViewModel
 * that can be used to satisfy a TileView's requirements without
 * actually providing any data.
 *
 * @author mark
 */
public class NullTileViewModel implements TileViewModel<ITile> {

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public int getHeight() {
        return -1;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int getTileWidth() {
        return 8;
    }

    @Override
    public int getTileHeight() {
        return 8;
    }

    @Override
    public ITile getTile(int index) {
        return Tile.EMPTY_TILE;
    }

    @Override
    public void setTile(int index, ITile tile) {

    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public void removeTile(ITile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTile(ITile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertTile(int index, ITile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTileViewModelListener(TileViewModelListener l) {

    }

    @Override
    public void removeTileViewModelListener(TileViewModelListener l) {
        
    }

    @Override
    public int indexOf(ITile tile) {
        return 0;
    }

}
