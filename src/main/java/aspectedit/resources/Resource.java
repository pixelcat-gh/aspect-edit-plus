
package aspectedit.resources;

/**
 *
 */
public interface Resource {

    public void setName(String name);
    public String getName();

    public void setFileName(String filename);
    public String getFileName();

    public void setOffset(int offset);
    public int getOffset();
    
    public void setOriginalSize(int size);
    public int getOriginalSize();

    public boolean isModified();
    public void setModified(boolean modified);
    
}
