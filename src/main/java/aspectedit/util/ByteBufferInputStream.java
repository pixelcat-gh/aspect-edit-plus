
package aspectedit.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 */
public class ByteBufferInputStream extends InputStream {

    private ByteBuffer buf;
    private long offset = 0;

    public ByteBufferInputStream(ByteBuffer buf) {
        this.buf = buf;
    }
    
    @Override
    public int read() throws IOException {
        if(!buf.hasRemaining())
            return -1;
        return buf.get();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = Math.min(len, buf.remaining());

        buf.get(b, off, len);
        return len;
    }

    @Override
    public long skip(long n) throws IOException {
        buf.position(buf.position() + (int) offset);

        return n;
    }

}
