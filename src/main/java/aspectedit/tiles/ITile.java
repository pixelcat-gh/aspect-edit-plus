
package aspectedit.tiles;

import java.awt.image.BufferedImage;

/**
 *
 * @author mark
 */
public interface ITile {

    public int getWidth();
    public int getHeight();

    public void setPixel(int value, int x, int y);
    public int getPixel(int x, int y);

    public byte[][] getBitplanes();
    public void setBitplanes(byte[][] bitplanes);

    public BufferedImage getImage();
    
}
