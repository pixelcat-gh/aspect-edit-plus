
package aspectedit.io.filefilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 */
public class BasicFileFilter extends FileFilter {

    private String fullExtension;
    private String extension;
    private String description;

    public BasicFileFilter(String extension, String description) {
        this.extension = extension;
        this.fullExtension = "." + extension;
        this.description = description;
    }

    public File checkExtension(File file) {
        if(file.getAbsolutePath().endsWith(fullExtension)) {
            return file;
        } else {
            return new File(file.getAbsolutePath() + fullExtension);
        }
    }

    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getAbsolutePath().endsWith(fullExtension);
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getExtension() {
        return extension;
    }

}
