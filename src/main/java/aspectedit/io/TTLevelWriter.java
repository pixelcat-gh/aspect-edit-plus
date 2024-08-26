
package aspectedit.io;

import aspectedit.level.Level;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Writes an instance of Level to an OutputStream with a Sonic Triple
 * Trouble LZSS compression scheme. See http://info.sonicretro.org/SCHG:Sonic_Triple_Trouble
 * for details.
 * 
 * @author mark
 */
public class TTLevelWriter extends ResourceWriter<Level> {

    /** The size of the sliding window. */
    private static final int WINDOW = 4095;
    /** The maximum length of a single compressed pattern */
    private static final int MAX_LENGTH = 18;

    /**
     * Construct
     * @param out The OutputStream.
     */
    public TTLevelWriter(OutputStream out) {
        super(out);
    }


    @Override
    public void write(Level level) throws IOException {
        // copy the level data into an array
        int[] levelData = new int[level.getWidth() * level.getHeight()];

        for(int i=0; i<levelData.length; i++) {
            levelData[i] = level.getMappingValue(i % level.getWidth(), i / level.getWidth());
        }

        // prep an arraylist for the compressed data
        ArrayList<Integer> compressedData = new ArrayList<Integer>();

        // loop over the level data and attempt to compress
        for (int charPos = 0; charPos < levelData.length;) {

            // store the index of the flag byte in the arraylist
            // we'll use this later to replace the placeholder with the
            // actual flag byte
            int flagIndex = compressedData.size();

            // add a placeholder flag byte into the compressed data array
            int flags = 0;
            compressedData.add(flags);

            // loop 8 times (each bit of the flag byte)
            for (int i = 0; i < 8; i++) {

                // scan the window for pattern matches at the current position
                Pair p = scanForPattern(levelData, charPos, WINDOW);

                // if no pattern match was found or the match was < 2 bytes long
                if (p == null || p.length < 3) {
                    
                    // add the byte of level data to the array
                    compressedData.add(levelData[charPos]);

                    // move to the next char
                    ++charPos;

                    //set the flag bit
                    flags |= (1 << i);

                } else {
                    // calculate the 2 compressed bytes
                    int second = ((p.length - 3 & 0xF) << 4) | ((p.position & 0xF00) >> 8);
                    int first = (p.position & 0xFF);

                    compressedData.add(first);
                    compressedData.add(second);

                    // move the current char pos pointer
                    charPos += p.length;
                }

                // check that we're not exceeding the data bounds
                if (charPos >= levelData.length) {
                    break;
                }
            }

            // replace the placeholder byte with the actual flags
            compressedData.set(flagIndex, flags);
        }

        // add the end-of-stream marker word
        compressedData.add(0);
        compressedData.add(0);

        // write the compressed data to the output stream
        for (int i = 0; i < compressedData.size(); i++) {
            out.write(compressedData.get(i));
        }
    }

    /**
     * Scan the input stream for a match for the pattern starting at
     * codePos within the specified window.
     * @param input The input stream.
     * @param codePos The index of the pattern within the input stream.
     * @param window The size of the sliding window.
     * @return An instance of Pair if a match was found. Null otherwise.
     */
    private Pair scanForPattern(int[] input, int codePos, int window) {

        List<Pair> matches = new ArrayList<Pair>();

        // scan through the input stream looking for the pattern at codePos
        for (int i = codePos - 1;
                i >= Math.max(codePos - window, 0);
                //i < codePos && codePos < input.length - 4;
                i--) {

            int matchPos = i;
            int length = 0;

            // try to find the longest matching string while
            // keeping within the array's bounds & within MAX_LENGTH.
            while (length + codePos < input.length
                    && length < MAX_LENGTH
                    && input[i + length] == input[codePos + length]) {
                ++length;
            }

            if(length >= 3) {
                matches.add(new Pair(matchPos - codePos, length));
            }

        }

        // if no match was found just return null
        if (matches.size() == 0) {
            return null;

        } else {
            // at least one match was found. loop through the array
            // and find the longest match
            Pair pair = matches.get(0);

            for (Pair p : matches) {
                if (p.length > pair.length) {
                    pair = p;
                }
            }

            // return the longest match
            return pair;
        }
    }


    /**
     * A structure representing the length and absolute position
     * of a pattern match.
     */
    private class Pair {

        int position;
        int length;

        public Pair(int pos, int length) {
            this.position = pos;
            this.length = length;
        }
    }
}
