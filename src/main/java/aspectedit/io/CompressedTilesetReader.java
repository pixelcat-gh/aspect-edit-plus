
package aspectedit.io;

import aspectedit.tiles.Tileset;
import aspectedit.tiles.Tile;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class CompressedTilesetReader extends TilesetReader {

    private byte[] buffer;

    private int flagPointer;
    private int sourcePointer;
    private int compressionFlags;
    private Tileset tileset;

    public CompressedTilesetReader(InputStream in) {
        super(in);
    }

    @Override
    public Tileset read() throws IOException {
        tileset = new Tileset();
        tileset.setCompressed(true);
        
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        //skip 2 bytes
        if(in.read() != 1 || in.read() != 0) {
            throw new IOException("Data does not appear to be a compressed tileset.");
        }
        
        //read tilecount
        tileCount = ((in.read() & 0xFF) | ((in.read() & 0xFF) << 8));
        //read flagPointer
        flagPointer = ((in.read() & 0xFF) | ((in.read() & 0xFF) << 8));

        sourcePointer = 0; //offset + 6;

        int dataLength = (int)(flagPointer + (Math.ceil(tileCount * 2 /8))) - 5;

        flagPointer -= 6;

        if(dataLength < 0) {
            throw new IOException("Invalid compressed data length.\nThis tileset may be uncompressed.");
        }
        
        buffer = new byte[dataLength];

        int readLen = in.read(buffer);

        for (int i = 0; i < tileCount; i += 4) {
            for (int j = 0; j < 8; j += 2) {
                if (i + j / 2 >= tileCount) {
                    break;
                }
                if (j == 0) {
                    getCompressionFlags();
                }

                switch ((compressionFlags >> j) & 3) {
                    case 0:
                        tileset.addTile(getBlankTile());
                        break;
                    case 1:
                        tileset.addTile(getUncompressedTile());
                        break;
                    case 2:
                        tileset.addTile(getCompressedTile());
                        break;
                    case 3:
                        tileset.addTile(getXorCompressedTile());
                        break;
                }
            }

        }

        tileset.setModified(false);
        
        return tileset;
    }

    private void getCompressionFlags() {
        compressionFlags = buffer[flagPointer];
        ++flagPointer;
    }

    private Tile getBlankTile() {
        return new Tile();
    }

    private Tile getUncompressedTile() {
        byte[][] tileData = new byte[8][4];
        for (int i = 0; i < 8; i++) {
            tileData[i][0] = buffer[sourcePointer++];
            tileData[i][1] = buffer[sourcePointer++];
            tileData[i][2] = buffer[sourcePointer++];
            tileData[i][3] = buffer[sourcePointer++];
        }

        Tile tile = new Tile();
        tile.setBitplanes(tileData);
        tile.setModified(false);

        return tile;
    }

    private Tile getCompressedTile() {
        byte[] data = getUncompressedData();

        byte[][] tileData = new byte[8][4];
        for(int i=0; i<8; i++) {
            tileData[i][0] = data[i * 4];
            tileData[i][1] = data[i * 4 + 1];
            tileData[i][2] = data[i * 4 + 2];
            tileData[i][3] = data[i * 4 + 3];
        }

        Tile tile = new Tile();
        tile.setBitplanes(tileData);
        tile.setModified(false);
        
        return tile;
    }

    private Tile getXorCompressedTile() {
        byte[] data = getUncompressedData();

        for (int i = 0; i < 14; i += 2) {
            data[i + 2] ^= data[i];
            data[i + 3] ^= data[i + 1];
            if (i + 19 < 32) {
                data[i + 18] ^= data[i + 16];
                data[i + 19] ^= data[i + 17];
            }
        }

        byte[][] tileData = new byte[8][4];
        for(int i=0; i<8; i++) {
            tileData[i][0] = data[i * 4];
            tileData[i][1] = data[i * 4 + 1];
            tileData[i][2] = data[i * 4 + 2];
            tileData[i][3] = data[i * 4 + 3];
        }

        Tile tile = new Tile();
        tile.setBitplanes(tileData);
        tile.setModified(false);
        
        return tile;
    }

    private byte[] getUncompressedData() {
        byte[] tileData = new byte[32];
        byte[] flags = new byte[4];

        for (int i = 0; i < 4; i++) {
            flags[i] = buffer[sourcePointer++];
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if (((flags[i] >> j) & 1) == 1) {
                    tileData[i * 8 + j] = buffer[sourcePointer++];

                } else {
                    tileData[i * 8 + j] = 0;

                }
            }
        }

        return tileData;
    }
}
