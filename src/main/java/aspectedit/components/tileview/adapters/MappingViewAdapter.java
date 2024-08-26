
package aspectedit.components.tileview.adapters;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import aspectedit.blocks.Blockset.Mapping;
import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;

/**
 * An adapter class to allow a {@link Blockset.Mapping} to
 * be used as a data model for a TileView.
 */
public class MappingViewAdapter 
        extends TileViewModelSupport
        implements TileViewModel<Blockset.Mapping> {


    private Blockset blockset;


    /**
     * Construct a MappingViewAdapter using a default
     * empty Blockset.
     */
    public MappingViewAdapter() {
        this(Blockset.EMPTY_BLOCKSET);
    }

    /**
     * Construct a MappingViewAdapter using the specified Blockset.
     *
     * @param blockset The blockset.
     */
    public MappingViewAdapter(Blockset blockset) {
        this.blockset = blockset;
    }


    /**
     * Get the blockset to be used as a data model.
     * @return The blockest.
     */
    public Blockset getBlockset() {
        return blockset;
    }

    /**
     * Set the blockset to be used as a data model.
     *
     * @param blockset The blockset.
     */
    public void setBlockset(Blockset blockset) {
        this.blockset = blockset;

        fireStructureChanged();
        // TODO: should this fire a STRUCTURE_CHANGED event?
    }

    @Override
    public int getWidth() {
        return -1;
    }

    @Override
    public int getHeight() {
        return -1;
    }

    @Override
    public int size() {
        return 256;
    }

    @Override
    public int getTileWidth() {
        return Block.EMPTY_BLOCK.getWidth();
    }

    @Override
    public int getTileHeight() {
        return Block.EMPTY_BLOCK.getHeight();
    }

    @Override
    public Mapping getTile(int index) {
        return blockset.getMapping(index);
    }


	//
	// TODO: how does this effect the UndoManager support?
	//
    @Override
    public void setTile(int index, ITile tile) {
		
		// keep hold of the previous mapping
		final Mapping oldMapping = blockset.getMapping(index);
			
		if(tile instanceof Mapping) {
			Mapping mapping = (Mapping) tile;
			
			blockset.setBlockIndexForMapping(index, mapping.getBlockIndex());
			// fire the an event to any listeners
			fireTileChanged(index, oldMapping, mapping);
			
		} else if(tile instanceof Block) {
			
			Block block = (Block) tile;
			
			blockset.setBlockIndexForMapping(index, blockset.indexOf(block));
			
			fireTileChanged(index, oldMapping, blockset.getMapping(index));
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
    public int indexOf(Mapping tile) {
        return tile.getMappingIndex();
    }

    @Override
    public void removeTile(Mapping tile) {
        // mappings are immutable
        throw new UnsupportedOperationException("Cannot remove mapping.");
    }

    @Override
    public void addTile(Mapping tile) {
        // mappings are immutable
        throw new UnsupportedOperationException("Cannot add mapping.");
    }

    @Override
    public void insertTile(int index, Mapping tile) {
        // mapping are immutable
        throw new UnsupportedOperationException("Cannot insert mapping.");
    }

}
