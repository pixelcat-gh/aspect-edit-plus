
package aspectedit.images;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author mark
 */
public class IconManager {

    public static final String ADD = "plus.png";
    public static final String DELETE = "cross.png";
    public static final String EDIT = "pencil.png";
    public static final String PALETTE = "color.png";
    public static final String PALETTE_NEW = "color_plus.png";
    public static final String TILESET = "pictures.png";
	public static final String TILESET_NEW = "pictures_plus.png";
    public static final String OPEN = "folder_open.png";
    public static final String SAVE = "disk_black.png";
    public static final String SAVE_AS = "disk_arrow.png";
    public static final String ZOOM_IN = "magnifier_zoom.png";
    public static final String ZOOM_OUT = "magnifier_zoom_out.png";
    public static final String ASM_FILE = "document_text.png";
    public static final String EXPORT_IMAGE = "image_pencil.png";
    public static final String BLOCKSET = "puzzle.png";
	public static final String BLOCKSET_NEW = "puzzle_plus.png";
	public static final String LEVEL = "picture.png";
	public static final String LEVEL_NEW = "picture_plus.png";
    public static final String ARROW_RIGHT = "arrow.png";
    public static final String ARROW_LEFT = "arrow_180.png";
	
    public static Icon getIcon(String name) {
        return new ImageIcon(IconManager.class.getResource("/aspectedit/images/" + name));
    }

}
