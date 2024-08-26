
package aspectedit.components.tileview.undo;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 */
public class InsertTileUndoableEdit extends AbstractUndoableEdit {

    private TileViewModel model;
    private ITile tile;
    private int index;

    public InsertTileUndoableEdit(TileViewModel model, ITile tile, int index) {
        this.model = model;
        this.tile = tile;
        this.index = index;
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
        return "Insert Tile";
    }

    @Override
    public boolean isSignificant() {
        return true;
    }

    @Override
    public void redo() throws CannotRedoException {
        model.insertTile(index, tile);
    }

    @Override
    public void undo() throws CannotUndoException {
        model.removeTile(model.getTile(index));
    }


}
