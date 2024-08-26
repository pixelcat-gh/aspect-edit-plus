
package aspectedit.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 */
public class SpinnerHexEditor extends JFormattedTextField {

    private JSpinner spinner;

    public SpinnerHexEditor(JSpinner spinner) {
        super(new HexFormatter());

        this.spinner = spinner;

        spinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent evt) {
                updateValue(evt);
            }

        });

        this.addPropertyChangeListener("value", new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SpinnerHexEditor.this.propertyChange(evt);
            }

        });

        spinner.setEditor(this);
    }

    protected void propertyChange(PropertyChangeEvent evt) {
        Object o = getValue();

        if(o != null) spinner.getModel().setValue(o);
    }

    protected void updateValue(ChangeEvent evt) {
        if(evt.getSource() instanceof JSpinner) {
            JSpinner spinner = (JSpinner) evt.getSource();

            if(spinner.getModel() instanceof SpinnerNumberModel) {
                SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
                if(! model.getValue().equals(getValue())) {
                    setValue(model.getValue());
                }
            }
        }
    }

}
