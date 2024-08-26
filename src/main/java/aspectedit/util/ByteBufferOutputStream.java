/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

/**
 *
 * @author mark
 */
public class ByteBufferOutputStream extends OutputStream {

    private ByteBuffer buf;
    
    public ByteBufferOutputStream(ByteBuffer buf) {
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {
        buf.put((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buf.put(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        buf.put(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        if(buf instanceof MappedByteBuffer) {
            ((MappedByteBuffer) buf).force();
        }
    }


}
