/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.io;

import aspectedit.level.Level;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 *
 * @author mark
 */
public class TTLevelReader extends ResourceReader<Level> {

    public TTLevelReader(InputStream in) {
        super(in);
    }


    @Override
    public Level read() throws IOException {
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        boolean read = true;
        ArrayList<Integer> levelData = new ArrayList<Integer>();

        while(read) {
            int compressionFlags = in.read();

            if(compressionFlags == -1) {
                throw new IOException("Unexpected end of stream!");
            }
            
            for(int i=0; i<8; i++) {

                if((compressionFlags >> i & 1) == 1) {
                    // read uncompressed
                    levelData.add(in.read());

                } else {
                    if(levelData.size() == 0) {
                        throw new IOException("Cannot decompress. Data corrupt.");
                    }

                    // read compressed
                    int dataPointer = (in.read() | (in.read() << 8));

                    // check for end-of-stream marker
                    if(dataPointer == 0) {
                        read = false;
                        continue;
                    }

                    // extract the count from the data pointer
                    int count = (dataPointer >> 12) + 3;

                    //set the sign bit on the data pointer
                    dataPointer |= 0xF000;

                    short relative = (short) dataPointer;

                    // decompress the data
                    for(int j=count; j>0; j--) {
                        levelData.add(
                                levelData.get(
                                Math.max(levelData.size() + relative, 0)));
                    }
                }
            }
        }

        int[] levelDataArray = new int[levelData.size()];
        for(int i=0; i<levelData.size(); i++) {
            levelDataArray[i] = levelData.get(i);
        }

        Level level = new Level();
        level.setData(levelDataArray);

        return level;
    }

}
