
package aspectedit.components.tileview.adapters;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.*;

/**
 * An adapter class that allows a Tileset to be used as the
 * data model for a {@link TileView} component.
 */
public class TilesetViewAdapter
        extends TileViewModelSupport
        implements TileViewModel<Tile> {

    protected Tileset tileset = Tileset.EMPTY_TILESET;
    protected int width = 1;
    protected int height = 1;

    /**
     * Construct
     */
    public TilesetViewAdapter() {
    }

    /**
     * Get the Tileset.
     * @return The tileset.
     */
    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Set the Tileset.
     * @param tileset The tileset.
     */
    public void setTileset(Tileset tileset) {
        this.tileset = tileset;

        //calculate a new height based on the current width.
        this.height = Math.max(
                1,
                (int)Math.ceil(tileset.size() / width + 0.5));

        fireStructureChanged();
    }

    /**
     * Set the preferred width of the view.
     *
     * @param width The width in tiles.
     */
    public void setWidth(int width) {
        if (width < 1) {
            throw new IllegalArgumentException("Width cannot be < 1.");
        }

        this.width = width;
        fireStructureChanged();
    }

    /**
     * Get the preferred width of the view.
     *
     * @return The width in tiles.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Set the preferred height of the view.
     *
     * @param height The height in tiles.
     */
    public void setHeight(int height) {
        if (height < 1) {
            throw new IllegalArgumentException("Height cannot be < 1.");
        }

        this.height = height;
        fireStructureChanged();
    }

    /**
     * Get the preferred height of the view.
     *
     * @return The height in tiles.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Get the size of the model.
     *
     * @return The size.
     */
    @Override
    public int size() {
        return tileset.size();
    }

    @Override
    public int getTileWidth() {
        return tileset.getTileWidth();
    }

    @Override
    public int getTileHeight() {
        return tileset.getTileHeight();
    }

    @Override
    public Tile getTile(int index) {
        return tileset.getTile(index);
    }

    @Override
    public void setTile(int index, ITile tile) {
		if(tile instanceof Tile) {
			final Tile oldTile = getTile(index);

			tileset.setTile(index, (Tile) tile);

			fireTileChanged(index, oldTile, (Tile) tile);
		}
    }

    @Override
    public boolean isAddSupported() {
        return true;
    }

    @Override
    public boolean isRemoveSupported() {
        return true;
    }

    @Override
    public void removeTile(Tile tile) {
        tileset.removeTile(tile);

        fireTileRemoved(width, tile);
    }

    @Override
    public void addTile(Tile tile) {
        tileset.addTile(tile);

        fireTileAdded(tileset.size()-1, tile);
    }

    @Override
    public void insertTile(int index, Tile tile) {
        tileset.insertTile(index, tile);

        fireTileAdded(index, tile);
    }

    @Override
    public int indexOf(Tile tile) {
        return tileset.indexOf(tile);
    }
}
