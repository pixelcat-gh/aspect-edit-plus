
package aspectedit.components.tileview.adapters;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.*;

/**
 * An implementation of {@link TileViewModel} than can
 * be used to display a grid of tiles in a user-defined
 * arrangement.
 */
public class TileArrangerModel
        extends TileViewModelSupport
        implements TileViewModel<Tile> {

    // Keep a blank tile. We're not using Tile.EMPTY_TILE as
    // we need to be able to manipulate its data.
    private Tile blankTile = new Tile();

    private Tileset tileset = Tileset.EMPTY_TILESET;
    private int width = 10;
    private int height = 10;

    private int[][] data;

    /**
     * Construct a new TileArrangerModel using the
     * default size of 10x10 tiles.
     */
    public TileArrangerModel() {
        this(10, 10);
    }

    /**
     * Construct a new TileArrangerModel using the
     * specified dimensions.
     *
     * @param w The width in tiles.
     * @param h The height in tiles.
     */
    public TileArrangerModel(int w, int h) {
        this.width = w;
        this.height = h;

        //initialise the data array
        data = new int[height][width];
        clearArray(data);
    }

    /**
     * Get the tileset.
     * @return The tileset.
     */
    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Set the tileset.
     * @param tileset The tileset.
     */
    public void setTileset(Tileset tileset) {
        this.tileset = tileset;

        fireStructureChanged();
    }

    /**
     * Initialises each element of the data array to -1.
     */
    private void clearArray(int[][] array) {
        for(int y=0; y<array.length; y++) {
            for(int x=0; x<array[y].length; x++) {
                array[y][x] = -1;
            }
        }
    }

    /**
     * Resizes the data array while preserving as much data as possible.
     */
    private void recreateDataArray() {
        int[][] newdata = new int[height][width];

        clearArray(newdata);

        //copy as much of the previous array as possible to the new array
        for(int y=0; y<Math.min(data.length, newdata.length); y++) {
            for(int x=0; x<Math.min(data[y].length, newdata[y].length); x++) {
                newdata[y][x] = data[y][x];
            }
        }

        this.data = newdata;
    }


    /**
     * Clear the data from the model.
     */
    public void clear() {
        clearArray(data);

        fireStructureChanged();
    }

    /**
     * Sets the width of the model. Attempts to preserve as much data
     * as possible.
     *
     * @param width The width in tiles.
     */
    public void setWidth(int width) {
        this.width = width;

        recreateDataArray();

        fireStructureChanged();
    }

    /**
     * Get the width of the model.
     *
     * @return The width in tiles.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Sets the height of the model. Attempts to preserve as much data
     * as possible.
     *
     * @param height The height in tiles.
     */
    public void setHeight(int height) {
        this.height = height;

        recreateDataArray();

        fireStructureChanged();
    }

    /**
     * Get the height of the model.
     * @return The height in tiles.
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Return the size of the model (total number of cells).
     *
     * @return The number of cells.
     */
    @Override
    public int size() {
        return width * height;
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
        // get the tile's index from the data array
        int tileIndex = data[index / width][index % width];

        if(tileIndex < 0 || tileIndex >= tileset.size()) {
            //if the tile index is out of bounds, return the blank tile
            blankTile.setPalette(tileset.getPalette());

            return blankTile;
            
        } else {
            //get the tile from the tileset
            return tileset.getTile(tileIndex);
        }
    }

    @Override
    public void setTile(int index, ITile tile) {
        if(! (tile instanceof Tile)) {
            throw new IllegalArgumentException(
                    String.format("Illegal tile type: %s.", tile.getClass()));
        }

        if(index < 0) {
            throw new IllegalArgumentException("Index cannot be < 0.");
        }

        final Tile oldTile = getTile(index);

        //get the tile's index...
        int tileIndex = tileset.indexOf( (Tile) tile);
        //...and set it in the data array
        data[index / width][index % width] = tileIndex;

        fireTileChanged(index, oldTile, tile);
    }

    public void setTile(int index, int tileIndex) {
        final Tile oldTile = getTile(index);

        Tile newTile = null;

        if(tileIndex >= 0 && tileIndex < tileset.size()) {
            newTile = tileset.getTile(tileIndex);
        } else {
            newTile = Tile.EMPTY_TILE;
        }

        data[index / width][index % width] = tileIndex;

        fireTileChanged(index, oldTile, newTile);
    }

    public void setTileAt(int x, int y, int tileIndex) {
        setTile(y * width + x, tileIndex);
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
    public void removeTile(Tile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTile(Tile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertTile(int index, Tile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Tile tile) {
        // This method is meaningless for this type of model.
        return 0;
    }

}
