
package aspectedit.components.tileview.layout;

import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewModel;
import java.awt.Dimension;
import java.awt.Point;

/**
 *
 * @author mark
 */
public class TileViewGridLayout implements TileViewLayout {

    @Override
    public Dimension calculatePreferredSize(TileView view, TileViewModel model, Dimension preferredSize) {
        if(preferredSize == null || !view.isPreferredSizeSet()) {
            preferredSize = new Dimension();
            preferredSize.width = (int)(model.getWidth() * model.getTileWidth() * view.getZoomFactor());
            preferredSize.height = (int)(model.getHeight() * model.getTileHeight() * view.getZoomFactor());
        }

        return preferredSize;
    }

    @Override
    public Point getCoordinateForTile(TileView view, TileViewModel model, int tileIndex) {
        int tw = (int)(model.getTileWidth() * view.getZoomFactor());
        int th = (int)(model.getTileHeight() * view.getZoomFactor());

        int x = tileIndex % model.getWidth();
        int y = tileIndex / model.getWidth();

        if(y < model.getHeight()) {
            //draw tiles in a grid
            return new Point(x * tw, y * th);

        } else {
            //any remaining tiles to be drawn offscreen
            return new Point(-tw, -th);
        }
    }

    @Override
    public int getTileIndexFromPoint(TileView view, TileViewModel model, Point point) {
        int tw = (int)(model.getTileWidth() * view.getZoomFactor());
        int th = (int)(model.getTileHeight() * view.getZoomFactor());

        int x = point.x / tw;
        int y = point.y / th;

        if(x < model.getWidth()) {
            //int index = y * (view.getWidth() / tw) + x;
            int width = model.getWidth() == -1 ? view.getWidth() / tw : model.getWidth();

            int index = y * width + x;

            if(index < model.size()) return index;
        }

        return -1;
    }

}
