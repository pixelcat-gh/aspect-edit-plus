
package aspectedit.blocks;

/**
 *
 * @author mark
 */
public class BlocksetTileSource {

    public static final Block EMPTY_BLOCK = new Block();

    public static final int DISPLAY_MODE_MAPPINGS = 1;
    public static final int DISPLAY_MODE_BLOCKSET = 2;
    private int displayMode = DISPLAY_MODE_BLOCKSET;

    private Blockset blockset;

    public BlocksetTileSource() {
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        this.blockset = blockset;
    }

    //<editor-fold desc="MutableTileSource Implementation">
    public void setTile(int index, Block tile) {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                blockset.setBlockAt(index, tile);
            } else if(displayMode == DISPLAY_MODE_MAPPINGS) {
                blockset.setBlockIndexForMapping(index, blockset.indexOf(tile));
            }
        }
    }

    public void removeTile(Block tile) {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                blockset.removeBlock(tile);
                
            } else if(displayMode == DISPLAY_MODE_MAPPINGS) {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void addTile(Block tile) {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                blockset.addBlock(tile);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void insertTile(int index, Block tile) {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                blockset.insertBlock(index, tile);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public int size() {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                return blockset.size();

            } else if(displayMode == DISPLAY_MODE_MAPPINGS) {
                return 256;
            }
        }

        return 0;
    }

    public int getTileWidth() {
        return 32;
    }

    public int getTileHeight() {
        return 32;
    }

    public Block getTile(int index) {
        if(blockset != null) {
            if(displayMode == DISPLAY_MODE_BLOCKSET) {
                return blockset.getBlockAt(index);

            } else if(displayMode == DISPLAY_MODE_MAPPINGS) {
                return blockset.getBlockForMapping(index);
            }
        }

        return EMPTY_BLOCK;
    }

    //</editor-fold>
}
