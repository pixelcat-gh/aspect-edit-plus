
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
public class ZoomInAction extends AbstractAction {

    private TileView tileView;

    private PropertyChangeListener zoomPropertyListener;

    public ZoomInAction() {
        super("Zoom in", IconManager.getIcon(IconManager.ZOOM_IN));

        zoomPropertyListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if("zoomFactor".equals(evt.getPropertyName())) {
                    setEnabled(tileView.getZoomFactor() < TileView.MAX_ZOOM_FACTOR);
                }
            }

        };
    }

    public TileView getTileView() {
        return tileView;
    }

    public void setTileView(TileView tileView) {
        boolean oldEnabled = isEnabled();

        if(this.tileView != null) {
            this.tileView.removePropertyChangeListener(zoomPropertyListener);
        }

        this.tileView = tileView;

        if(tileView != null) {
            tileView.addPropertyChangeListener(zoomPropertyListener);
        }
        
        //fire a property change since the tile view can affect the "enabled" property
        firePropertyChange("enabled", oldEnabled, isEnabled());
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled()
                && tileView != null
                && tileView.getZoomFactor() < TileView.MAX_ZOOM_FACTOR;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(tileView != null) {

            boolean oldEnabled = isEnabled();

            tileView.setZoomFactor(tileView.getZoomFactor() + 1);

            firePropertyChange("enabled", oldEnabled, isEnabled());
        }
    }

}
