
package aspectedit.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class RGBCube {

    /** This cube's depth within the octree. */
    private int level;

    /** Maximum depth for the whole octree. */
    private int maxLevel;

    private RGBCube[] children;
    private boolean leaf = false;
    
    private int reds, greens, blues, count;


    public RGBCube(int depth) {
        this(0, depth);
    }


    private RGBCube(int level, int maxLevel) {
        children = new RGBCube[8];
        this.level = level;
        this.maxLevel = maxLevel;
        this.leaf = level == maxLevel;
    }


    /**
     * Get this cube's depth.
     * @return
     */
    public int getLevel() {
        return level;
    }


    /**
     * Get this cube's child cubes.
     * @return
     */
    public RGBCube[] getChildren() {
        return children;
    }


    /**
     * Get the child node at the specified index. If the node does not
     * exist and create is true, the child will be created.
     *
     * @param index The index.
     * @param create Create the node if required.
     * @return
     */
    public RGBCube getChild(int index, boolean create) {
        RGBCube child = children[index];

        if(child == null && level < maxLevel && create) {
            children[index] = child = new RGBCube(level + 1, maxLevel);
        }

        return child;
    }


    /**
     *
     * @return
     */
    public boolean isLeaf() {
        return leaf;
    }


    /**
     * Get the number of leaf nodes within this octree.
     * @return
     */
    public int getLeafCount() {
        RGBCubeWalker<Integer> walker = new RGBCubeWalker<Integer>() {

            @Override
            public void process(RGBCube cube) {
                if(cube.isLeaf()) {
                    yieldValue = yieldValue == null ? 0 : ++yieldValue;
                }
            }

        };

        walker.walk(this);
        return walker.getYieldValue();
    }


    /**
     * Find the next non-leaf node with the smallest count of elements.
     * @return The next non-leaf node.
     */
    public RGBCube getNextReducibleCube() {
        RGBCubeWalker<RGBCube> walker = new RGBCubeWalker<RGBCube>() {

            @Override
            public void process(RGBCube cube) {
                if(!cube.isLeaf()) {
                    if(yieldValue == null || cube.count() < yieldValue.count()) {
                        yieldValue = cube;
                    }
                }
            }

        };

        walker.walk(this);

        return walker.getYieldValue();
    }


    /**
     * Reduce this node.
     */
    public void reduce() {
        count = 0;

        // add each child cube to this one
        for(int i=0; i<children.length; i++) {

            RGBCube child = children[i];

            if(child != null) {
                // if the child node is not a leaf, traverse the
                // tree and reduce as necessary
                if(!child.isLeaf()) child.reduce();

                // add the child's colour values
                reds += child.getReds();
                greens += child.getGreens();
                blues += child.getBlues();
                count += child.count();

                // remove the child cube
                children[i] = null;
            }
        }

        // this cube is now a leaf node
        leaf = true;
    }


    /**
     * Get the average colour for this node.
     *
     * @return The colour
     */
    public Color getColour() {
        if(leaf) {
            // calculate an average colour
            return new Color(reds / count, greens / count, blues / count);

        } else {
            return null;
        }
    }


    /**
     * Get a list of colours for at each leaf node in the octree.
     * @return A list of colours.
     */
    public List<Color> getColours() {
        final ArrayList<Color> colours = new ArrayList<Color>();
        
        new RGBCubeWalker() {

            @Override
            public void process(RGBCube cube) {
                // if the node is a leaf add its colour value
                // to the list
                if(cube.isLeaf()) colours.add(cube.getColour());
            }

        }.walk(this);
        
        return colours;
    }


    /**
     * Get the quantised colour for the supplied components.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return The closest possible colour to [r,g,b] within the cube.
     */
    public Color getQuantisedColour(int r, int g, int b) {
        if(leaf) {
            return getColour();

        } else {
            //traverse the octree to get the leaf node
            RGBCube child = getChild(indexForLevel(level, r, g, b), true);

            return child.getQuantisedColour(r, g, b);
        }
    }


    /**
     * Add the supplied colour to the cube.
     *
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     */
    public void addColour(int r, int g, int b) {
        addColour(0, r, g, b);
    }


    /**
     * Add a colour to a cube at the specified depth.
     * @param level The depth.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     */
    protected void addColour(int level, int r, int g, int b) {
        if(leaf) {
            reds += r;
            greens += g;
            blues += b;
            ++count;

        } else {
            RGBCube child = getChild(indexForLevel(level, r, g, b), true);

            ++count;

            child.addColour(level+1, r, g, b);
        }
    }


    /**
     * Get the sum of the elements' red components.
     * @return
     */
    public int getReds() {
        return reds;
    }


    /**
     * Get the sum of the elements' green components.
     * @return
     */
    public int getGreens() {
        return greens;
    }


    /**
     * Get the sum of the elements' blue components.
     * @return
     */
    public int getBlues() {
        return blues;
    }


    /**
     * Get the number of elements at this node.
     * @return
     */
    public int count() {
        return count;
    }


    /**
     * Get the child node index for a given level.
     * @param level The level.
     * @param r The red component.
     * @param g The green component.
     * @param b The blue component.
     * @return
     */
    protected int indexForLevel(int level, int r, int g, int b) {
        // combine the "level"-th bit (counting from the MSB) of each 
        // component to get the index
        int index = (b >> 7 - level & 1)
                | ((g >> 7 - level & 1) << 1)
                | ((r >> 7 - level & 1) << 2);

        return index;
    }


    /**
     * A class to simplify preorder traversal of the octree.
     * @param <T>
     */
    public abstract class RGBCubeWalker<T> {

        /** May be used by subclasses to yield a value from the process. */
        protected T yieldValue;

        /**
         * Traverse the cube (preorder).
         * @param cube The cube.
         */
        public void walk(RGBCube cube) {
            process(cube);

            for(RGBCube child : cube.getChildren()) {
                if(child != null) {
                    walk(child);
                }
            }
        }

        /**
         * Yield value accessor.
         * @return The value.
         */
        public T getYieldValue() {
            return yieldValue;
        }

        /**
         * Must be overriden by subclasses to perform actions on
         * the node.
         * @param cube The octree node.
         */
        public abstract void process(RGBCube cube);

    }
}
