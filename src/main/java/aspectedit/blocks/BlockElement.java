
package aspectedit.blocks;

import aspectedit.palette.Palette;
import aspectedit.tiles.ITile;
import aspectedit.tiles.Tile;
import aspectedit.tiles.Tileset;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 *
 * @author mark
 */
public class BlockElement implements ITile {

    public static final BlockElement EMPTY_ELEMENT = new BlockElement();

    public static final int HORIZONTAL_FLIP_BIT = 0x200;
    public static final int VERTICAL_FLIP_BIT = 0x400;
    public static final int PALETTE_SELECT_BIT = 0x800;
    public static final int PRIORITY_BIT = 0x1000;

    private int tileOffset;
    private int blockValue;
    private Tileset tileset = Tileset.EMPTY_TILESET;

    private boolean redrawRequired = true;
    private BufferedImage image;
    private Graphics2D graphics;
    private AffineTransform tx = new AffineTransform();

    private Palette fgPalette = Palette.BLANK_PALETTE;
    private Palette bgPalette = Palette.BLANK_PALETTE;


    @SuppressWarnings("unchecked")
    public BlockElement() {
        image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
        graphics = (Graphics2D) image.getGraphics();
    }


    @Override
    public BufferedImage getImage() {
        if(redrawRequired) redraw();

        return image;
    }

    public void redraw() {
        Tile tile = getTile();

        BufferedImage tileImage = tile.getImage(
                isUseSpritePalette() ? fgPalette : bgPalette);

        tx.setToIdentity();

        tx.translate(
                isFlipHorizontal() ? 8 : 0,
                isFlipVertical() ? 8 : 0);

        tx.scale(
                isFlipHorizontal() ? -1.0 : 1.0,
                isFlipVertical() ? -1.0 : 1.0);

        graphics.setTransform(tx);
        graphics.drawImage(tileImage, 0, 0, 8, 8, null);

        redrawRequired = false;
    }

    public Palette getBgPalette() {
        return bgPalette;
    }

    public void setBgPalette(Palette bgPalette) {
        this.bgPalette = bgPalette;

        if(!isUseSpritePalette()) {
            redrawRequired = true;
        }
    }

    public Palette getFgPalette() {
        return fgPalette;
    }

    public void setFgPalette(Palette fgPalette) {
        this.fgPalette = fgPalette;

        if(isUseSpritePalette()) {
            redraw();
        }
    }

    public int getTileOffset() {
        return tileOffset;
    }

    public void setTileOffset(int tileOffset) {
        this.tileOffset = tileOffset;

        redrawRequired = true;
    }

    /**
     * Null-safe tile accessor.
     * @return
     */
    private Tile getTile() {
        int tileIndex = getTileIndex();

        if (tileset == null || tileIndex < 0 || tileIndex >= tileset.size()) {
            return Tile.EMPTY_TILE;
        }

        return tileset.getTile(tileIndex);
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        this.tileset = tileset;

        redrawRequired = true;
    }

    @Override
    public int getHeight() {
        return getTile().getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        return getTile().getPixel(x, y);
    }

    @Override
    public int getWidth() {
        return getTile().getWidth();
    }

    @Override
    public void setPixel(int value, int x, int y) {
        //getTile().setPixel(value, x, y);
    }

    public int getTileIndex() {
        return blockValue - tileOffset & 0x1FF;
    }

    public void setTileIndex(Tile tile) {
        setTileIndex(tileset.indexOf(tile));
    }

    public void setTileIndex(int value) {
        blockValue = (blockValue & 0xFE00) | (value + tileOffset & 0x1FF) ;
        redrawRequired = true;
    }

    public boolean isFlipHorizontal() {
        return (blockValue & HORIZONTAL_FLIP_BIT) != 0;
    }

    public void setFlipHorizontal(boolean value) {
        if (!value) {
            blockValue &= ~HORIZONTAL_FLIP_BIT;
        } else {
            blockValue |= HORIZONTAL_FLIP_BIT;
        }

        redrawRequired = true;
    }

    public boolean isFlipVertical() {
        return (blockValue & VERTICAL_FLIP_BIT) != 0;
    }

    public void setFlipVertical(boolean value) {
        if (!value) {
            blockValue &= ~VERTICAL_FLIP_BIT;
        } else {
            blockValue |= VERTICAL_FLIP_BIT;
        }

        redrawRequired = true;
    }

    public boolean isUseSpritePalette() {
        return (blockValue & PALETTE_SELECT_BIT) != 0;
    }

    public void setUseSpritePalette(boolean value) {
        if (!value) {
            blockValue &= ~PALETTE_SELECT_BIT;
        } else {
            blockValue |= PALETTE_SELECT_BIT;
        }

        redrawRequired = true;
    }

    public boolean isHighPriority() {
        return (blockValue & PRIORITY_BIT) != 0;
    }

    public void setHighPriority(boolean value) {
        if (!value) {
            blockValue &= ~PRIORITY_BIT;
        } else {
            blockValue |= PRIORITY_BIT;
        }
    }

    public void setBlockValue(int blockValue) {
        this.blockValue = blockValue;

        redrawRequired = true;
    }

    public int getBlockValue() {
        return blockValue;
    }

    @Override
    public byte[][] getBitplanes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBitplanes(byte[][] bitplanes) {
        throw new UnsupportedOperationException();
    }
}
