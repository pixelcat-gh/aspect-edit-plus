
package aspectedit.frames;

import aspectedit.blocks.Blockset;
import aspectedit.components.ComboBoxNumberEditor;
import aspectedit.components.tileview.DefaultTileViewCellRenderer;
import aspectedit.components.tileview.TileSelectionEvent;
import aspectedit.components.tileview.TileSelectionListener;
import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.adapters.LevelViewAdapter;
import aspectedit.components.tileview.adapters.MappingViewAdapter;
import aspectedit.frames.action.*;
import aspectedit.images.IconManager;
import aspectedit.level.Level;
import aspectedit.palette.Palette;
import aspectedit.tiles.Tileset;
import aspectedit.util.AsyncActionBuilder;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 */
public class LevelEditorFrame
        extends JInternalFrame
        implements PropertyChangeListener {


    /* Actions */
    private OpenLevelAction openAction;
    private SaveLevelAction saveAction;
    private SaveLevelAsAction saveAsAction;
    private OpenBlocksetAction openBlocksetAction;
    private OpenTilesetAction openTilesetAction;
    private OpenPaletteAction openFgPaletteAction;
    private OpenPaletteAction openBgPaletteAction;
    private ExportToImageAction exportAction;
    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;
    private ZoomInAction zoomInAction2;
    private ZoomOutAction zoomOutAction2;

    /* Properties */
    private Level level;
    private Blockset blockset;
    private Tileset tileset;
    private Palette fgPalette;
    private Palette bgPalette;

    /* Components */
    private MappingViewAdapter mappingAdapter;
    private LevelViewAdapter levelAdapter;
    private DefaultTileViewCellRenderer mappingRenderer;
    private DefaultTileViewCellRenderer levelRenderer;

    private PropertyChangeListener levelListener;


    public LevelEditorFrame() {
        this(new Level());
    }

    /** Creates new form LevelEditorFrame */
    public LevelEditorFrame(Level level) {

        buildActions();

        mappingAdapter = new MappingViewAdapter();
        levelAdapter = new LevelViewAdapter();

        mappingRenderer = new DefaultTileViewCellRenderer();
        levelRenderer = new DefaultTileViewCellRenderer();
        
        initComponents();

        exportAction.setTileView(levelView);

        // create the level property listener
        levelListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                levelPropertyChanged(evt);
            }
        };

        levelView.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {}

            @Override
            public void mouseMoved(MouseEvent e) {
                levelViewMouseMoved(e);
            }
        });
        

        setLevel(level);

        zoomInAction.setTileView(levelView);
        zoomOutAction.setTileView(levelView);
        zoomOutAction.setEnabled(false);

        zoomInAction2.setTileView(mappingView);
        zoomOutAction2.setTileView(mappingView);
        zoomOutAction2.setEnabled(false);
    }


    private void buildActions() {
        openAction = AsyncActionBuilder.buildAction(OpenLevelAction.class)
                .withPropertyListeners(this).build();

        openBlocksetAction = AsyncActionBuilder.buildAction(OpenBlocksetAction.class)
                .icon(IconManager.getIcon(IconManager.BLOCKSET))
                .withPropertyListeners(this).build();
        openBlocksetAction.setWarnIfModified(false);
        
        openTilesetAction = AsyncActionBuilder.buildAction(OpenTilesetAction.class)
                .icon(IconManager.getIcon(IconManager.TILESET))
                .withPropertyListeners(this).build();
        openTilesetAction.setWarnIfModified(false);
        
        openBgPaletteAction = AsyncActionBuilder.buildAction(OpenPaletteAction.class)
                .icon(IconManager.getIcon(IconManager.PALETTE))
                .withPropertyListeners(this).build();

        openFgPaletteAction = AsyncActionBuilder.buildAction(OpenPaletteAction.class)
                .icon(IconManager.getIcon(IconManager.PALETTE))
                .withPropertyListeners(this).build();

        exportAction = new ExportToImageAction();
        saveAction = new SaveLevelAction();
        saveAsAction = new SaveLevelAsAction();

        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();

        zoomInAction2 = new ZoomInAction();
        zoomOutAction2 = new ZoomOutAction();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Object source = evt.getSource();

        //despatch to the relevant method
        if(source == openAction) {
            levelChanged(evt);

        } else if(source == openBgPaletteAction) {
            bgPaletteChanged(evt);

        } else if(source == openFgPaletteAction) {
            fgPaletteChanged(evt);

        } else if(source == openTilesetAction) {
            tilesetChanged(evt);

        } else if(source == openBlocksetAction) {
            blocksetChanged(evt);

        }
    }



    //<editor-fold defaultstate="collapsed" desc="Action Property Listeners">
    private void levelChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Level) {
            setLevel( (Level) evt.getNewValue() );
        }
    }
    
    private void blocksetChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Blockset) {
            setBlockset( (Blockset) evt.getNewValue() );
        }
    }

    private void tilesetChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Tileset) {
            setTileset( (Tileset) evt.getNewValue() );
        }
    }

    private void fgPaletteChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Palette) {
            setFgPalette( (Palette) evt.getNewValue() );
        }
    }

    private void bgPaletteChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Palette) {
            setBgPalette( (Palette) evt.getNewValue() );
        }
    }

    //</editor-fold>


    /**
     * Listens to PropertyChangeEvents generated by the level. This will
     * update the widthCombo and heightSpinner components and enforce
     * level dimensions.
     *
     * @param evt The PropertyChangeEvent
     */
    private void levelPropertyChanged(PropertyChangeEvent evt) {
        if("width".equals(evt.getPropertyName())) {
            widthCombo.setSelectedItem(level.getWidth());

        } else if("height".equals(evt.getPropertyName())) {
            heightSpinner.setValue(level.getHeight());
            
        }
    }

    private void tileSelectionChanged(TileSelectionEvent evt) {
        if(evt.getSource() == mappingView) {
            levelView.setEditTileIndex(evt.getIndex());
        }
    }


    private void levelViewMouseMoved(MouseEvent evt) {
        int index = levelView.getTileViewLayout().getTileIndexFromPoint(levelView, levelAdapter, evt.getPoint());

        tileXLabel.setText(String.valueOf(index % level.getWidth()));
        tileYLabel.setText(String.valueOf(index / level.getWidth()));
        mouseXLabel.setText(String.valueOf(evt.getX()));
        mouseYLabel.setText(String.valueOf(evt.getY()));
    }

    //<editor-fold defaultstate="collapsed" desc="Accessor/Mutators">
    public Palette getBgPalette() {
        return bgPalette;
    }

    public void setBgPalette(Palette bgPalette) {
        if(bgPalette == null) {
            throw new IllegalArgumentException("BG Palette cannot be null.");
        }
        
        this.bgPalette = bgPalette;

        if(tileset != null) tileset.setPalette(bgPalette);
        if(blockset != null) blockset.setBgPalette(bgPalette);

        levelView.repaint();
        mappingView.repaint();
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        if(blockset == null)
            throw new IllegalArgumentException("Blockset cannot be null.");
        
        this.blockset = blockset;

        if(bgPalette != null) blockset.setBgPalette(bgPalette);
        if(fgPalette != null) blockset.setFgPalette(fgPalette);
        if(tileset != null) blockset.setTileset(tileset);
        blockset.setTileOffset( (Integer) tileOffsetSpinner.getValue());

        levelAdapter.setBlockset(blockset);
        mappingAdapter.setBlockset(blockset);
        levelView.setEditingModel(mappingAdapter);

        levelView.repaint();
        mappingView.repaint();
    }

    public Palette getFgPalette() {
        return fgPalette;
    }

    public void setFgPalette(Palette fgPalette) {
        if(fgPalette == null) {
            throw new IllegalArgumentException("FG Palette cannot be null.");
        }
        
        this.fgPalette = fgPalette;

        if(blockset != null) {
            blockset.setFgPalette(fgPalette);

            levelView.repaint();
            mappingView.repaint();
        }

    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if(level == null) {
            throw new IllegalArgumentException("Level cannot be null.");
        }

        if(this.level != null) {
            this.level.removePropertyChangeListener(levelListener);
        }

        this.level = level;

        this.level.addPropertyChangeListener(levelListener);
        
        levelAdapter.setLevel(level);
        saveAction.setLevel(level);
        saveAsAction.setLevel(level);
        openAction.setLevel(level);

        heightSpinner.setValue(level.getHeight());
        widthCombo.setSelectedItem(level.getWidth());
    }

    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        if(tileset == null) {
            throw new IllegalArgumentException("Tileset cannot be null.");
        }

        this.tileset = tileset;

        if(bgPalette != null) tileset.setPalette(bgPalette);
        if(blockset != null) blockset.setTileset(tileset);

        levelView.repaint();
        mappingView.repaint();
        
    }

    //</editor-fold>


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        jToolBar1 = new JToolBar();
        openLevelButton = new JButton();
        saveLevelButton = new JButton();
        saveLevelAsButton = new JButton();
        exportImageButton = new JButton();
        jSeparator1 = new Separator();
        openBlocksetButton = new JButton();
        openTilesetButton = new JButton();
        openFgPaletteButton = new JButton();
        openBgPaletteButton = new JButton();
        jSplitPane1 = new JSplitPane();
        jScrollPane2 = new JScrollPane();
        levelView = new TileView();
        jTabbedPane1 = new JTabbedPane();
        jScrollPane1 = new JScrollPane();
        mappingView = new TileView();
        jPanel1 = new JPanel();
        jPanel2 = new JPanel();
        jLabel1 = new JLabel();
        tileOffsetSpinner = new JSpinner();
        jPanel3 = new JPanel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        heightSpinner = new JSpinner();
        widthCombo = new JComboBox();
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(int i : Level.VALID_WIDTHS) {
            model.addElement(i);
        }

        widthCombo.setModel(model);
        showMappingNumbersCheck = new JCheckBox();
        showLevelNumbersCheck = new JCheckBox();
        showGridCheck = new JCheckBox();
        jPanel4 = new JPanel();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel6 = new JLabel();
        jLabel7 = new JLabel();
        jLabel8 = new JLabel();
        jLabel9 = new JLabel();
        mouseXLabel = new JLabel();
        mouseYLabel = new JLabel();
        tileXLabel = new JLabel();
        tileYLabel = new JLabel();

        jSeparator2 = new Separator();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton2 = new javax.swing.JButton();
        zoomOutButton2 = new javax.swing.JButton();
        jSeparator3 = new Separator();

        setClosable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Level Editor");
        setMinimumSize(new Dimension(320, 240));
        setPreferredSize(new Dimension(800, 720));
        addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameActivated(InternalFrameEvent evt) {
            }
            public void internalFrameClosed(InternalFrameEvent evt) {
            }
            public void internalFrameClosing(InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(InternalFrameEvent evt) {
            }
            public void internalFrameIconified(InternalFrameEvent evt) {
            }
            public void internalFrameOpened(InternalFrameEvent evt) {
            }
        });

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        openLevelButton.setAction(openAction);
        openLevelButton.setToolTipText(openAction.getValue(OpenLevelAction.NAME).toString());
        openLevelButton.setFocusable(false);
        openLevelButton.setHideActionText(true);
        openLevelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openLevelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openLevelButton);

        saveLevelButton.setAction(saveAction);
        saveLevelButton.setToolTipText(saveAction.getValue(SaveLevelAction.NAME).toString());
        saveLevelButton.setFocusable(false);
        saveLevelButton.setHideActionText(true);
        saveLevelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveLevelButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(saveLevelButton);

        saveLevelAsButton.setAction(saveAsAction);
        saveLevelAsButton.setToolTipText(saveAsAction.getValue(SaveLevelAsAction.NAME).toString());
        saveLevelAsButton.setFocusable(false);
        saveLevelAsButton.setHideActionText(true);
        saveLevelAsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveLevelAsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(saveLevelAsButton);

        exportImageButton.setAction(exportAction);
        exportImageButton.setToolTipText(exportAction.getValue(ExportToImageAction.NAME).toString());
        exportImageButton.setFocusable(false);
        exportImageButton.setHideActionText(true);
        exportImageButton.setHorizontalTextPosition(SwingConstants.CENTER);
        exportImageButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(exportImageButton);
        jToolBar1.add(jSeparator1);

        openBlocksetButton.setAction(openBlocksetAction);
        openBlocksetButton.setToolTipText(openBlocksetAction.getValue(OpenBlocksetAction.NAME).toString());
        openBlocksetButton.setFocusable(false);
        openBlocksetButton.setHideActionText(true);
        openBlocksetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openBlocksetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openBlocksetButton);

        openTilesetButton.setAction(openTilesetAction);
        openTilesetButton.setToolTipText(openTilesetAction.getValue(OpenTilesetAction.NAME).toString());
        openTilesetButton.setFocusable(false);
        openTilesetButton.setHideActionText(true);
        openTilesetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openTilesetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openTilesetButton);

        openFgPaletteButton.setAction(openFgPaletteAction);
        openFgPaletteButton.setToolTipText("Open Foreground Palette");
        openFgPaletteButton.setFocusable(false);
        openFgPaletteButton.setHideActionText(true);
        openFgPaletteButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openFgPaletteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openFgPaletteButton);

        openBgPaletteButton.setAction(openBgPaletteAction);
        openBgPaletteButton.setToolTipText("Open Background Palette");
        openBgPaletteButton.setFocusable(false);
        openBgPaletteButton.setHideActionText(true);
        openBgPaletteButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openBgPaletteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openBgPaletteButton);

        jToolBar1.add(jSeparator2);

        jLabel8.setText("Editor zoom:");
        jToolBar1.add(jLabel8);

        zoomInButton.setAction(zoomInAction);
        zoomInButton.setToolTipText(zoomInAction.getValue(ZoomInAction.NAME).toString());
        zoomInButton.setFocusable(false);
        zoomInButton.setHideActionText(true);
        zoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomInButton);

        zoomOutButton.setAction(zoomOutAction);
        zoomOutButton.setToolTipText(zoomOutAction.getValue(ZoomOutAction.NAME).toString());
        zoomOutButton.setFocusable(false);
        zoomOutButton.setHideActionText(true);
        zoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomOutButton);

        jToolBar1.add(jSeparator3);

        jLabel9.setText("Mapping zoom:");
        jToolBar1.add(jLabel9);

        zoomInButton2.setAction(zoomInAction2);
        zoomInButton2.setToolTipText(zoomInAction2.getValue(ZoomInAction.NAME).toString());
        zoomInButton2.setFocusable(false);
        zoomInButton2.setHideActionText(true);
        zoomInButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomInButton2);

        zoomOutButton2.setAction(zoomOutAction2);
        zoomOutButton2.setToolTipText(zoomOutAction2.getValue(ZoomOutAction.NAME).toString());
        zoomOutButton2.setFocusable(false);
        zoomOutButton2.setHideActionText(true);
        zoomOutButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(zoomOutButton2);

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setResizeWeight(0.9);

        levelView.setCellRenderer(levelRenderer);
        levelView.setMode(TileView.MODE_EDIT);
        levelView.setModel(levelAdapter);

        GroupLayout levelViewLayout = new GroupLayout(levelView);
        levelView.setLayout(levelViewLayout);
        levelViewLayout.setHorizontalGroup(
            levelViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 255, Short.MAX_VALUE)
        );
        levelViewLayout.setVerticalGroup(
            levelViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(levelView);

        jSplitPane1.setLeftComponent(jScrollPane2);

        mappingView.setCellRenderer(mappingRenderer);
        mappingView.setModel(mappingAdapter);
        mappingView.addTileSelectionListener(new TileSelectionListener() {
            @Override
            public void notifyTileSelected(TileSelectionEvent evt) {
                tileSelectionChanged(evt);
            }
        });

        GroupLayout mappingViewLayout = new GroupLayout(mappingView);
        mappingView.setLayout(mappingViewLayout);
        mappingViewLayout.setHorizontalGroup(
            mappingViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );
        mappingViewLayout.setVerticalGroup(
            mappingViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(mappingView);

        jTabbedPane1.addTab("Mappings", jScrollPane1);

        jPanel2.setBorder(BorderFactory.createTitledBorder("Blockset Properties"));

        jLabel1.setText("Tile Offset:");

        tileOffsetSpinner.setModel(new SpinnerNumberModel(256, 0, 447, 1));
        tileOffsetSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                tileOffsetSpinnerStateChanged(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(tileOffsetSpinner, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tileOffsetSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(BorderFactory.createTitledBorder("Level Properites"));

        jLabel2.setText("Width:");

        jLabel3.setText("Height:");

        heightSpinner.setModel(new SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        heightSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                heightSpinnerStateChanged(evt);
            }
        });

        widthCombo.setEditable(true);
        widthCombo.setEditor(new ComboBoxNumberEditor());
        widthCombo.setPreferredSize(new Dimension(57, 20));
        widthCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent evt) {
                widthComboItemStateChanged(evt);
            }
        });

        showMappingNumbersCheck.setText("Show numbers in mapping palette");
        showMappingNumbersCheck.addActionListener(this::showMappingNumbersCheckActionPerformed);

        showLevelNumbersCheck.setText("Show numbers in level view");
        showLevelNumbersCheck.addActionListener(this::showLevelNumbersCheckActionPerformed);

        showGridCheck.setSelected(true);
        showGridCheck.setText("Show grid lines");
        showGridCheck.addActionListener(this::showGridCheckActionPerformed);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(widthCombo, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE))
                    .addComponent(showMappingNumbersCheck)
                    .addComponent(showLevelNumbersCheck)
                    .addComponent(showGridCheck))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(widthCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(heightSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(showMappingNumbersCheck)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(showLevelNumbersCheck)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(showGridCheck)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(BorderFactory.createTitledBorder("Info"));
        jPanel4.setLayout(new GridBagLayout());

        jLabel4.setText("Mouse X:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(4, 8, 4, 4);
        jPanel4.add(jLabel4, gridBagConstraints);

        jLabel5.setText("Mouse Y:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(4, 8, 4, 4);
        jPanel4.add(jLabel5, gridBagConstraints);

        jLabel6.setText("Tile X:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.insets = new Insets(4, 8, 4, 4);
        jPanel4.add(jLabel6, gridBagConstraints);

        jLabel7.setText("Tile Y:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(4, 8, 4, 4);
        jPanel4.add(jLabel7, gridBagConstraints);

        mouseXLabel.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        jPanel4.add(mouseXLabel, gridBagConstraints);

        mouseYLabel.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        jPanel4.add(mouseYLabel, gridBagConstraints);

        tileXLabel.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        jPanel4.add(tileXLabel, gridBagConstraints);

        tileYLabel.setText("0");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(4, 4, 4, 4);
        jPanel4.add(tileYLabel, gridBagConstraints);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Properties", jPanel1);

        jSplitPane1.setRightComponent(jTabbedPane1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
            .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tileOffsetSpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_tileOffsetSpinnerStateChanged
        if(tileOffsetSpinner.getValue() instanceof Integer) {
            blockset.setTileOffset( (Integer) tileOffsetSpinner.getValue());

            levelView.repaint();
            mappingView.repaint();
        }
    }//GEN-LAST:event_tileOffsetSpinnerStateChanged

    private void widthComboItemStateChanged(ItemEvent evt) {//GEN-FIRST:event_widthComboItemStateChanged
        Object item = widthCombo.getSelectedItem();

        if(item instanceof Integer) {
            int value = (Integer) item;
            value = Math.max(Math.min(value, Level.MAX_WIDTH), Level.MIN_WIDTH);

            level.setWidth(value);
            levelView.revalidate();
            levelView.repaint();
        }
    }//GEN-LAST:event_widthComboItemStateChanged

    private void heightSpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_heightSpinnerStateChanged
        Object item = heightSpinner.getValue();

        if(item instanceof Integer) {
            level.setHeight( (Integer) item );
            levelView.revalidate();
            levelView.repaint();
        }
    }//GEN-LAST:event_heightSpinnerStateChanged

    private void showMappingNumbersCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showMappingNumbersCheckActionPerformed
        mappingRenderer.setDrawIndex(showMappingNumbersCheck.isSelected());
        mappingView.repaint();
    }//GEN-LAST:event_showMappingNumbersCheckActionPerformed

    private void showLevelNumbersCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showLevelNumbersCheckActionPerformed
        levelRenderer.setDrawIndex(showLevelNumbersCheck.isSelected());
        levelView.repaint();
    }//GEN-LAST:event_showLevelNumbersCheckActionPerformed

    private void formInternalFrameClosing(InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if(level.isModified()) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "The level has been modified.\nDo you want to save the changes?",
                    AspectEdit.APP_NAME,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if(result == JOptionPane.YES_OPTION) {
                saveAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "save"));
            } else if(result == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        dispose();
    }//GEN-LAST:event_formInternalFrameClosing

    private void showGridCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showGridCheckActionPerformed
        levelRenderer.setDrawBorder(showGridCheck.isSelected());
        mappingRenderer.setDrawBorder(showGridCheck.isSelected());
        levelView.repaint();
        mappingView.repaint();
    }//GEN-LAST:event_showGridCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton exportImageButton;
    private JSpinner heightSpinner;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private Separator jSeparator1;
    private Separator jSeparator2;
    private Separator jSeparator3;
    private JSplitPane jSplitPane1;
    private JTabbedPane jTabbedPane1;
    private JToolBar jToolBar1;
    private TileView levelView;
    private TileView mappingView;
    private JLabel mouseXLabel;
    private JLabel mouseYLabel;
    private JButton openBgPaletteButton;
    private JButton openBlocksetButton;
    private JButton openFgPaletteButton;
    private JButton openLevelButton;
    private JButton openTilesetButton;
    private JButton saveLevelAsButton;
    private JButton saveLevelButton;
    private JCheckBox showGridCheck;
    private JCheckBox showLevelNumbersCheck;
    private JCheckBox showMappingNumbersCheck;
    private JSpinner tileOffsetSpinner;
    private JLabel tileXLabel;
    private JLabel tileYLabel;
    private JComboBox widthCombo;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JButton zoomInButton2;
    private javax.swing.JButton zoomOutButton2;
    // End of variables declaration//GEN-END:variables

}
