
package aspectedit.components.tileview;

import aspectedit.tiles.ITile;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 *
 */
public class DefaultTileViewCellRenderer implements TileViewCellRenderer {

    private boolean drawBorder = true;
    private boolean drawIndex = false;
    private Color borderColour = Color.LIGHT_GRAY;
    private Color indexColour = Color.YELLOW;
    private Color textBorderColor = Color.BLACK;

    public DefaultTileViewCellRenderer() {

    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    public boolean isDrawIndex() {
        return drawIndex;
    }

    public void setDrawIndex(boolean drawIndex) {
        this.drawIndex = drawIndex;
    }

    public Color getBorderColour() {
        return borderColour;
    }

    public void setBorderColour(Color borderColour) {
        this.borderColour = borderColour;
    }

    public Color getIndexColour() {
        return indexColour;
    }

    public void setIndexColour(Color indexColour) {
        this.indexColour = indexColour;
    }


    @Override
    public void render(Graphics2D g, TileViewModel model, ITile tile, int x, int y, int w, int h, boolean selected) {

        Graphics2D g2 = (Graphics2D) g.create();
        FontMetrics fm = g.getFontMetrics();

        g.drawImage(tile.getImage(), x, y, w, h, null);


        if(drawBorder) {
            g.setColor(borderColour);

            //draw bottom line
            g.drawLine(x, y+h-1, x+w-1, y+h-1);
            //draw right-hand line
            g.drawLine(x+w-1, y, x+w-1, y+h-1);
        }

        if(selected) {
            g.setColor(Color.RED);
            g.drawRect(x-1, y-1, w, h);
        }

        if(drawIndex) {
            g.setColor(textBorderColor);

            String value = String.valueOf(model.indexOf(tile));

            int sw = fm.stringWidth(value);

            for (int xOffset = -1; xOffset < 2; xOffset++) {
                for (int yOffset = -1; yOffset < 2; yOffset++) {
                    g.drawString(value, x + w - sw - 1 + xOffset, y + h - 1 + yOffset);
                }
            }

            g.setColor(indexColour);
            g.drawString(value, x + w - sw - 1, y + h - 1);
        }
    }

}
