
package aspectedit;

import aspectedit.frames.AspectEdit;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author mark
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {}
        
        SwingUtilities.invokeLater(() -> {
            AspectEdit aspectEdit = new AspectEdit();
            aspectEdit.setPreferredSize(new Dimension(800,600));
            aspectEdit.setLocationRelativeTo(null);
            aspectEdit.setVisible(true);
        });
    }

}
