
package aspectedit.palette;

import java.awt.Color;

/**
 *
 */
public class GGPalette extends Palette {

    public static final int GG_COLOUR_STEP = 256/15;

    public GGPalette() {
        super();
    }

    @Override
    public void setColour(int index, int colour) {
        colour &= 0xFFF;
        this.colours[index] = colour;

        this.javaColours[index] = new Color(
                (colour & 15) * GG_COLOUR_STEP,
                ((colour >> 4) & 15) * GG_COLOUR_STEP,
                ((colour >> 8) & 15) * GG_COLOUR_STEP
                );
    }


}
