
package aspectedit.components.tileview.layout;

import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewModel;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 */
public interface TileViewLayout {

    Dimension calculatePreferredSize(TileView view, TileViewModel model, Dimension preferredSize);

    Point getCoordinateForTile(TileView view, TileViewModel model, int tileIndex);

    int getTileIndexFromPoint(TileView view, TileViewModel model, Point point);
}
