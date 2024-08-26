package aspectedit.io;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * This class generates a WLA-DX assembly source file for a {@link Blockset}
 * that can be included in a project.
 * 
 * @author mark
 */
public class BlocksetAsmWriter extends ResourceWriter<Blockset> {

    public BlocksetAsmWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(Blockset blockset) throws IOException {
        if(blockset.size() == 0) {
            throw new IOException("Cannot save empty blockset.");
        }

        // Generate a label prefix from the file name
        String labelValue = blockset.getName().replaceAll(":|\\.|;|\\-|\\s|&", "_");


        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        int size = blockset.size();

        //write the mapping labels
        for(int i=0; i<256; i++) {
            bw.write(".dw ");
            bw.write(labelValue);
            bw.write("_");
            bw.write(
                    String.valueOf(Math.min(blockset.getBlockIndexForMapping(i), size)));
            bw.write("\n");
        }

        //write the block values
        for(int i=0; i<blockset.size(); i++) {
            bw.write(labelValue);
            bw.write("_");
            bw.write(String.valueOf(i));
            bw.write(":\n");

            Block block = blockset.getBlockAt(i);
            for(int j=0; j<16; j++) {
                int value = block.getElementAt(j % 4, j / 4);
                bw.write(".dw $");
                bw.write(String.format("%X", (short)value));
                bw.write("\n");
            }
            bw.flush();
        }

        out.flush();
    }

}
