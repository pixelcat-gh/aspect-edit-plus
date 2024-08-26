
package aspectedit.components;

import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

/**
 * A combo box editor that accepts integer input in base-10 or base-16.
 * Base-16 integers must be prefixed with either "$" or "0x".
 */
public class ComboBoxNumberEditor implements ComboBoxEditor {

    private JTextField editComponent;
    private Object oldObject;

    public ComboBoxNumberEditor() {
        editComponent = new JTextField(4);
        editComponent.setBorder(null);
    }

    @Override
    public Component getEditorComponent() {
        return editComponent;
    }

    @Override
    public void setItem(Object anObject) {
        oldObject = anObject;

        editComponent.setText(anObject.toString());
    }

    @Override
    public Object getItem() {
        String text = editComponent.getText();
        Integer value = null;

        if(text.startsWith("$") || text.startsWith("0x")) {
            // try to parse a hex number
            // remove the prefix
            if(text.startsWith("$")) text = text.substring(1);
            else if(text.startsWith("0x")) text = text.substring(2);

            try {
                value = Integer.parseInt(text, 16);
            } catch (NumberFormatException ex) {
                // value = null
            }


        } else {
            //try to parse a decimal number
            try {
                value = Integer.parseInt(text);
            } catch (NumberFormatException ex) {
                // value = null
            }
        }

        // if the attempt to parse an integer from the string failed
        // just reset to the old object
        if(value == null) return oldObject;
        else return value;
    }

    @Override
    public void selectAll() {
        editComponent.selectAll();
        editComponent.requestFocus();
    }

    @Override
    public void addActionListener(ActionListener l) {
        editComponent.addActionListener(l);
    }

    @Override
    public void removeActionListener(ActionListener l) {
        editComponent.removeActionListener(l);
    }

}
