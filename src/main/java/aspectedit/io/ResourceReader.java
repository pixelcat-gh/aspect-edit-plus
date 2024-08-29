
package aspectedit.io;

import aspectedit.level.Level;
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

    /**
     * Estimates the width of the level based on the pre-configured
     * widths specified in the base Sonic 2 engine and the size of
     * the decompressed level data.
     *
     * @param decompressedSize Decompressed data length.
     * @return An estimated level width. Defaults to 168 if no match was found.
     */
    protected int estimateLevelWidth(int decompressedSize) {
        for (int i = Level.VALID_WIDTHS.length - 1; i >= 0; i--) {
            if (decompressedSize % Level.VALID_WIDTHS[i] == 0) {
                return Level.VALID_WIDTHS[i];
            }
        }

        // return a default size
        return 168;
    }
}
