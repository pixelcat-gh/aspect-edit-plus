
package aspectedit.components.tileview.layout;

import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewModel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import javax.swing.JViewport;

/**
 *
 */
public class TileViewFlowLayout implements TileViewLayout {


    public TileViewFlowLayout() {
    }


    @Override
    public Dimension calculatePreferredSize(TileView view, TileViewModel model, Dimension preferredSize) {

        //should the layout manager calculate the size?
        if(preferredSize == null || !view.isPreferredSizeSet()) {
            preferredSize = view.getMinimumSize();
            
            //get the parent container
            Container c = view;
            do {
                if(c.getParent() == null) break;

                c = c.getParent();
            } while(c instanceof JViewport);

            if(c != null) {
                //calculate the preferred width based on the width of the parent
                Insets insets = c.getInsets();
                preferredSize.width = Math.max(c.getWidth() - insets.left - insets.right, preferredSize.width);
            }


            //calculate the height based on the width of the view
            if(model.size() > 0) {
                int tw = (int)Math.max((model.getTileWidth() * view.getZoomFactor()), 1);
                int th = (int)Math.max((model.getTileHeight() * view.getZoomFactor()), 1);

                int widthInTiles = Math.max(preferredSize.width / tw, 1);
                int heightInTiles = Math.max((int)Math.round(model.size() / widthInTiles + 0.5), 1);

                preferredSize.height = Math.max(th, heightInTiles * th);
            }
            
        }
        
        return preferredSize;
    }


    @Override
    public int getTileIndexFromPoint(TileView view, TileViewModel model, Point point) {
        int tw = (int)(model.getTileWidth() * view.getZoomFactor());
        int th = (int)(model.getTileHeight() * view.getZoomFactor());

        int widthInTiles = view.getSize().width / tw;

        //if point wasn't actually on a tile return -1.
        if(point.x > widthInTiles * tw) return -1;

        int x = point.x / tw;
        int y = point.y / th;

        int index = y * widthInTiles + x;

        return index < model.size() ? index : -1;
    }


    @Override
    public Point getCoordinateForTile(TileView view, TileViewModel model, int tileIndex) {
        Dimension size = view.getSize();
        float zoom = view.getZoomFactor();

        int tw = (int)(model.getTileWidth() * zoom);
        int th = (int)(model.getTileWidth() * zoom);

        int tx = size.width / tw;
        int x = (tileIndex % tx) * tw;
        int y = (tileIndex / tx) * th;

        return new Point(x, y);
    }

}
