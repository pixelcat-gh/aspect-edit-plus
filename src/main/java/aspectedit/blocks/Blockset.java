
package aspectedit.blocks;

import aspectedit.palette.Palette;
import aspectedit.resources.AbstractResource;
import aspectedit.tiles.ITile;
import aspectedit.tiles.Tileset;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 */
public class Blockset extends AbstractResource {

    public static final Blockset EMPTY_BLOCKSET = new Blockset();

    private Tileset tileset = Tileset.EMPTY_TILESET;
    private ArrayList<Block> blocks;
    private Mapping[] mappings;

    private String asmFileName;
    private int tileOffset;

    private Palette fgPalette = Palette.BLANK_PALETTE;
    private Palette bgPalette = Palette.BLANK_PALETTE;


    /**
     * Construct a new Blockset.
     */
    public Blockset() {
        blocks = new ArrayList<Block>();

        mappings = new Mapping[256];

        for (int i = 0; i < mappings.length; i++) {
            mappings[i] = new Mapping(i, 0);

        }
    }


    public Palette getBgPalette() {
        return bgPalette;
    }

    public void setBgPalette(Palette bgPalette) {
        if(this.bgPalette != bgPalette) {
            this.bgPalette = bgPalette;
            tileset.setPalette(bgPalette);

            for (Block b : blocks) {
                if (b != null) {
                    b.setBgPalette(bgPalette);
                }
            }
            
            setModified(true);
        }
    }

    public Palette getFgPalette() {
        return fgPalette;
    }

    public void setFgPalette(Palette fgPalette) {
        if(this.fgPalette != fgPalette) {
            this.fgPalette = fgPalette;
            for (Block b : blocks) {
                if (b != null) {
                    b.setFgPalette(fgPalette);
                }
            }
            
            setModified(true);
        }
    }

    public String getAsmFileName() {
        return asmFileName;
    }

    public void setAsmFileName(String asmFileName) {
        this.asmFileName = asmFileName;
    }

    public int getTileOffset() {
        return tileOffset;
    }

    
    public void setTileOffset(int tileOffset) {
        if(this.tileOffset != tileOffset) {
            this.tileOffset = tileOffset;
            for (Block b : blocks) {
                b.setTileOffset(tileOffset);
            }

            setModified(true);
        }
    }


    public Tileset getTileset() {
        return tileset;
    }


    public void setTileset(Tileset tileset) {
        this.tileset = tileset;

        //apply the tileset to each block in the blockset
        for (Block b : blocks) {
            b.setTileset(tileset);
        }
    }


    public int size() {
        return blocks.size();
    }

    //<editor-fold desc="Mapping Accessor/Mutators">
    /**
     * Get the block number for a mapping index.
     *
     * @param index The mapping index.
     * @return The block's index within the blockset or -1 if the
     * block is invalid.
     */
    public int getBlockIndexForMapping(int index) {
        if (index < 0 || index > 255) {
            throw new IllegalArgumentException("Mapping index must be between 0 and 255.");
        }

        return mappings[index].getBlockIndex();
    }

    /**
     * Set the block number to be used for a mapping index.
     *
     * @param index The mapping index.
     * @param mapping The block number.
     */
    public void setBlockIndexForMapping(int mappingIndex, int blockIndex) {
        if (mappingIndex < 0 || mappingIndex > 255) {
            throw new IllegalArgumentException("Mapping index must be between 0 and 255.");
        }

        //Set the block index for the specified mapping
        mappings[mappingIndex].setBlockIndex(blockIndex);
    }

    /**
     * Get the {@link Mapping} at the specified index.
     * @param index The index.
     * @return The mapping.
     */
    public Mapping getMapping(int index) {
        return mappings[index];
    }

    /**
     * Get the {@link Block} for the specified mapping index.
     * @param index The mapping index.
     * @return The Block.
     */
    public Block getBlockForMapping(int index) {
        // if the blockset is empty return the empty block.
        if (blocks.size() == 0) {
            return Block.EMPTY_BLOCK;
        }

        // make sure that the index is within the bounds
        if (index < 0 || index > 255) {
            throw new IllegalArgumentException("Mapping index must be between 0 and 255.");
        }


        return mappings[index].getBlock();
    }

    //</editor-fold>


    //<editor-fold desc="Block Accessor/Mutators">
    private void setBlockProperties(Block block) {
        block.setTileOffset(tileOffset);
        block.setTileset(tileset);
        block.setFgPalette(fgPalette);
        block.setBgPalette(bgPalette);
    }

    public Block getBlockAt(int index) {
        return blocks.get(index);
    }

    public void addBlock(Block block) {
        blocks.add(block);
        setBlockProperties(block);
    }

    public void setBlockAt(int index, Block block) {
        blocks.set(index, block);
        setBlockProperties(block);
    }

    public void insertBlock(int index, Block block) {
        blocks.add(index, block);
        setBlockProperties(block);
    }

    public int indexOf(Block block) {
        return blocks.indexOf(block);
    }

    public void removeBlock(Block block) {
        int blockIndex = indexOf(block);

        //reset any mappings that point to this block to 0
        for (int i = 0; i < mappings.length; i++) {
            if (mappings[i].getBlockIndex() == blockIndex) {
                mappings[i].setBlockIndex(0);
            }
        }

        blocks.remove(block);
    }

    //</editor-fold>
    
    /**
     * Represents a mapping between a {@link Block} in the {@link Blockset} and
     * one of the 256 mapping slots.
     */
    public class Mapping implements ITile {

        private int mappingIndex;
        private int blockIndex = 0;

        Mapping(int mappingIndex, int blockIndex) {
            this.mappingIndex = mappingIndex;
            this.blockIndex = blockIndex;
        }

        /**
         * Get the index of the block within the blockset.
         *
         * @return The block's index.
         */
        public int getBlockIndex() {
            return blockIndex;
        }

        /**
         * Set the index of the block within the blockset.
         *
         * @param blockIndex The block's index.
         */
        void setBlockIndex(int blockIndex) {
            this.blockIndex = blockIndex;
        }

        /**
         * Get the mapping's index (slot number).
         *
         * @return The index.
         */
        public int getMappingIndex() {
            return mappingIndex;
        }

        /**
         * Set the mapping's index (between 0 and 255).
         *
         * @param mappingIndex The mapping's index.
         */
        void setMappingIndex(int mappingIndex) {
            assert mappingIndex >= 0 && mappingIndex < 255;

            this.mappingIndex = mappingIndex;
        }

        /**
         * Get the block for this mapping.
         * @return The block 
         */
        public Block getBlock() {
            if (blocks.size() == 0) {
                return Block.EMPTY_BLOCK;
            }

            if (blockIndex >= 0 && blockIndex < blocks.size()) {
                return blocks.get(blockIndex);
            } else {
                return Block.EMPTY_BLOCK;
            }
        }

        @Override
        public Mapping clone() {
            return new Mapping(mappingIndex, blockIndex);
        }

        //<editor-fold defaultstate="collapsed" desc="ITile implementation">
        @Override
        public int getWidth() {
            return Block.EMPTY_BLOCK.getWidth();
        }

        @Override
        public int getHeight() {
            return Block.EMPTY_BLOCK.getHeight();
        }

        @Override
        public void setPixel(int value, int x, int y) {
            if (blockIndex >= 0 && blockIndex < blocks.size()) {
                blocks.get(blockIndex).setPixel(value, x, y);
            }
        }

        @Override
        public int getPixel(int x, int y) {
            if (blocks.size() > 0 && blockIndex >= 0 && blockIndex < blocks.size()) {
                return blocks.get(blockIndex).getPixel(x, y);
            }

            return Block.EMPTY_BLOCK.getPixel(x, y);
        }

        @Override
        public byte[][] getBitplanes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBitplanes(byte[][] bitplanes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BufferedImage getImage() {
            if (blockIndex >= 0 && blockIndex < blocks.size()) {
                return blocks.get(blockIndex).getImage();
            }

            return Block.EMPTY_BLOCK.getImage();
        }

        //</editor-fold>
    }
}
