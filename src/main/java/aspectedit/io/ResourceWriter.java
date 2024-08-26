
package aspectedit.io;

import aspectedit.resources.Resource;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mark
 */
public abstract class ResourceWriter<T extends Resource> {

    protected OutputStream out;

    public ResourceWriter(OutputStream out) {
        this.out = out;
    }

    public abstract void write(T t) throws IOException;

}
