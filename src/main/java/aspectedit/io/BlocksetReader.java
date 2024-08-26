
package aspectedit.io;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import java.io.IOException;
import java.io.InputStream;

/**
 * Reads a {@link Blockset} and its associated mappings in from a raw
 * binary file. If the mappings are absolute pointers they will be converted
 * to zero-based index values.
 *
 * @author mark
 */
public class BlocksetReader extends ResourceReader<Blockset> {


    public BlocksetReader(InputStream in) {
        super(in);
    }


    @Override
    public Blockset read() throws IOException {
        if(in.skip(offset) < offset) {
            throw new IOException("Invalid offset.");
        }

        Blockset blockset = new Blockset();

        // an array to hold the absolute pointers in the binary data
        int[] absolutePointers = new int[256];
        int maxPointer = 0;
        int minPointer = 0xFFFF;

        // read the absolute pointers into the array and calculate the
        // min and max pointers. The pointers will be used to calculate
        // the size of the blockset.
        for (int i = 0; i < absolutePointers.length; i++) {

            // since the FileInputStream.skip() method doesnt stop at EOF
            // we need to check for the -1 byte at every iteration of the loop
            int b = in.read();
            if(b == -1) {
                throw new IOException("Unexpected end of stream.");
            }

            absolutePointers[i] = (b | (in.read() << 8));

            maxPointer = Math.max(absolutePointers[i], maxPointer);
            minPointer = Math.min(absolutePointers[i], minPointer);
        }


        // convert the absolute pointers to zero-based indices
        if (maxPointer >= 0x8000) {

            for (int i = 0; i < 256; i++) {
                absolutePointers[i] = (absolutePointers[i] - minPointer) / 32;
            }

            maxPointer = (maxPointer - minPointer) / 32;
        }

        // the maxPointer variable now contains the number of blocks to read

        // read blocks - divide by 32 = 16 elements * 2 bytes
        // int blockCount = (maxPointer - minPointer) / 32;
        for (int i = 0; i <= maxPointer; i++) {
            Block block = new Block();

            // set each element in the block
            for (int j = 0; j < 16; j++) {
                int value = (in.read() | (in.read() << 8));

                block.setElementAt(value, j % 4, j / 4);
            }

            // add the block to the blockset
            blockset.addBlock(block);
        }

        //set mappings
        for (int i = 0; i < 256; i++) {
            blockset.setBlockIndexForMapping(i, absolutePointers[i]);
        }

        blockset.setModified(false);
        
        return blockset;
    }
}
