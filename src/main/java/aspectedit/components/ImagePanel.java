/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package aspectedit.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 *
 * @author mark
 */
public class ImagePanel extends JPanel {

    private Image image;

    public ImagePanel() {

    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if(getParent() instanceof JViewport && image != null) {
            Dimension d = new Dimension();
            d.width = image.getWidth(null);
            d.height = image.getHeight(null);

            return d;
            
        } else {
            return super.getPreferredSize();
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(image != null) {
            g.drawImage(image, 0, 0, 
                    (int)Math.min(image.getWidth(null), getWidth()),
                    (int)Math.min(image.getHeight(null), getHeight()), null);
        }
    }
}
