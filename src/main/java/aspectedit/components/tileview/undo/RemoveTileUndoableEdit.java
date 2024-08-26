
package aspectedit.components.tileview.undo;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 */
public class RemoveTileUndoableEdit extends AbstractUndoableEdit {

    private TileViewModel model;
    private ITile tile;

    public RemoveTileUndoableEdit(TileViewModel model, ITile tile) {
        this.model = model;
        this.tile = tile;
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
        return "Remove Tile";
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        model.removeTile(tile);
    }

    @Override
    public void undo() throws CannotUndoException {
        model.addTile(tile);
    }


}
