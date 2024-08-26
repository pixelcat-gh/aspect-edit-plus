
package aspectedit.components.tileview.adapters;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.TileViewModel;
import aspectedit.level.Level;
import aspectedit.tiles.ITile;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * An adapter class to allow a Level instance to be used
 * as a data model for a {@link TileView}.
 */
public class LevelViewAdapter
        extends TileViewModelSupport
        implements TileViewModel<Blockset.Mapping> {

    private Level level;
    private Blockset blockset = Blockset.EMPTY_BLOCKSET;
    private PropertyChangeListener levelListener;

    public LevelViewAdapter() {

        // add a property listener that will fire a Structure Changed event
        // when the level changes size
        levelListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fireStructureChanged();
            }
        };
    }
    

    /**
     * Get the blockset to be used to display the level data.
     * 
     * @return The blockset.
     */
    public Blockset getBlockset() {
        return blockset;
    }

    /**
     * Set the blockset to be used to display the level data.
     *
     * @param blockset The blockset.
     */
    public void setBlockset(Blockset blockset) {
        this.blockset = blockset;
    }

    /**
     * Get the level (data model) for this adapter.
     * @return The level.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Set the data model for this adapter.
     * @param level The data model.
     */
    public void setLevel(Level level) {

        if(this.level != null) {
            this.level.removePropertyChangeListener(levelListener);
        }

        this.level = level;

        level.addPropertyChangeListener(levelListener);
        
        fireStructureChanged();
    }


    @Override
    public int getWidth() {
        if(level == null) return 0;

        return level.getWidth();
    }

    @Override
    public int getHeight() {
        if(level == null) return 0;

        return level.getHeight();
    }

    @Override
    public int size() {
        if(level == null) return 0;

        return level.getWidth() * level.getHeight();
    }


    @Override
    public int getTileWidth() {
        return Block.EMPTY_BLOCK.getWidth();
    }

    @Override
    public int getTileHeight() {
        return Block.EMPTY_BLOCK.getHeight();
    }

    /**
     * Get the {@link Blockset.Mapping} at the specified position
     * within the level data.
     *
     * @param index The position within the level data.
     * @return The Mapping.
     */
    @Override
    public Blockset.Mapping getTile(int index) {

        //FIXME: This is not null-safe.
        
        int mappingValue = level.getMappingValue(
                index % level.getWidth(),
                index / level.getWidth());

        Blockset.Mapping mapping = blockset.getMapping(mappingValue);

        return mapping;
    }

    /**
     * Set the {@link Blockset.Mapping} to be used at the specified
     * position within the level data.
     *
     * @param index The position within the level data.
     * @param tile The Mapping block to use.
     */
    @Override
    public void setTile(int index, ITile tile) {
        if(index < 0) {
            throw new IllegalArgumentException("Index cannot be < 0.");
        }

		if(tile instanceof Blockset.Mapping) {
			if(level == null) {
				return;
			}

			final Blockset.Mapping oldMapping = getTile(index);

			Blockset.Mapping mapping = (Blockset.Mapping) tile;
			
			level.setMappingValue(
					index % level.getWidth(), // x
					index / level.getWidth(), // y
					mapping.getMappingIndex());  // mapping value

			fireTileChanged(index, oldMapping, mapping);
		}
    }

    @Override
    public boolean isAddSupported() {
        return false;
    }

    @Override
    public boolean isRemoveSupported() {
        return false;
    }

    @Override
    public void removeTile(Blockset.Mapping tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTile(Blockset.Mapping tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertTile(int index, Blockset.Mapping tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Blockset.Mapping tile) {
        // use the mapping index
        return tile.getMappingIndex();
    }

}
