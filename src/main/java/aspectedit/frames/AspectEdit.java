
/*
 * AspectEdit.java
 *
 * Created on 14 October 2008, 17:00
 */
package aspectedit.frames;

import aspectedit.Config;
import aspectedit.blocks.Blockset;
import aspectedit.frames.action.OpenBlocksetAction;
import aspectedit.frames.action.OpenLevelAction;
import aspectedit.frames.action.OpenPaletteAction;
import aspectedit.frames.action.OpenTilesetAction;
import aspectedit.images.IconManager;
import aspectedit.level.Level;
import aspectedit.palette.GGPalette;
import aspectedit.palette.Palette;
import aspectedit.resources.Resource;
import aspectedit.tiles.Tileset;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 *
 * @author  mark
 */
public class AspectEdit extends JFrame {

    public static final String APP_NAME = "Aspect Edit Plus";
    public static final int VERSION_MAJOR = 0;
    public static final int VERSION_MINOR = 5;
    
    private NewTilesetAction newTilesetAction;
    private NewBlocksetAction newBlocksetAction;
    private NewLevelAction newLevelAction;
    private NewPaletteAction newPaletteAction;
    private OpenTilesetAction openTilesetAction;
    private OpenBlocksetAction openBlocksetAction;
    private OpenLevelAction openLevelAction;
    private OpenPaletteAction openPaletteAction;

    /** Creates new form AspectEdit */
    public AspectEdit() {

        newTilesetAction = new NewTilesetAction();
        newBlocksetAction = new NewBlocksetAction();
        newLevelAction = new NewLevelAction();
        newPaletteAction = new NewPaletteAction();
        openTilesetAction = new OpenTilesetAction("Open Tileset",
                IconManager.getIcon(IconManager.TILESET));
        openBlocksetAction = new OpenBlocksetAction("Open Mappings",
                IconManager.getIcon(IconManager.BLOCKSET));
        openLevelAction = new OpenLevelAction("Open Level",
                IconManager.getIcon(IconManager.LEVEL));
        openPaletteAction = new OpenPaletteAction("Open Palette",
                IconManager.getIcon(IconManager.PALETTE));

        PropertyChangeListener listener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                actionPropertyChanged(evt);
            }
        };

        openTilesetAction.addPropertyChangeListener(listener);
        openBlocksetAction.addPropertyChangeListener(listener);
        openLevelAction.addPropertyChangeListener(listener);
        openPaletteAction.addPropertyChangeListener(listener);

        initComponents();

    }

    @SuppressWarnings("unchecked")
    private void actionPropertyChanged(PropertyChangeEvent evt) {
        if (evt.getSource() == openTilesetAction && "tileset".equals(evt.getPropertyName())) {
            openTilesetEditor((Tileset) evt.getNewValue());

        } else if (evt.getSource() == openBlocksetAction && "blockset".equals(evt.getPropertyName())) {
            openBlocksetEditor((Blockset) evt.getNewValue());

        } else if (evt.getSource() == openLevelAction && "level".equals(evt.getPropertyName())) {
            openLevelEditor((Level) evt.getNewValue());

        } else if(evt.getSource() == openPaletteAction && "palette".equals(evt.getPropertyName())) {
            openPaletteEditor((Palette) evt.getNewValue());
        }
    }


    public void openResourceEditor(Resource resource) {
        if(resource instanceof Tileset) {
            openTilesetEditor((Tileset) resource);
        } else if(resource instanceof Palette) {
            openPaletteEditor((Palette) resource);
        } else if(resource instanceof Blockset) {
            openBlocksetEditor((Blockset) resource);
        } else if(resource instanceof Level) {
            openLevelEditor((Level) resource);
        }
    }

    public void openTilesetEditor(Tileset tileset) {
        if (tileset == null) {
            return;
        }

        TilesetEditorFrame frame = new TilesetEditorFrame(tileset);

        frame.setVisible(true);
        desktop.add(frame, 0);
        desktop.setSelectedFrame(frame);

        openTilesetAction.setTileset(null);
    }

    public void openPaletteEditor(Palette palette) {
        if (palette == null) {
            return;
        }

        PaletteEditorFrame frame = new PaletteEditorFrame(palette);

        frame.setVisible(true);
        desktop.add(frame, 0);
        desktop.setSelectedFrame(frame);

        openPaletteAction.setPalette(null);
    }

    public void openBlocksetEditor(Blockset blockset) {
        if (blockset == null) {
            return;
        }

        BlockEditorFrame frame = new BlockEditorFrame(blockset);

        frame.setVisible(true);
        desktop.add(frame, 0);
        desktop.setSelectedFrame(frame);

        openBlocksetAction.setBlockset(null);
    }

    public void openLevelEditor(Level level) {
        if (level == null) {
            return;
        }

        LevelEditorFrame frame = new LevelEditorFrame(level);

        frame.setVisible(true);
        desktop.add(frame, 0);
        desktop.setSelectedFrame(frame);

        openLevelAction.setLevel(null);
    }

    private class NewTilesetAction extends AbstractAction {

        public NewTilesetAction() {
            super("New Tileset", IconManager.getIcon(IconManager.TILESET_NEW));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            openTilesetEditor(new Tileset());
        }
    }

    private class NewBlocksetAction extends AbstractAction {

        public NewBlocksetAction() {
            super("New Blockset", IconManager.getIcon(IconManager.BLOCKSET_NEW));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            openBlocksetEditor(new Blockset());
        }
    }

    private class NewLevelAction extends AbstractAction {

        public NewLevelAction() {
            super("New Level", IconManager.getIcon(IconManager.LEVEL_NEW));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            openLevelEditor(new Level());
        }
    }

    private class NewPaletteAction extends AbstractAction {

        public NewPaletteAction() {
            super("New Palette", IconManager.getIcon(IconManager.PALETTE_NEW));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if(evt.getSource() == newGGPaletteMenu) {
                openPaletteEditor(new GGPalette());
            } else {
                openPaletteEditor(new Palette());
            }
        }
    }

    private void checkClose() {
        for (JInternalFrame frame : desktop.getAllFrames()) {
            frame.doDefaultCloseAction();
        }

        if (desktop.getAllFrames().length == 0) {
            Config.getInstance().write();
            this.dispose();
            System.exit(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktop = new JDesktopPane();
        jToolBar1 = new JToolBar();
        openPaletteButton = new JButton();
        openTilesetButton = new JButton();
        openBlocksetButton = new JButton();
        openLevelButton = new JButton();
        jMenuBar1 = new JMenuBar();
        fileMenu = new JMenu();
        newMenu = new JMenu();
        newPaletteMenu = new JMenuItem();
        newGGPaletteMenu = new JMenuItem();
        newTilesetMenu = new JMenuItem();
        newBlocksetMenu = new JMenuItem();
        newLevelMenu = new JMenuItem();
        openMenu = new JMenu();
        openPaletteMenu = new JMenuItem();
        openTilesetMenu = new JMenuItem();
        openBlocksetMenu = new JMenuItem();
        openLevelMenu = new JMenuItem();
        jSeparator1 = new JSeparator();
        exitMenu = new JMenuItem();
        jMenu1 = new JMenu();
        aboutMenu = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Aspect Edit Plus");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        desktop.setBackground(new Color(102, 102, 102));

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        openPaletteButton.setAction(openPaletteAction);
        openPaletteButton.setToolTipText(openPaletteAction.getValue(OpenPaletteAction.NAME).toString());
        openPaletteButton.setFocusable(false);
        openPaletteButton.setHideActionText(true);
        openPaletteButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openPaletteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openPaletteButton);

        openTilesetButton.setAction(openTilesetAction);
        openTilesetButton.setToolTipText(openTilesetAction.getValue(OpenTilesetAction.NAME).toString());
        openTilesetButton.setFocusable(false);
        openTilesetButton.setHideActionText(true);
        openTilesetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openTilesetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openTilesetButton);

        openBlocksetButton.setAction(openBlocksetAction);
        openBlocksetButton.setToolTipText(openBlocksetAction.getValue(OpenBlocksetAction.NAME).toString());
        openBlocksetButton.setFocusable(false);
        openBlocksetButton.setHideActionText(true);
        openBlocksetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openBlocksetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openBlocksetButton);

        openLevelButton.setAction(openLevelAction);
        openLevelButton.setToolTipText(openLevelAction.getValue(OpenLevelAction.NAME).toString());
        openLevelButton.setFocusable(false);
        openLevelButton.setHideActionText(true);
        openLevelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openLevelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openLevelButton);

        fileMenu.setText("File");

        newMenu.setText("New...");

        newPaletteMenu.setAction(newPaletteAction);
        newPaletteMenu.setText("SMS Palette");
        newMenu.add(newPaletteMenu);

        newGGPaletteMenu.setAction(newPaletteAction);
        newGGPaletteMenu.setText("GG Palette");
        newMenu.add(newGGPaletteMenu);

        newTilesetMenu.setAction(newTilesetAction);
        newTilesetMenu.setText("Tileset");
        newMenu.add(newTilesetMenu);

        newBlocksetMenu.setAction(newBlocksetAction);
        newBlocksetMenu.setText("Mappings");
        newMenu.add(newBlocksetMenu);

        newLevelMenu.setAction(newLevelAction);
        newLevelMenu.setText("Level");
        newMenu.add(newLevelMenu);

        fileMenu.add(newMenu);

        openMenu.setText("Open...");

        openPaletteMenu.setAction(openPaletteAction);
        openPaletteMenu.setText("Palette");
        openMenu.add(openPaletteMenu);

        openTilesetMenu.setAction(openTilesetAction);
        openTilesetMenu.setText("Tileset");
        openMenu.add(openTilesetMenu);

        openBlocksetMenu.setAction(openBlocksetAction);
        openBlocksetMenu.setText("Mappings");
        openMenu.add(openBlocksetMenu);

        openLevelMenu.setAction(openLevelAction);
        openLevelMenu.setText("Level");
        openMenu.add(openLevelMenu);

        fileMenu.add(openMenu);
        fileMenu.add(jSeparator1);

        exitMenu.setText("Exit");
        exitMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exitMenuActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenu);

        jMenuBar1.add(fileMenu);

        jMenu1.setText("Help");

        aboutMenu.setText("About");
        aboutMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                aboutMenuActionPerformed(evt);
            }
        });
        jMenu1.add(aboutMenu);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
            .addComponent(desktop, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(desktop, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exitMenuActionPerformed
        checkClose();
    }//GEN-LAST:event_exitMenuActionPerformed

    private void aboutMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_aboutMenuActionPerformed
        AboutDialog dlg = new AboutDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_aboutMenuActionPerformed

    private void formWindowClosing(WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        checkClose();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JMenuItem aboutMenu;
    private JDesktopPane desktop;
    private JMenuItem exitMenu;
    private JMenu fileMenu;
    private JMenu jMenu1;
    private JMenuBar jMenuBar1;
    private JSeparator jSeparator1;
    private JToolBar jToolBar1;
    private JMenuItem newBlocksetMenu;
    private JMenuItem newGGPaletteMenu;
    private JMenuItem newLevelMenu;
    private JMenu newMenu;
    private JMenuItem newPaletteMenu;
    private JMenuItem newTilesetMenu;
    private JButton openBlocksetButton;
    private JMenuItem openBlocksetMenu;
    private JButton openLevelButton;
    private JMenuItem openLevelMenu;
    private JMenu openMenu;
    private JButton openPaletteButton;
    private JMenuItem openPaletteMenu;
    private JButton openTilesetButton;
    private JMenuItem openTilesetMenu;
    // End of variables declaration//GEN-END:variables
}
