
package aspectedit.resources;

/**
 *
 */
public abstract class AbstractResource implements Resource {

    private String name;
    private String fileName;
    private int offset;
    private int originalSize;
    private boolean modified = false;

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setFileName(String filename) {
        this.fileName = filename;
        modified = true;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
        modified = true;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public void setOriginalSize(int size) {
        this.originalSize = size;
    }

    @Override
    public int getOriginalSize() {
        return originalSize;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return name;
    }

}
