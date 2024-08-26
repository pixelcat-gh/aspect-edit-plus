
package aspectedit.io;

import aspectedit.tiles.ITile;
import aspectedit.tiles.Tileset;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Vector;

/**
 *
 * @author mark
 */
public class CompressedTilesetWriter extends TilesetWriter {

    //output buffer
    private byte[] compressedData;
    //insert pointer for the output buffer
    private int insertPtr = 0;
    
    private byte currentFlag = 0;
    private int flagCount = 0;
    private Vector<Byte> compressionFlags;


    public CompressedTilesetWriter(OutputStream out) {
        super(out);

        compressionFlags = new Vector<Byte>();
    }

    @Override
    public void write(Tileset tileset) throws IOException {
        //allocate some memory for the compressed data
        compressedData = new byte[tileset.size() * 32];
        insertPtr = 0;

        //set up the 6-byte header in the output buffer
        compressedData[insertPtr++] = 1;    //write the marker word
        compressedData[insertPtr++] = 0;
        //write the tile count
        compressedData[insertPtr++] = (byte)(tileset.size() & 0xFF);
        compressedData[insertPtr++] = (byte)(tileset.size() >> 8 & 0xFF);
        //write a placeholder value for the flag pointer
        compressedData[insertPtr++] = 0;
        compressedData[insertPtr++] = 0;

        //compress the tiles
        for(ITile tile : tileset) {
            compressTile(tile);
        }

        //write the actual value for the flag pointer
        compressedData[4] = (byte)(insertPtr & 0xFF);
        compressedData[5] = (byte)(insertPtr >> 8 & 0xFF);

        //write the compression flags
        if(flagCount != 0) compressionFlags.add(currentFlag);
        for(int i=0; i<compressionFlags.size(); i++) {
            compressedData[insertPtr++] = compressionFlags.get(i);
        }

        //write the buffer to the output stream
        out.write(compressedData, 0, insertPtr);
    }

    private void setFlag(int value) {
        value &= 3;
        
        currentFlag |= (value << (flagCount * 2));
        
        if(++flagCount >= 4) {
            compressionFlags.add(currentFlag);
            currentFlag = 0;
            flagCount = 0;
        }
    }

    
    private void compressTile(ITile tile) {
        byte[][] bitplanes = tile.getBitplanes();
        
        byte[] buffer = new byte[32];
        byte[] xored = null;
        
        //flatten out the 2D array
        for(int i=0; i<8; i++) {
            buffer[i * 4] = bitplanes[i][0];
            buffer[i * 4 + 1] = bitplanes[i][1];
            buffer[i * 4 + 2] = bitplanes[i][2];
            buffer[i * 4 + 3] = bitplanes[i][3];
        }
        
        //xor a copy of the tile data
        xored = xorData(buffer);
        
        int zeroCount = 0;
        int xorZeroCount = 0;
        boolean isBlank = true;
        
        //check to see if the tile is blank &
        //count the number of 0's to see if it's worth compressing the data.
        for(int i=0; i<32; i++) {
            if(buffer[i] == 0) ++zeroCount;
            if(xored[i] == 0) ++xorZeroCount;
            
            if(isBlank && buffer[i] != 0) isBlank = false;
        }
        
        
        if(isBlank) {
            //tile is blank - set the flag to 0
            setFlag(0);
            
        } else {
            //calculate the compression method to use
            if(zeroCount > 4 && zeroCount >= xorZeroCount) {
                //compress the data
                setFlag(2);
                writeCompressedTile(buffer);

            } else {

                if(xorZeroCount > 4) {
                    //compress the xored data
                    setFlag(3);
                    writeCompressedTile(xored);

                } else {
                    //write an uncompressed tile
                    setFlag(1);
                    writeUncompressedTile(buffer);
                }
            }
        }
        
    }

    private void writeUncompressedTile(byte[] data) {
        for(int i=0; i<data.length; i++) {
            compressedData[insertPtr++] = data[i];
        }
    }

    /**
     * Compresses the tile data and copies it to the output buffer.
     * @param data The uncompressed tile data.
     */
    private void writeCompressedTile(byte[] data) {
        CompressedTile tile = compressData(data);

        compressedData[insertPtr++] = tile.flags[0];
        compressedData[insertPtr++] = tile.flags[1];
        compressedData[insertPtr++] = tile.flags[2];
        compressedData[insertPtr++] = tile.flags[3];

        for(int i=0; i<tile.data.length; i++) {
            compressedData[insertPtr++] = tile.data[i];
        }
    }

    /**
     * Compresses tile data.
     * @param data The uncompressed data.
     * @return
     */
    private CompressedTile compressData(byte[] data) {
        CompressedTile tile = new CompressedTile();

        Vector<Byte> compressed = new Vector<Byte>();

        for(int i=0; i<32; i++) {
            if(data[i] == 0) {
                tile.flags[i/8] &= 0xFF ^ (1 << (i%8));

            } else {
                tile.flags[i/8] |= 1 << i%8;
                compressed.add(data[i]);
            }
        }

        tile.data = new byte[compressed.size()];
        for(int i=0; i<compressed.size(); i++) {
            tile.data[i] = compressed.get(i);
        }

        return tile;
    }

    /**
     * Applies a XOR routine on the tile data.
     * @param data The uncompressed tile data.
     * @return
     */
    private byte[] xorData(byte[] data) {
        byte[] xored = Arrays.copyOf(data, 32);

        for(int i=12; i>=0; i-=2)
		{
			xored[i+18] ^= xored[i+16];
			xored[i+19] ^= xored[i+17];
			xored[i+2] ^= xored[i];
			xored[i+3] ^= xored[i+1];
		}

        return xored;
    }


    /**
     * A structure representing a compressed tile.
     */
    private class CompressedTile {
        private byte[] data;
        private byte[] flags = new byte[4];
    }
    
}
