
package aspectedit.io;

import aspectedit.resources.*;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public abstract class ResourceReader<T extends Resource> {

    protected InputStream in;
    protected long offset;

    public ResourceReader(InputStream in) {
        this.in = in;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public abstract T read() throws IOException;

}
