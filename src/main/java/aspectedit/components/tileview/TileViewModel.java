
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;

/**
 * Implementations of this interface are used as the
 * data model for the {@link TileView} component.
 */
public interface TileViewModel<T extends ITile> {

    /**
     * Get the preferred width of the display. Implementations
     * may return -1 if the width is irrelevant.
     *
     * @return The width in tiles.
     */
    public int getWidth();

    /**
     * Get the preferred height of the display. Implementations
     * may return -1 if the height is irrelevant.
     *
     * @return The height in tiles.
     */
    public int getHeight();

    /**
     * Get the size of the model.
     *
     * @return The size in tiles.
     */
    public int size();

    /**
     * The width of a single tile in pixels.
     *
     * @return The width.
     */
    public int getTileWidth();

    /**
     * The height of a single tile in pixels.
     *
     * @return The height.
     */
    public int getTileHeight();

    /**
     * Get a the tile at the specified index.
     *
     * @param index The tile's index.
     * @return The tile.
     */
    public T getTile(int index);

    /**
     * Set the tile at the specified index.
     *
     * @param index The index.
     * @param tile The tile.
     */
    public void setTile(int index, ITile tile);

    /**
     * Returns true if the model supports addition of new tiles.
     *
     * @return
     */
    public boolean isAddSupported();

    /**
     * Returns true if the model supports removal of tiles.
     *
     * @return
     */
    public boolean isRemoveSupported();

    /**
     * Get the index of the specified tile within the model.
     *
     * @param tile The tile.
     * @return The tile's index or -1 if it is not part of the model.
     */
    public int indexOf(T tile);

    /**
     * Remove a tile from the model.
     *
     * @param tile The tile.
     */
    public void removeTile(T tile);

    /**
     * Add a tile to the model
     * 
     * @param tile The tile.
     */
    public void addTile(T tile);

    /**
     * Insert a tile at the specified position.
     *
     * @param index The position within the model.
     * @param tile The tile.
     */
    public void insertTile(int index, T tile);

    /**
     * Register a {@link TileViewModelListener} with this model.
     * 
     * @param l The listener.
     */
    public void addTileViewModelListener(TileViewModelListener l);

    /**
     * Unregister a {@link TileViewModelListener} from this model.
     *
     * @param l The listener.
     */
    public void removeTileViewModelListener(TileViewModelListener l);

}
