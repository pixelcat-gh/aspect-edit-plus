package aspectedit.blocks;

import aspectedit.palette.Palette;
import aspectedit.tiles.ITile;
import aspectedit.tiles.Tile;
import aspectedit.tiles.Tileset;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * The Block class represents a 4x4 grid of 8-pixel square {@link Tile}s. Blocks
 * are used in conjunction with mappings to construct level layouts.
 *
 * @author mark
 */
public class Block implements ITile {

    // bitmasks for tile flags
    public static final int HORIZONTAL_FLIP_BIT = 0x200;
    public static final int VERTICAL_FLIP_BIT = 0x400;
    public static final int PALETTE_SELECT_BIT = 0x800;
    public static final int PRIORITY_BIT = 0x1000;

    /** A static instance of an empty block to aid in null-safety */
    public static final Block EMPTY_BLOCK = new Block();

    /** Array of elements in the block */
    private int[] elements;

    /** Reference to the tileset to use to render this block */
    private Tileset tileset = Tileset.EMPTY_TILESET;

    /** Base index value to add to tile index */
    private int tileOffset;

    /** Image variables */
    private BufferedImage image;
    private WritableRaster raster;
    private boolean dirty = true;

    /** The palettes to use when rendering this block */
    private Palette fgPalette = Palette.BLANK_PALETTE;
    private Palette bgPalette = Palette.BLANK_PALETTE;


    /**
     * Construct a new block
     */
    @SuppressWarnings("unchecked")
    public Block() {
        //create the buffer image
        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        
        //keep a reference to the raster
        raster = image.getRaster();

        //generate the array of elements and set each of them to -1 (invalid)
        elements = new int[16];
        for (int i = 0; i < elements.length; i++) {
			//use the hi-bit of the word as a flag that represents whether the
			//block has been set
            elements[i] = 0x8000;

        }
    }


    /**
     * Forces the block to redraw its buffer image.
     */
    public void redraw() {
 
       for(int y=0; y<4; y++) {
           for(int x=0; x<4; x++) {
               drawTileAtPoint(x, y, x * 8, y * 8);
           }
       }

        dirty = false;
    }


    /**
     * Draws the tile at index (tileX, tileY) at the coordinate (x,y).
     *
     * @param tileX X-index of the tile within the mapping.
     * @param tileY Y-index of the tile within the mapping
     * @param x X-coordinate to start drawing the tile from.
     * @param y Y-coordinate to start drawing the tile from.
     */
    private void drawTileAtPoint(int tileX, int tileY, int x, int y) {
        // which tile?
        int tileIndex = getTileIndex(tileX, tileY) - tileOffset;

        // get the tile from the tileset
        Tile tile = null;
        if( (tileIndex & 0x8000) != 0 || tileset == null || tileIndex >= tileset.size())
            //the tile index is invalid within the current tileset
            tile = Tile.EMPTY_TILE;

        else
            tile = tileset.getTile(tileIndex);


        // does the tile need mirroring on any axis?
        boolean flipX = isFlipHorizontal(tileX, tileY);
        boolean flipY = isFlipVertical(tileX, tileY);

        // which palette?
        Palette p = isUseSpritePalette(tileX, tileY) ? fgPalette : bgPalette;

        // set the elements in the WritableRaster
        for(int h=0; h<8; h++) {
            for(int w=0; w<8; w++) {
                
                //get the pixel data (transforming as necessary)
                int pixel = tile.getPixel(flipX ? 7-w : w, flipY ? 7-h : h);
                Color colour = p.getJavaColour(pixel);

                //set the pixel on the buffer image
                raster.setPixel(x + w, y + h, new int[] {
                    colour.getRed(), colour.getGreen(), colour.getBlue()
                });
            }
        }

    }


    public BufferedImage getSubTile(int x, int y) {
        if(x < 0 || x > 3) {
            throw new IllegalArgumentException(
                    String.format("Tile index out of bounds: %d", x));
        }

        if(y < 0 || y > 3) {
            throw new IllegalArgumentException(
                    String.format("Tile index out of bounds: %d", y));
        }

        
        //call getImage() to redraw if necessary
        getImage();

        return image.getSubimage(x * 8, y * 8, 8, 8);
    }


    /**
     * Get this block's buffer image.
     * @return The buffer image.
     */
    @Override
    public BufferedImage getImage() {
        //lazily render the image
        if(dirty) redraw();

        return image;
    }


    /**
     * Background palette accessor.
     * @return The background palette.
     */
    public Palette getBgPalette() {
        return bgPalette;
    }

    /**
     * Background palette mutator.
     * @param bgPalette The new background palette.
     */
    public void setBgPalette(Palette bgPalette) {
        this.bgPalette = bgPalette;

        dirty = true;   // flag for a lazy redraw
    }


    /**
     * Foreground (sprite) palette accessor.
     * @return The foreground palette.
     */
    public Palette getFgPalette() {
        return fgPalette;
    }

    /**
     * Foreground (sprite) palette mutator.
     * @param fgPalette The new foreground palette.
     */
    public void setFgPalette(Palette fgPalette) {
        this.fgPalette = fgPalette;

        dirty = true;   // flag for a lazy redraw
    }


    /**
     * Tile offset accessor.
     * @return The offset value.
     */
    public int getTileOffset() {
        return tileOffset;
    }

    /**
     * Tile offset mutator.
     * @param tileOffset The new tile offset value.
     */
    public void setTileOffset(int tileOffset) {
        this.tileOffset = tileOffset;

        dirty = true;   // flag for a lazy redraw
    }


    /**
     * Tileset accessor.
     * @return The tileset.
     */
    public Tileset getTileset() {
        return tileset;
    }

    /**
     * Tileset mutator.
     * @param tileset The new tileset.
     */
    public void setTileset(Tileset tileset) {
        this.tileset = tileset;

        dirty = true;   // flag for a lazy redraw
    }


    //<editor-fold defaultstate="collapsed" desc="ITile Implementation">
    @Override
    public byte[][] getBitplanes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public int getPixel(int x, int y) {
        return 0;
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public void setBitplanes(byte[][] bitplanes) {

    }

    @Override
    public void setPixel(int value, int x, int y) {

    }
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="Element Accessor/Mutators">
    public int getElementAt(int x, int y) {
        int value = elements[y * 4 + x];
        if(value < 0) {
            return 0;
        } else {
            return elements[y * 4 + x] & 0x7FFF;
        }
    }

    public void setElementAt(int value, int x, int y) {
        elements[y * 4 + x] = value;
    }


    public int getTileIndex(int x, int y) {
        //return lower 9 bits of element value
        return elements[y * 4 + x] & 0x1FF;
    }

    public void setTileIndex(int index, int x, int y) {
        //set the lower 9 bits of the block value to (index + tileOffset)
		//and, at the same time, clear the MSB flag that we use to determine
		//if an element has been set
        elements[y * 4 + x] = (elements[y * 4 + x]  & 0xFE00) | (index + tileOffset & 0x1FF);

        dirty = true;
    }


    public boolean isFlipHorizontal(int x, int y) {
        int blockValue = elements[y * 4 + x];
        return (blockValue & HORIZONTAL_FLIP_BIT) != 0;
    }

    public void setFlipHorizontal(boolean value, int x, int y) {
        if (!value) {
            elements[y * 4 + x] &= ~HORIZONTAL_FLIP_BIT;
        } else {
            elements[y * 4 + x] |= HORIZONTAL_FLIP_BIT;
        }

        dirty = true;
    }

    public boolean isFlipVertical(int x, int y) {
        return (elements[y * 4 + x] & VERTICAL_FLIP_BIT) != 0;
    }

    public void setFlipVertical(boolean value, int x, int y) {
        if (!value) {
            elements[y * 4 + x] &= ~VERTICAL_FLIP_BIT;
        } else {
            elements[y * 4 + x] |= VERTICAL_FLIP_BIT;
        }

        dirty = true;
    }

    public boolean isUseSpritePalette(int x, int y) {
        return (elements[y * 4 + x] & PALETTE_SELECT_BIT) != 0;
    }

    public void setUseSpritePalette(boolean value, int x, int y) {
        if (!value) {
            elements[y * 4 + x] &= ~PALETTE_SELECT_BIT;
        } else {
            elements[y * 4 + x] |= PALETTE_SELECT_BIT;
        }

        dirty = true;
    }

    public boolean isHighPriority(int x, int y) {
        return (elements[y * 4 + x] & PRIORITY_BIT) != 0;
    }

    public void setHighPriority(boolean value, int x, int y) {
        if (!value) {
            elements[y * 4 + x] &= ~PRIORITY_BIT;
        } else {
            elements[y * 4 + x] |= PRIORITY_BIT;
        }
    }

    //</editor-fold>

}
