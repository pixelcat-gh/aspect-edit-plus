
package aspectedit.tiles;

import aspectedit.palette.Palette;
import aspectedit.resources.AbstractResource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mark
 */
public class Tileset 
        extends AbstractResource
        implements Iterable<Tile> {

    public static final Tileset EMPTY_TILESET;

    static {
        EMPTY_TILESET = new Tileset();
        EMPTY_TILESET.addTile(Tile.EMPTY_TILE);
    }

    private boolean compressed;
    private ArrayList<Tile> tiles;
    private Palette palette = Palette.BLANK_PALETTE;

    public Tileset() {
        tiles = new ArrayList<Tile>();
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        if(this.palette != palette) {
            this.palette = palette;

            for(Tile t : tiles) {
                t.setPalette(palette);
            }
        }
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }


    public Tile getTile(int index) {
        return tiles.get(index);
    }


    public void addTile(Tile tile) {
        tile.setPalette(palette);
        tiles.add(tile);

        setModified(true);
    }

    public List<Tile> getTiles() {
        return tiles;
    }


    public void setTile(int index, Tile tile) {
        if(tiles.get(index) != tile) {
            tile.setPalette(palette);
            tiles.set(index, tile);
            
            setModified(true);
        }
    }


    public void removeTile(Tile tile) {
        tiles.remove(tile);
    }


    public void insertTile(int index, Tile tile) {
        tile.setPalette(palette);
        tiles.add(index, tile);

        setModified(true);
    }

    public int indexOf(Tile tile) {
        return tiles.indexOf(tile);
    }
    
    public int size() {
        return tiles.size();
    }

    public int getTileWidth() {
        if(tiles.size() > 0) {
            return tiles.get(0).getWidth();
        } else {
            return 0;
        }
    }

    public int getTileHeight() {
        if(tiles.size() > 0) {
            return tiles.get(0).getHeight();
        } else {
            return 0;
        }
    }

    @Override
    public Iterator<Tile> iterator() {
        return tiles.iterator();
    }


    /**
     * Check whether the tileset has been modified.
     * @return
     */
    @Override
    public boolean isModified() {
        return super.isModified() || checkTilesModified();
    }

    /**
     * Checks each tile to see if any modifications have been made.
     * @return True if any tile has been modified.
     */
    private boolean checkTilesModified() {
        for(Tile t : tiles) {
            if(t.isModified()) return true;
        }
        
        return false;
    }

}
