
package aspectedit.io;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Write a {@link Blockset} out to a binary file.
 *
 * NOTE: The output of this class is *NOT* directly compatible with the
 * Sonic 2 disassembly since the mappings are index values, not pointers.
 * 
 * @author mark
 */
public class BlocksetWriter extends ResourceWriter<Blockset> {

    public BlocksetWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Blockset blockset) throws IOException {
        
        //write the mapping values
        for(int i=0; i<256; i++) {
            int value = blockset.getBlockIndexForMapping(i);
            out.write(value);
            out.write(value >> 8);
        }

        //write the block values
        for(int i=0; i<blockset.size(); i++) {

            Block block = blockset.getBlockAt(i);
            for(int j=0; j<16; j++) {
                int value = block.getElementAt(j % 4, j / 4);
                
                out.write(value);
                out.write(value >> 8);
            }
        }
    }

}
