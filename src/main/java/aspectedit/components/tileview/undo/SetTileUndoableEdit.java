
package aspectedit.components.tileview.undo;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 */
public class SetTileUndoableEdit extends AbstractUndoableEdit {

    private TileViewModel model;
    private ITile oldTile;
    private ITile newTile;
    private int index;
    private boolean significant;

    public SetTileUndoableEdit(
            TileViewModel model,
            int index,
            ITile oldTile,
            ITile newTile,
            boolean significant) {

        this.model = model;
        this.index = index;
        this.oldTile = oldTile;
        this.newTile = newTile;
        this.significant = significant;
    }

    @Override
    public boolean canRedo() {
        return true;
    }

    @Override
    public boolean canUndo() {
        return true;
    }

    @Override
    public String getPresentationName() {
        return "Set Tile";
    }

    @Override
    public boolean isSignificant() {
        return significant;
    }

    @Override
    public void redo() throws CannotRedoException {
        model.setTile(index, newTile);
    }

    @Override
    public void undo() throws CannotUndoException {
        model.setTile(index, oldTile);
    }


}
