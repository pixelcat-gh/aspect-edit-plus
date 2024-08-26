/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.util;

import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 */
public class OffsetSpinnerBuilder {

    public static JSpinner build() {
        JSpinner spinner = new JSpinner();

        // create the editor component
        SpinnerHexEditor editor = new SpinnerHexEditor(spinner);

        // create the spinner's model
        SpinnerNumberModel model = new SpinnerNumberModel(0, 0, null, 1);

        spinner.setModel(model);
        
        // calculate an optimal width for the spinner
        FontMetrics fm = editor.getFontMetrics(editor.getFont());
        int minWidth = fm.stringWidth("00000000");
        Dimension d = editor.getPreferredSize();

        if(d.width < minWidth) {
            d.width = minWidth;
            editor.setPreferredSize(d);
        }

        return spinner;
    }
}
