
package aspectedit.palette;

import java.awt.Color;

/**
 *
 * @author mark
 */
public class MasterPalette extends Palette {

    public MasterPalette() {
        super();
        
        colours = new int[64];
        javaColours = new Color[64];

        for(int i=0; i<64; i++) {
            colours[i] = i;

            javaColours[i] = new Color(
                    (i & 3) * COLOUR_STEP,
                    (i >> 2 & 3) * COLOUR_STEP,
                    (i >> 4 & 3) * COLOUR_STEP);
        }
    }

    @Override
    public void setColour(int index, int colour) {

    }

}
