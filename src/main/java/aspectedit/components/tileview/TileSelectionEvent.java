
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;
import java.util.EventObject;

/**
 *
 */
public class TileSelectionEvent extends EventObject {

    private ITile tile;
    private int index;

    public TileSelectionEvent(Object source, ITile tile, int index) {
        super(source);
        this.tile = tile;
        this.index = index;
    }

    public ITile getTile() {
        return tile;
    }

    public int getIndex() {
        return index;
    }

}
