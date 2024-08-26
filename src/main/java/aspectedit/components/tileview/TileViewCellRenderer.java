
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;
import java.awt.Graphics2D;

/**
 * Implementations of this class can be used to paint
 * the cells of a TileView.
 */
public interface TileViewCellRenderer {

    public void render(
            Graphics2D g,
            TileViewModel model,
            ITile tile,
            int x,
            int y,
            int w,
            int h,
            boolean selected);

}
