
package aspectedit.io;

import aspectedit.tiles.Tileset;
import aspectedit.tiles.Tile;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class TilesetReader extends ResourceReader<Tileset> {

    protected int tileCount = -1;

    public TilesetReader(InputStream in) {
        super(in);
    }

    public TilesetReader(InputStream in, int tileCount) {
        super(in);
        this.tileCount = tileCount;
    }

    public int getTileCount() {
        return tileCount;
    }

    public void setTileCount(int tileCount) {
        this.tileCount = tileCount;
    }

    @Override
    public Tileset read() throws IOException {
        byte[] buffer = new byte[32];
        Tileset tileset = new Tileset();

        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        int data = -1;
        int count = 0;

        if(tileCount == 0) tileCount = -1;
        
        while( (data = in.read()) != -1 && (tileCount == -1 || count < tileCount)) {
            buffer[0] = (byte)data;

            if(in.read(buffer, 1, buffer.length - 1) < buffer.length - 1) {
                throw new IOException("Unexpected end of stream.");
            }

            byte[][] bitplanes = new byte[8][4];
            for(int y=0; y<8; y++) {
                bitplanes[y][0] = buffer[y*4];
                bitplanes[y][1] = buffer[y*4+1];
                bitplanes[y][2] = buffer[y*4+2];
                bitplanes[y][3] = buffer[y*4+3];

            }

            Tile tile = new Tile();
            tile.setBitplanes(bitplanes);
            tile.setModified(false);
            
            tileset.addTile(tile);

            ++count;
        }

        tileset.setModified(false);
        
        return tileset;
    }

}
