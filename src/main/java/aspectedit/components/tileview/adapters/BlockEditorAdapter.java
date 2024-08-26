
package aspectedit.components.tileview.adapters;

import aspectedit.blocks.Block;
import aspectedit.components.tileview.TileViewModel;
import aspectedit.tiles.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author mark
 */
public class BlockEditorAdapter
        extends TileViewModelSupport
        implements TileViewModel<Tile> {

    private Block block = Block.EMPTY_BLOCK;
    private BlockElement[] elements;


    public BlockEditorAdapter() {
        elements = new BlockElement[16];

        for (int i = 0; i < elements.length; i++) {
            elements[i] = new BlockElement(i);
        }
    }


    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        if(block == null)
            throw new IllegalArgumentException("Block cannot be null.");
        
        this.block = block;
        fireStructureChanged();
    }

    @Override
    public int getWidth() {
        return 4;
    }

    @Override
    public int getHeight() {
        return 4;
    }

    @Override
    public int size() {
        return 16;
    }

    @Override
    public int getTileWidth() {
        return 8;
    }

    @Override
    public int getTileHeight() {
        return 8;
    }

    @Override
    public BlockElement getTile(int index) {
        return elements[index];
    }

    @Override
    public void setTile(int index, ITile tile) {
        if(block == Block.EMPTY_BLOCK || block.getTileset() == null) return;
		
		if(tile instanceof Tile) {
			int tileIndex = block.getTileset().indexOf( (Tile) tile);
			
			if(tileIndex >= 0) {
				final Tile oldTile = getTile(index);

				block.setTileIndex(tileIndex, index % 4, index / 4);

				fireTileChanged(index, oldTile, getTile(index));
			}
		}
    }


    @Override
    public int indexOf(Tile tile) {
        if(tile instanceof BlockElement) {
            return ((BlockElement) tile).index;
        }

        return -1;
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
    public void removeTile(Tile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addTile(Tile tile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertTile(int index, Tile tile) {
        throw new UnsupportedOperationException();
    }



    protected class BlockElement extends Tile {
        private int index;

        BlockElement(int index) {
            assert index >= 0 && index < 16;
            
            this.index = index;
        }


        @Override
        public final void setPixel(int value, int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final int getPixel(int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final byte[][] getBitplanes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void setBitplanes(byte[][] bitplanes) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BufferedImage getImage() {
            return block.getSubTile(index % 4, index / 4);
        }

    }

}
