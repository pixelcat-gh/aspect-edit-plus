/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.palette;

import java.awt.Color;

/**
 *
 */
public class GGMasterPalette extends GGPalette {

    public GGMasterPalette() {
        super();

        colours = new int[4096];
        javaColours = new Color[4096];

        for(int i=0; i<64; i++) {
            colours[i] = i;

            javaColours[i] = new Color(
                    (i & 0xF) * GG_COLOUR_STEP,
                    (i >> 4 & 0xF) * GG_COLOUR_STEP,
                    (i >> 8 & 0xF) * GG_COLOUR_STEP);
        }
    }

    @Override
    public void setColour(int index, int colour) {

    }
}
