
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
		
        int result = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to remove the selected tile?",
                AspectEdit.APP_NAME,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if(result == JOptionPane.YES_OPTION) {
            tileView.removeTile(
                    tileView.getSelectedTile());

			tileView.revalidate();
			tileView.repaint();
        }
    }

}
