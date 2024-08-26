/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aspectedit.frames.action;

import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewModel;
import aspectedit.images.IconManager;
import aspectedit.tiles.ITile;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author mark
 */
public class MoveTileLeftAction extends AbstractAction {

    private TileView tileView;

    public MoveTileLeftAction() {
        super("Move Tile Left", IconManager.getIcon(IconManager.ARROW_LEFT));
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        final boolean oldEnabled = isEnabled();

        this.tileView = tileView;

        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled() && tileView != null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileView == null) return;

        int index = tileView.getSelectedTileIndex();
        TileViewModel model = tileView.getModel();

        if(model.isRemoveSupported() && model.isAddSupported()) {
            if(index > 0) {
                ITile tile = tileView.removeTile(index);
                tileView.insertTile(tile, index-1);
                tileView.setSelectedTileIndex(index-1);
            }
        }
    }
}
