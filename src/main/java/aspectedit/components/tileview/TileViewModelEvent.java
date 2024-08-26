
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;
import java.util.EventObject;

/**
 *
 */
public class TileViewModelEvent extends EventObject {

    public static final int STRUCTURE_CHANGED = 1;
    public static final int TILE_ADDED = 2;
    public static final int TILE_REMOVED = 3;
    public static final int TILE_CHANGED = 4;

    private int type;
    private ITile tile;
    private ITile oldTile;
    private int tileIndex;


    public TileViewModelEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

    public TileViewModelEvent(Object source, int type, int tileIndex, ITile tile) {
        super(source);
        this.type = type;
    }

    public TileViewModelEvent(Object source, int type, ITile oldTile, ITile newTile, int tileIndex) {
        super(source);
        this.type = type;
        this.tile = newTile;
        this.oldTile = oldTile;
        this.tileIndex = tileIndex;
    }


    public int getType() {
        return type;
    }

    public ITile getTile() {
        return tile;
    }

    public ITile getOldTile() {
        return oldTile;
    }

    public int getTileIndex() {
        return tileIndex;
    }

}
