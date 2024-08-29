
package aspectedit.frames.action;

import aspectedit.components.tileview.TileView;
import aspectedit.frames.AspectEdit;
import aspectedit.images.IconManager;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 */
public class RemoveTileAction extends AbstractAction {

    private TileView tileView;

    public RemoveTileAction() {
        this("Remove Tile", IconManager.getIcon(IconManager.DELETE));
    }

    public RemoveTileAction(String name, Icon icon) {
        super(name, icon);
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        this.tileView = tileView;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileView == null || tileView.getSelectedTileIndex() == -1) return;

        tileView.removeTile(tileView.getSelectedTile());

        int selectedTileIndex = tileView.getSelectedTileIndex();

        tileView.revalidate();
        tileView.repaint();

        tileView.setSelectedTileIndex(selectedTileIndex);
    }

}
