
package aspectedit.palette;

import aspectedit.resources.AbstractResource;
import java.awt.Color;

/**
 *
 * @author mark
 */
public class Palette extends AbstractResource {

    public static final int COLOUR_STEP = 256/3;
    public static final Palette BLANK_PALETTE = new Palette();
    
    protected int[] colours;
    protected Color[] javaColours;

    public Palette() {
        colours = new int[16];
        javaColours = new Color[] {
            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
            Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK
        };
    }

    public int getColour(int index) {
        return colours[index];
    }

    public void setColour(int index, int colour) {
        colour &= 0x3F;
        if(this.colours[index] != colour) {
            this.colours[index] = colour;

            javaColours[index] = new Color(
                    (colour & 3) * COLOUR_STEP,
                    ((colour >> 2) & 3) * COLOUR_STEP,
                    ((colour >> 4) & 3) * COLOUR_STEP);
            
            setModified(true);
        }
    }

    public Color[] getAllJavaColours() {
        return javaColours;
    }
    
    public Color getJavaColour(int index) {
        return javaColours[index];
    }

    public int size() {
        return colours.length;
    }
}
