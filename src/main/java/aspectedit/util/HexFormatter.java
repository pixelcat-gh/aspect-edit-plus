
package aspectedit.util;

import java.text.ParseException;
import javax.swing.JFormattedTextField;

/**
 *
 * @author mark
 */
public class HexFormatter extends JFormattedTextField.AbstractFormatter {

    @Override
    public Object stringToValue(String text) throws ParseException {
        if(text.isEmpty()) return 0;
        
        return Integer.parseInt(text, 16);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        return String.format("%X", value == null ? 0 : value);
    }

}
