
package aspectedit.tiles;

import aspectedit.palette.Palette;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author mark
 */
public class Tile implements ITile {

    public static final Tile EMPTY_TILE = new Tile();
    
    protected byte[][] bitplanes;
    protected BufferedImage image;

    private boolean modified = false;

    private Palette palette = Palette.BLANK_PALETTE;

    public Tile() {
        bitplanes = new byte[8][4];
        image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
    }

    public void redraw() {
        for(int y=0; y<8; y++) {
            for(int x=0; x<8; x++) {
                Color colour = getPalette().getJavaColour(getPixel(x, y));

                image.setRGB(x, y, colour.getRGB());
            }
        }
    }

    public BufferedImage getImage(Palette palette) {
        if(palette == palette) {
            return image;
        }

        BufferedImage img = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);

        for(int y=0; y<8; y++) {
            for(int x=0; x<8; x++) {
                Color colour = palette.getJavaColour(getPixel(x, y));

                img.setRGB(x, y, colour.getRGB());
            }
        }

        return img;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        boolean requiresRedraw = this.palette != palette;

        this.palette = palette;
        if(requiresRedraw) redraw();
    }

    @Override
    public void setBitplanes(byte[][] bitplanes) {
        this.bitplanes = bitplanes;
        redraw();

        modified = true;
    }

    @Override
    public byte[][] getBitplanes() {
        return bitplanes;
    }
    
    @Override
    public int getPixel(int x, int y) {

        int value = ((bitplanes[y][0] >> 7-x) & 1
                | ((bitplanes[y][1] >> 7-x) & 1) << 1
                | ((bitplanes[y][2] >> 7-x) & 1) << 2
                | ((bitplanes[y][3] >> 7-x) & 1) << 3);

        return value;
    }

    @Override
    public void setPixel(int value, int x, int y) {
        bitplanes[y][0] = (byte)((bitplanes[y][0] & ~(1 << 7-x)) | ((value & 1) << 7-x));
        bitplanes[y][1] = (byte)((bitplanes[y][1] & ~(1 << 7-x)) | ((value>>1 & 1) << 7-x));
        bitplanes[y][2] = (byte)((bitplanes[y][2] & ~(1 << 7-x)) | ((value>>2 & 1) << 7-x));
        bitplanes[y][3] = (byte)((bitplanes[y][3] & ~(1 << 7-x)) | ((value>>3 & 1) << 7-x));

        Color c = palette.getJavaColour(value);
        
        image.setRGB(x, y, c.getRGB());
        
        modified = true;
    }

    @Override
    public int getWidth() {
        return 8;
    }

    @Override
    public int getHeight() {
        return 8;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }


}
