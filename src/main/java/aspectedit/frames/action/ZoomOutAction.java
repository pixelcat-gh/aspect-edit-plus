
package aspectedit.frames.action;

import aspectedit.components.tileview.TileView;
import aspectedit.images.IconManager;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;

/**
 *
 * @author mark
 */
public class ZoomOutAction extends AbstractAction {

    private TileView tileView;
    
    private boolean oldEnabled = false;

    /**
     * A listener that will handle property change events from the associated
     * {@link TileView}. Listens for the changes to the zoom factor and enables
     * or disables the action as appropriate.
     */
    private PropertyChangeListener propertyListener;


    /**
     * Construct
     */
    public ZoomOutAction() {
        super("Zoom out", IconManager.getIcon(IconManager.ZOOM_OUT));

        //create the property change listener
        propertyListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                tileViewPropertyChanged(evt);
            }
        };
    }


    private void tileViewPropertyChanged(PropertyChangeEvent evt) {
        if("zoomFactor".equals(evt.getPropertyName())) {
            setEnabled(tileView.getZoomFactor() > 1);
        }
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        oldEnabled = isEnabled();

        if(this.tileView != null) {
            this.tileView.removePropertyChangeListener(propertyListener);
        }

        this.tileView = tileView;

        if(tileView != null) {
            this.tileView.addPropertyChangeListener(propertyListener);
        }

        //fire a property change since the tileset can affect the "enabled" property
        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled()
                && tileView != null
                && tileView.getZoomFactor() > 1;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileView != null && tileView.getZoomFactor() > 1) {

            tileView.setZoomFactor(tileView.getZoomFactor() - 1);

            firePropertyChange("enabled", oldEnabled, isEnabled());
        }
    }

}
