
package aspectedit.io;

import aspectedit.tiles.ITile;
import aspectedit.tiles.Tileset;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mark
 */
public class TilesetWriter extends ResourceWriter<Tileset> {

    public TilesetWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Tileset tileset) throws IOException {
        for(ITile t : tileset) {
            byte[][] data = t.getBitplanes();
            for(int y=0; y<8; y++) {
                out.write(data[y][0]);
                out.write(data[y][1]);
                out.write(data[y][2]);
                out.write(data[y][3]);
            }
        }
    }

}
