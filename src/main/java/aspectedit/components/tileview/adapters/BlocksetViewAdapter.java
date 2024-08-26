
package aspectedit.components.tileview.adapters;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.ITile;

/**
 * An adapter class to allow a {@link Blockset} to be used
 * as a model for a TileView.
 * 
 * @author mark
 */
public class BlocksetViewAdapter
        extends TileViewModelSupport
        implements TileViewModel<Block> {


    private Blockset blockset = Blockset.EMPTY_BLOCKSET;


    public BlocksetViewAdapter() {
    }

    /**
     * Get the Blockset to use as a data model.
     *
     * @return The blockset.
     */
    public Blockset getBlockset() {
        return blockset;
    }

    /**
     * Set the Blockset to use as a data model.
     * 
     * @param blockset The blockset.
     */
    public void setBlockset(Blockset blockset) {
        if(blockset == null) {
            throw new IllegalArgumentException("Blockset cannot be null.");
        }

        this.blockset = blockset;

        fireStructureChanged();
        // TODO: Should this fire a STRUCTURE_CHANGED event?
    }


    /**
     * Get the index of a block within the blockset.
     * @param block The block.
     * @return The index. Will return -1 if the specified block
     * is not part of the current blockset.
     */
    @Override
    public int indexOf(Block block) {
        return blockset.indexOf(block);
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
        return blockset.size();
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
    public Block getTile(int index) {
        if (index == -1 || index >= blockset.size()) {
            return Block.EMPTY_BLOCK;
        }

        return blockset.getBlockAt(index);
    }


    @Override
    public void setTile(int index, ITile tile) {
		
		if(tile instanceof Block) {
			Block block = (Block) tile;

			Block oldBlock = blockset.getBlockAt(index);

			blockset.setBlockAt(index, block);

			fireTileChanged(index, oldBlock, block);
		}
    }


    @Override
    public boolean isAddSupported() {
        return true;
    }


    @Override
    public boolean isRemoveSupported() {
        return true;
    }


    @Override
    public void removeTile(Block block) {
        if(block == null) throw new IllegalArgumentException("Block cannot be null.");

        int index = indexOf(block);
        // remove the block...
        blockset.removeBlock(block);
        // ...and fire an event
        fireTileRemoved(index, block);
    }


    @Override
    public void addTile(Block block) {
        if(block == null)
            throw new IllegalArgumentException("Block cannot be null.");
        
        // Bail out if the block is already part of the blockset.
        if(blockset.indexOf(block) != -1) return;

        // add the block...
        blockset.addBlock(block);
        // ... and fire an event.
        fireTileAdded(indexOf(block), block);
    }


    @Override
    public void insertTile(int index, Block block) {
        if(block == null) throw new IllegalArgumentException("Block cannot be null.");

        blockset.insertBlock(index, block);

        // notify any listeners that the tile was inserted
        fireTileAdded(index, block);
        // structure will have changed so fire the event.
        fireStructureChanged();
    }
    
}
