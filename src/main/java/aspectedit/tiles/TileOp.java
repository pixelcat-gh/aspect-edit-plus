
package aspectedit.tiles;

/**
 * This interface represents an operation that can be performed on a Tile's data.
 *
 * @author Mark Barnett
 */
public interface TileOp {

    /**
     * Returns true if the operation modifies the tile's data in place.
     * @return
     */
	public boolean isFilterInPlace();

    /**
     * Performs the operation on the specified tile.
     * @param input The Tile.
     * @return The Tile.
     */
	public Tile filter(Tile input);
	
}