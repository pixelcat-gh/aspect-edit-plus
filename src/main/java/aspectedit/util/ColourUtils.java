/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import aspectedit.palette.GGMasterPalette;
import aspectedit.palette.GGPalette;
import aspectedit.palette.MasterPalette;
import aspectedit.palette.Palette;
import java.awt.Color;

/**
 *
 */
public class ColourUtils {

    private static final MasterPalette smsPalette = new MasterPalette();
    private static final GGMasterPalette ggPalette = new GGMasterPalette();

    public static int getClosestSMSColour(Color javaColour) {
        return getClosestColour(smsPalette, javaColour);
    }

    public static int getClosestGGColour(Color javaColour) {
        return getClosestColour(ggPalette, javaColour);
    }

    /**
     * Convert a GG palette to an SMS palette.
     * @param gg The Game Gear palette.
     * @return A Master System palette.
     */
    public static Palette convertGGPaletteToSMS(GGPalette gg) {
        Palette p = new Palette();

        for(int i=0; i<gg.size(); i++) {
            p.setColour(i, getClosestSMSColour(gg.getJavaColour(i)));
        }

        return p;
    }

    /**
     * Convert from an SMS palette to a GG palette.
     * @param sms The Master System palette.
     * @return The Game Gear palette.
     */
    public static Palette convertSMSPaletteToGG(Palette sms) {
        GGPalette p = new GGPalette();

        for(int i=0; i<sms.size(); i++) {
            p.setColour(i, getClosestGGColour(sms.getJavaColour(i)));
        }

        return p;
    }

    /**
     * Find the nearest matching colour in a palette using an Euclidean
     * distance algorithm.
     * @param palette The palette containing the desired colours.
     * @param javaColour The colour to match.
     * @return 
     */
    private static int getClosestColour(Palette palette, Color javaColour) {

        double optimalDistance = Double.MAX_VALUE;
        int index = 0;

        // calculate optimal euclidean distance
        for(int i=0; i<palette.size(); i++) {
            Color c = palette.getJavaColour(i);

            double dr = c.getRed() - javaColour.getRed();
            double dg = c.getGreen() - javaColour.getGreen();
            double db = c.getBlue() - javaColour.getBlue();

            double distance = Math.sqrt(dr * dr + dg * dg + db * db);

            if(distance < optimalDistance) {
                optimalDistance = distance;
                index = i;
            }
        }

        return palette.getColour(index);
    }
}
