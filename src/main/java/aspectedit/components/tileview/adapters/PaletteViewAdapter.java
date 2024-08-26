
package aspectedit.components.tileview.adapters;

import aspectedit.components.tileview.TileViewModel;
import aspectedit.palette.GGPalette;
import aspectedit.palette.Palette;
import aspectedit.tiles.ITile;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * An adapter class to allow a {@link Palette} to be displayed
 * using a TileView component.
 */
public class PaletteViewAdapter
        extends TileViewModelSupport
        implements TileViewModel<ITile> {


    private static final int TILE_SIZE = 16;


    private Palette palette = Palette.BLANK_PALETTE;

    /** The tiles to be used to display the palette. */
    private PaletteTile[] tiles = new PaletteTile[Palette.BLANK_PALETTE.size()];


    /**
     * Construct
     */
    public PaletteViewAdapter() {
        //Initialise the tile array.
        for(int i=0; i<tiles.length; i++) {
            tiles[i] = new PaletteTile(i);
        }
    }

    /**
     * Get the Palette.
     * @return The Palette.
     */
    public Palette getPalette() {
        return palette;
    }

    /**
     * Set the Palette to be used by this adapter.
     * @param palette The palette.
     */
    public void setPalette(Palette palette) {
        if(palette == null)
            throw new IllegalArgumentException("Palette cannot be null.");

        this.palette = palette;


        tiles = new PaletteTile[palette.size()];

        for(int i=0; i<tiles.length; i++) {
            tiles[i] = new PaletteTile(i);
        }

        fireStructureChanged();
    }


    @Override
    public int getWidth() {
        return 8;
    }


    @Override
    public int getHeight() {
        return palette.size() / getWidth();
    }


    @Override
    public int size() {
        return palette.size();
    }


    @Override
    public int getTileWidth() {
        return TILE_SIZE;
    }


    @Override
    public int getTileHeight() {
        return TILE_SIZE;
    }


    @Override
    public ITile getTile(int index) {
        return tiles[index];
    }


    @Override
    public void setTile(int index, ITile tile) {
        if(tile instanceof PaletteTile) {
            int pixel = ((PaletteTile) tile).index;
            tiles[index].index = pixel;

            fireTileChanged(index, tiles[index], tile);
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
    public void removeTile(ITile tile) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void addTile(ITile tile) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void insertTile(int index, ITile tile) {
        throw new UnsupportedOperationException();
    }


    @Override
    public int indexOf(ITile tile ) {
        if( ! (tile instanceof PaletteTile) ) {
            throw new IllegalArgumentException("Tile must be an instance of PaletteTile");
        }

        return ((PaletteTile) tile).index;
    }

    /**
     * Get the colour for a given tile.
     *
     * @param tile The tile.
     * @return The colour value.
     */
    public int getColourForTile(ITile tile) {
        if( ! (tile instanceof PaletteTile) ) {
            throw new IllegalArgumentException("Tile must be an instance of PaletteTile");
        }

        // get the colour from the palette.
        return palette.getColour( ((PaletteTile) tile).index );
    }

    public void setColour(int index, int r, int g, int b) {

        if(palette instanceof GGPalette) {
            palette.setColour(index,
                    ((b & 0xF) << 8) | ((g & 0xF) << 4) | (r & 0xF));
        } else {
            palette.setColour(index,
                    ((b & 3) << 4) | ((g & 3) << 2) | (r & 3));
        }
        
        tiles[index].repaint();

        fireTileChanged(index, tiles[index], tiles[index]);
    }

    /**
     * A basic implementation of ITile that displays a
     * single block of colour.
     */
    private class PaletteTile implements ITile {

        private BufferedImage img;

        /** The index of the colour within the palette. */
        private int index;

        public PaletteTile(int index) {
            img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
            this.index = index;
            repaint();
        }

        public void repaint() {
            Color colour = palette.getJavaColour(index);

            Graphics g = img.getGraphics();
            g.setColor(colour);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.dispose();
        }

        @Override
        public int getWidth() {
            return TILE_SIZE;
        }

        @Override
        public int getHeight() {
            return TILE_SIZE;
        }

        @Override
        public void setPixel(int value, int x, int y) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPixel(int x, int y) {
            return index;
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
            return img;
        }

    }

}
