
package aspectedit.util;

import aspectedit.palette.MasterPalette;
import aspectedit.palette.Palette;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class Quantiser {

    private Palette masterPalette = new MasterPalette();
    private Palette result = new Palette();

    public Quantiser() {
        
    }

    public Palette getResultingPalette() {
        return result;
    }
    
    public BufferedImage quantise(BufferedImage input) {

        //quantise to a level of 4bpp
        RGBCube cube = new RGBCube(4);

        WritableRaster inputRaster = input.getRaster();

        int[] samples = new int[3];

        // Create a cube from the image's pixels
        for(int y = 0; y < input.getHeight(); y++) {
            for(int x = 0; x < input.getWidth(); x++) {

                //convert the image to the mastersystem palette
                inputRaster.getPixel(x, y, samples);

                int sms = getSMSColour(samples[0], samples[1], samples[2]);

                Color c = masterPalette.getJavaColour(sms);

                samples[0] = c.getRed();
                samples[1] = c.getGreen();
                samples[2] = c.getBlue();

                inputRaster.setPixel(x, y, samples);

                cube.addColour(samples[0], samples[1], samples[2]);
            }
        }


        // Reduce the cube to, at most, 16 leaf nodes
        while(cube.getLeafCount() > 16) {
            RGBCube node = cube.getNextReducibleCube();
            node.reduce();
        }

        // Calculate the closest SMS colour to each colour in the cube.
        HashMap<Integer, Color> colourConversion = generatePalette(cube);

        // create the output image
        BufferedImage output = new BufferedImage(
                input.getWidth(),
                input.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        WritableRaster outputRaster = output.getRaster();

        // render the quantised image
        for(int y = 0; y < output.getHeight(); y++) {
            for(int x = 0; x < output.getWidth(); x++) {
                inputRaster.getPixel(x, y, samples);

                Color c = cube.getQuantisedColour(samples[0], samples[1], samples[2]);
                c = colourConversion.get(c.getRGB());
                
                outputRaster.setPixel(x, y, new int[]{c.getRed(),c.getGreen(),c.getBlue()});
            }
        }

        return output;
    }

    /**
     * Generates an SMS palette from the colours in a quantised colour cube.
     * @param cube The colour cube.
     * @return A hashmap that can be used to convert the colours in the cube
     * to the relevant SMS colour.
     */
    private HashMap<Integer, Color> generatePalette(RGBCube cube) {
        assert cube.getLeafCount() == 16;

        //get the colours from the cube
        List<Color> colours = cube.getColours();

        //build a hashmap that we can use to redraw the image
        //using the SMS palette
        HashMap<Integer, Color> colourConversion = new HashMap<Integer, Color>();

        //loop through each colour to find the closest SMS palette
        for(int i=0; i<colours.size(); i++) {
            int smsColour = getSMSColour(colours.get(i));

            result.setColour(i, smsColour);

            colourConversion.put(colours.get(i).getRGB(), result.getJavaColour(i));
        }

        return colourConversion;
    }

    /**
     * Get the closest SMS colour for the specified RGB colour.
     * @param colour The RGB colour.
     * @return The SMS colour byte.
     */
    private int getSMSColour(Color colour) {
        return getSMSColour(colour.getRed(), colour.getGreen(), colour.getBlue());
    }

    private int getSMSColour(int r, int g, int b) {
        int smsColour = 0;
        double optimalDistance = Double.MAX_VALUE;

        for(int i=0; i<masterPalette.size(); i++) {
            Color c = masterPalette.getJavaColour(i);

            // calculate the distance between the 2 colours
            int dr = c.getRed() - r;
            int dg = c.getGreen() - g;
            int db = c.getBlue() - b;

            double distance = Math.sqrt(dr * dr + dg * dg + db * db);

            // if the distance is less than the previous value
            // treat this colour as optimal
            if(distance < optimalDistance) {
                optimalDistance = distance;
                smsColour = masterPalette.getColour(i);
            }
        }

        return smsColour;
    }

}
