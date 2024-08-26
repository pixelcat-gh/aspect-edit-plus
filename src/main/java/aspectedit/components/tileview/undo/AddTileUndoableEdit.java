
package aspectedit.components.tileview.undo;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 */
public class AddTileUndoableEdit extends AbstractUndoableEdit {

    private TileViewModel model;
    private ITile tile;

    public AddTileUndoableEdit(TileViewModel model, ITile tile) {
        this.tile = tile;
        this.model = model;
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
    public boolean isSignificant() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        model.addTile(tile);
    }

    @Override
    public void undo() throws CannotUndoException {
        model.removeTile(tile);
    }

}
