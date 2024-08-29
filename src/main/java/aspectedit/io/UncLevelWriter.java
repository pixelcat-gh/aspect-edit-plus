
package aspectedit.io;

import aspectedit.level.Level;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mark
 */
public class UncLevelWriter extends ResourceWriter<Level> {

    public UncLevelWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Level level) throws IOException {
        // build a sequential array of level data
        int[] blocks = new int[level.getWidth() * level.getHeight()];

        int width = level.getWidth();

        for(int y=0; y<level.getHeight(); y++) {
            for(int x=0; x<level.getWidth(); x++) {
                blocks[y * width + x] = level.getMappingValue(x, y);
            }
        }

        // write the data
        for (int block : blocks) {
            out.write(block);
        }
    }

}
