
package aspectedit.level;

import aspectedit.resources.AbstractResource;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

/**
 *
 * @author mark
 */
public class Level extends AbstractResource {

    public static final int MAX_WIDTH = 4096/8;
    public static final int MIN_WIDTH = 16;

    /** An array of level widths that are valid for the vanilla Sonic 2 engine. */
    public static final int[] VALID_WIDTHS = new int[] {
        16, 24, 32, 40, 56, 64, 72, 80, 96, 104, 120, 128, 168, 256
    };

    /* The maximum amount of RAM available for level layout data */
    public static final int DATA_ARRAY_LENGTH = 0x1000;

    private int[] data;

    private int width;
    private int height;

    private LevelFormat format = LevelFormat.S2;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Construct a new level with a default size of 168x24 blocks.
     */
    public Level() {
        this(168, 24);
    }

    /**
     * Construct a new level with using the specified dimensions.
     * @param width The width in blocks.
     * @param height The height in blocks.
     */
    public Level(int width, int height) {
        setWidth(width);
        setHeight(height);

        //allocate the array to hold the level data in its entirety
        data = new int[DATA_ARRAY_LENGTH];

        //set each block to mapping 0xFF (blank mapping)
        for (int i = 0; i < data.length; i++) {
            data[i] = 0xFF;
        }

        setModified(true);
    }


    /**
     * Checks that the level's dimensions still fit within the
     * RAM allocated to level layout data and adjusts the {@code height}
     * property if necessary.
     */
    private void checkDimensions() {
        if(width * height > DATA_ARRAY_LENGTH) {
            setHeight(DATA_ARRAY_LENGTH / width);
        }
    }

    /**
     * Get the raw backing array of level data.
     * @return The data array.
     */
    public int[] getData() {
        return data;
    }

    /**
     * Set the raw level data.
     * @param data The data array.
     */
    public void setData(int[] data) {
        final int[] oldData = this.data;

        if(data.length != DATA_ARRAY_LENGTH) {
            data = Arrays.copyOf(data, DATA_ARRAY_LENGTH);
        }
        
        this.data = data;
        setModified(true);

        propertyChangeSupport.firePropertyChange("data", oldData, data);
    }


    /**
     * Get the height of the level in mapping blocks.
     * @return The height.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set the level's height in mapping blocks. Will attempt to
     * preserve as much of the original layout data as possible.
     *
     * @param height The new height.
     */
    public void setHeight(int height) {
        if(height < 1) {
            throw new IllegalArgumentException(String.format("Invalid height: %d", height));
        }
        

        if(this.height != height) {
            final int oldHeight = this.height;

            this.height = height;

            propertyChangeSupport.firePropertyChange("height", oldHeight, height);
            
            checkDimensions();
        }
    }

    /**
     * Get the width of the level in mapping blocks.
     * @return The width.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set the level's width in mapping blocks. Will attempt to
     * preserve as much of the original layout data as possible.
     *
     * @param width The new width.
     */
    public void setWidth(int width) {
        if(width < 1) {
            throw new IllegalArgumentException(String.format("Invalid width: %d", width));
        }

        if(this.width != width) {
            final int oldWidth = this.width;

            this.width = width;
            setModified(true);
            
            propertyChangeSupport.firePropertyChange("width", oldWidth, width);
            
            checkDimensions();
        }

    }

    /**
     * Get the mapping index for the specified position whithin
     * the level data.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @return The mapping index (between 0 and 255).
     */
    public int getMappingValue(int x, int y) {
        //return data[y][x];
        return data[x % width + y * width];
    }

    /**
     * Set the mapping index for the specified position within
     * the level data.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param mappingValue The mapping index.
     */
    public void setMappingValue(int x, int y, int mappingValue) {
        if(mappingValue < 0 || mappingValue > 255) {
            throw new IllegalArgumentException("Mapping index must be between 0 and 255.");
        }

        if(mappingValue != data[x % width + y * width]) {

            data[x % width + y * width] = mappingValue;
            setModified(true);
            
        }
    }


    public LevelFormat getLevelFormat() {
        return format;
    }
    
    public void setLevelFormat(LevelFormat format) {
        if(format == null) {
            throw new IllegalArgumentException("Level format cannot be null.");
        }
        
        this.format = format;
    }
    
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

}
