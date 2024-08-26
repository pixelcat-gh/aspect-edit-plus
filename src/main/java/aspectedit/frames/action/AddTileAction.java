
package aspectedit.frames.action;

import aspectedit.components.tileview.TileView;
import aspectedit.images.IconManager;
import aspectedit.tiles.ITile;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * This action adds a tile to the TileView's model.
 *
 * @author mark
 */
public class AddTileAction extends AbstractAction {

	/* The tile view */
    private TileView tileView;

	
	/**
	* Construct with default name and icon.
	*/
    public AddTileAction() {
        this("Add Tile", IconManager.getIcon(IconManager.ADD));
    }

	/**
	* Construct with the specified name and icon.
	*/
    public AddTileAction(String name, Icon icon) {
        super(name, icon);
    }


	/**
	* Get the TileView.
	*/
    public TileView getTileView() {
        return tileView;
    }

	/**
	* Set the TileView.
	*
	* @param tileView The new TileView.
	*/
    public void setTileView(TileView tileView) {
        this.tileView = tileView;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileView != null) {

            // reflect on the TileView's model to get the tile class
            Class<? extends ITile> tileClass = null;
            try {
                Method method = tileView.getModel().getClass().getMethod("getTile", int.class);

                tileClass = (Class<? extends ITile>) method.getReturnType();

                // create a new instance of the tile class...
                ITile tileInstance = tileClass.newInstance();

                // ...and add it to the model
                tileView.addTile(tileInstance);

				tileView.revalidate();
				tileView.repaint();
				
            } catch (NoSuchMethodException ex) {
                // should not happen since the "getTile" method
				// is part of the interface definition
                ex.printStackTrace();
				
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

}
