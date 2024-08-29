
package aspectedit.frames;

import aspectedit.blocks.Block;
import aspectedit.blocks.Blockset;
import aspectedit.components.tileview.*;
import aspectedit.components.tileview.adapters.*;
import aspectedit.components.tileview.layout.TileViewGridLayout;
import aspectedit.frames.action.*;
import aspectedit.images.IconManager;
import aspectedit.tiles.Tileset;
import aspectedit.palette.Palette;
import aspectedit.tiles.Tile;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JToolBar.Separator;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 *
 * @author mark
 */
public class BlockEditorFrame
        extends javax.swing.JInternalFrame
        implements TileSelectionListener {

    /* Actions */
    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;
    private ZoomInAction zoomInAction2;
    private ZoomOutAction zoomOutAction2;
    private ZoomInAction zoomInAction3;
    private ZoomOutAction zoomOutAction3;
    private OpenBlocksetAction openBlocksetAction;
    private OpenTilesetAction openTilesetAction;
    private OpenPaletteAction openBgPaletteAction;
    private OpenPaletteAction openFgPaletteAction;
    private SaveBlocksetAsAction saveAsAction;
    private SaveBlocksetAction saveAction;
    private SaveBlocksetAsAssemblyAction saveAssemblyAction;
    private AddTileAction addBlockAction;
    private RemoveTileAction removeBlockAction;
    private ExportToImageAction exportImageAction;

    private MappingViewAdapter mappingAdapter;
    private BlocksetViewAdapter blocksetAdapter;
    private TilesetViewAdapter tilesetAdapter;
    private BlockEditorAdapter editorAdapter;

    private DefaultTileViewCellRenderer mappingRenderer;
    private DefaultTileViewCellRenderer blocksetRenderer;

    private Blockset blockset;
    private Tileset tileset;
    private Palette fgPalette;
    private Palette bgPalette;
    private int tileOffset = 256;

    /** Creates new form BlockEditorFrame */
    public BlockEditorFrame() {
        this(new Blockset());
    }


    public BlockEditorFrame(Blockset blockset) {
        // create the actions
        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();
        zoomInAction2 = new ZoomInAction();
        zoomOutAction2 = new ZoomOutAction();
        zoomInAction3 = new ZoomInAction();
        zoomOutAction3 = new ZoomOutAction();
        openBlocksetAction = new OpenBlocksetAction();
        openTilesetAction = new OpenTilesetAction("Open Tileset", IconManager.getIcon(IconManager.TILESET));
        openBgPaletteAction = new OpenPaletteAction("Open BG Palette", IconManager.getIcon(IconManager.PALETTE));
        openFgPaletteAction = new OpenPaletteAction("Open FG Palette", IconManager.getIcon(IconManager.PALETTE));
        saveAsAction = new SaveBlocksetAsAction();
        saveAction = new SaveBlocksetAction();
        saveAssemblyAction = new SaveBlocksetAsAssemblyAction();
        addBlockAction = new AddTileAction("Add Block",
                IconManager.getIcon(IconManager.ADD));
        removeBlockAction = new RemoveTileAction("Remove Selected Block",
                IconManager.getIcon(IconManager.DELETE));
        exportImageAction = new ExportToImageAction("Export Mappings as Image",
                IconManager.getIcon(IconManager.EXPORT_IMAGE));

        // create the TileView adapters
        mappingAdapter = new MappingViewAdapter();
        blocksetAdapter = new BlocksetViewAdapter();
        tilesetAdapter = new TilesetViewAdapter();
        editorAdapter = new BlockEditorAdapter();


        mappingRenderer = new DefaultTileViewCellRenderer();
        mappingRenderer.setDrawIndex(true);

        blocksetRenderer = new DefaultTileViewCellRenderer();

        initComponents();

        exportImageAction.setTileView(mappingView);

        addBlockAction.setTileView(blockView);
        removeBlockAction.setTileView(blockView);
        zoomInAction.setTileView(blockView);
        zoomOutAction.setTileView(blockView);
        zoomOutAction.setEnabled(false);

        zoomInAction2.setTileView(tilesetView);
        zoomOutAction2.setTileView(tilesetView);

        zoomInAction3.setTileView(mappingView);
        zoomOutAction3.setTileView(mappingView);
        zoomOutAction3.setEnabled(false);

        // add property listeners to each of the actions
        openBlocksetAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                blocksetChangedEvent(evt);
            }
        });

        openTilesetAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                tilesetChangedEvent(evt);
            }
        });

        openBgPaletteAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                bgPaletteChangedEvent(evt);
            }
        });

        openFgPaletteAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                fgPaletteChangedEvent(evt);
            }
        });

        if(blockset != null) {
            setBlockset(blockset);
        }
    }


    //<editor-fold defaultstate="collapsed" desc="Action Property Change Handlers">
    private void blocksetChangedEvent(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Blockset) {
            setBlockset((Blockset) evt.getNewValue());
        }
    }

    private void tilesetChangedEvent(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Tileset) {
            setTileset((Tileset) evt.getNewValue());
        }
    }

    private void bgPaletteChangedEvent(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Palette) {
            setBgPalette((Palette) evt.getNewValue());
        }
    }

    private void fgPaletteChangedEvent(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Palette) {
            setFgPalette((Palette) evt.getNewValue());
        }
    }
    //</editor-fold>


    /**
     *
     * @param evt
     */
    @Override
    public void notifyTileSelected(TileSelectionEvent evt) {

        // use the source of the event to determine the course of action
        if(evt.getSource() == blockView && evt.getTile() instanceof Block) {

            // the event came from the block view component - update the block
            // editor with the selected Block
            blockEditor.setSelectedTileIndex(0);
			blockEditor.setMode(TileView.MODE_SELECT);
            blockEditorSelectModeMenu.setSelected(true);

			editorAdapter.setBlock((Block) evt.getTile());

            mappingView.setEditTileIndex(evt.getIndex());

        } else if(evt.getSource() == tilesetView && evt.getTile() instanceof Tile) {

            // the event came from the tileset view component - set the edit tile
            // on the block editor
            Tile tile = (Tile) evt.getTile();
            blockEditor.setEditTileIndex(tileset.indexOf(tile));

        } else if(evt.getSource() == blockEditor) {
            updateCheckBoxComponents();
        }
    }


    /**
     * Gets the coordiate of the selected element in the block editor.
     * @return
     */
    private Point getSelectedBlockElement() {
        int index = blockEditor.getSelectedTileIndex();

        int blockX = index % 4;
        int blockY = index / 4;

        return new Point(blockX, blockY);
    }


    /**
     * Updates the state of each checkbox that is associated with
     * the block editor.
     */
    private void updateCheckBoxComponents() {

        if(blocksetAdapter.size() > 0) {
            Block block = editorAdapter.getBlock();
            Point p = getSelectedBlockElement();

            flipXCheck.setSelected(block.isFlipHorizontal(p.x, p.y));
            flipYCheck.setSelected(block.isFlipVertical(p.x, p.y));
            useFgPaletteCheck.setSelected(block.isUseSpritePalette(p.x, p.y));
            highPriorityCheck.setSelected(block.isHighPriority(p.x, p.y));

        } else {
            flipXCheck.setSelected(false);
            flipYCheck.setSelected(false);
            useFgPaletteCheck.setSelected(false);
            highPriorityCheck.setSelected(false);
        }
    }


    /**
     * Update the VRAM address label with the value of the
     * blockset's tile offset property.
     * @param value The tile offset value.
     */
    private void updateVramAddressLabel() {
        vramAddressLabel.setText(String.format("(VRAM: $%X)",
                tileOffset*32 ));
    }


    //<editor-fold defaultstate="collapsed" desc="Accessor/Mutators">
    public Tileset getTileset() {
        return tileset;
    }

    public void setTileset(Tileset tileset) {
        if(tileset == null) {
            throw new IllegalArgumentException("Tileset cannot be null.");
        }

        this.tileset = tileset;

        if(bgPalette != null && tileset.getPalette() == Palette.BLANK_PALETTE) {
            tileset.setPalette(bgPalette);
        }

        tilesetAdapter.setTileset(tileset);

        if(blockset != null) {
            blockset.setTileset(tileset);

            blockView.repaint();
            mappingView.repaint();
        }

        tilesetView.repaint();
    }


    public Palette getBgPalette() {
        return bgPalette;
    }

    public void setBgPalette(Palette bgPalette) {
        if(bgPalette == null) {
            throw new IllegalArgumentException("BgPalette cannot be null.");
        }

        this.bgPalette = bgPalette;

        if(tileset != null) {
            tileset.setPalette(bgPalette);
        }

        if(blockset != null) {
            blockset.setBgPalette(bgPalette);
        }

        blockView.repaint();
        mappingView.repaint();
        tilesetView.repaint();
    }


    public Palette getFgPalette() {
        return fgPalette;
    }

    public void setFgPalette(Palette fgPalette) {
        if(fgPalette == null) {
            throw new IllegalArgumentException("FgPalette cannot be null.");
        }

        this.fgPalette = fgPalette;

        blockset.setFgPalette(fgPalette);

        blockView.repaint();
        mappingView.repaint();
    }

    public Blockset getBlockset() {
        return blockset;
    }

    public void setBlockset(Blockset blockset) {
        if(blockset == null) {
            throw new IllegalArgumentException("Blockset cannot be null");
        }

        this.blockset = blockset;

        // set the tileset if the blockset's tileset is null
        if(tileset != null && blockset.getTileset() == Tileset.EMPTY_TILESET) {
            blockset.setTileset(tileset);
        }

        // set the fg palette if the blockset's palette is null
        if(fgPalette != null && blockset.getFgPalette() == Palette.BLANK_PALETTE) {
            blockset.setFgPalette(fgPalette);
        }

        // set the bg palette if the blockset's palette is null
        if(bgPalette != null && blockset.getBgPalette() == Palette.BLANK_PALETTE) {
            blockset.setBgPalette(bgPalette);
        }

        // update the tile offset property
        if(blockset.getTileOffset() == 0) {
            blockset.setTileOffset(tileOffset);
        } else {
            setTileOffset(blockset.getTileOffset());
        }


        // update the block editor
        if(blockset.size() > 0) {
            blockView.setSelectedTileIndex(0);
        } else {
            editorAdapter.setBlock(Block.EMPTY_BLOCK);
        }


        updateCheckBoxComponents();

        mappingAdapter.setBlockset(blockset);
        blocksetAdapter.setBlockset(blockset);

        saveAsAction.setBlockset(blockset);
        saveAction.setBlockset(blockset);
        saveAssemblyAction.setBlockset(blockset);
    }


    protected int getTileOffset() {
        return tileOffset;
    }

    protected void setTileOffset(int tileOffset) {
        this.tileOffset = tileOffset;
        updateVramAddressLabel();

        if(blockset != null) {
            blockset.setTileOffset(tileOffset);
            blockView.repaint();
            mappingView.repaint();
        }
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

        blockEditorPopup = new JPopupMenu();
        blockEditorEditModeMenu = new JRadioButtonMenuItem();
        blockEditorSelectModeMenu = new JRadioButtonMenuItem();
        editorModeButtonGroup = new ButtonGroup();
        mappingViewPopup = new JPopupMenu();
        mappingViewEditModeMenu = new JRadioButtonMenuItem();
        mappingViewSelectModeMenu = new JRadioButtonMenuItem();
        mappingViewModeButtonGroup = new ButtonGroup();
        jToolBar1 = new JToolBar();
        openBlocksetButton = new JButton();
        saveBlocksetButton = new JButton();
        saveAsButton = new JButton();
        saveAssemblyButton = new JButton();
        exportImageButton = new JButton();
        jSeparator2 = new Separator();
        openTilesetButton = new JButton();
        openFgPaletteButton = new JButton();
        openBgPaletteButton = new JButton();
        jSeparator3 = new Separator();
        addBlockButton = new JButton();
        removeBlockButton = new JButton();
        jSplitPane1 = new JSplitPane();
        jTabbedPane1 = new JTabbedPane();
        jPanel2 = new JPanel();
        jPanel4 = new JPanel();
        blockEditor = new TileView();
        jSeparator1 = new JSeparator();
        flipXCheck = new JCheckBox();
        flipYCheck = new JCheckBox();
        useFgPaletteCheck = new JCheckBox();
        highPriorityCheck = new JCheckBox();
        jScrollPane3 = new JScrollPane();
        tilesetView = new TileView();
        jPanel5 = new JPanel();
        jLabel1 = new JLabel();
        tileOffsetSpinner = new JSpinner();
        vramAddressLabel = new JLabel();
        jPanel3 = new JPanel();
        jScrollPane2 = new JScrollPane();
        mappingView = new TileView();
        drawBlocksetGridCheck = new JCheckBox();
        showNumbersCheck = new JCheckBox();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        blockView = new TileView();
        jSeparator4 = new Separator();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        zoomInButton2 = new javax.swing.JButton();
        zoomOutButton2 = new javax.swing.JButton();
        zoomInButton3 = new javax.swing.JButton();
        zoomOutButton3 = new javax.swing.JButton();

        editorModeButtonGroup.add(blockEditorEditModeMenu);
        blockEditorEditModeMenu.setText("Edit Mode");
        blockEditorEditModeMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                blockEditorEditModeMenuActionPerformed(evt);
            }
        });
        blockEditorPopup.add(blockEditorEditModeMenu);

        editorModeButtonGroup.add(blockEditorSelectModeMenu);
        blockEditorSelectModeMenu.setSelected(true);
        blockEditorSelectModeMenu.setText("Select Mode");
        blockEditorSelectModeMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                blockEditorSelectModeMenuActionPerformed(evt);
            }
        });
        blockEditorPopup.add(blockEditorSelectModeMenu);

        mappingViewModeButtonGroup.add(mappingViewEditModeMenu);
        mappingViewEditModeMenu.setText("Edit Mode");
        mappingViewEditModeMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mappingViewEditModeMenuActionPerformed(evt);
            }
        });
        mappingViewPopup.add(mappingViewEditModeMenu);

        mappingViewModeButtonGroup.add(mappingViewSelectModeMenu);
        mappingViewSelectModeMenu.setSelected(true);
        mappingViewSelectModeMenu.setText("Select Mode");
        mappingViewSelectModeMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                mappingViewSelectModeMenuActionPerformed(evt);
            }
        });
        mappingViewPopup.add(mappingViewSelectModeMenu);

        setClosable(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setForeground(Color.white);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Mapping Editor");
        setMinimumSize(new Dimension(320, 240));
        setPreferredSize(new Dimension(640, 720));
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

        openBlocksetButton.setAction(openBlocksetAction);
        openBlocksetButton.setToolTipText(openBlocksetAction.getValue(OpenBlocksetAction.NAME).toString());
        openBlocksetButton.setFocusable(false);
        openBlocksetButton.setHideActionText(true);
        openBlocksetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        openBlocksetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(openBlocksetButton);

        saveBlocksetButton.setAction(saveAction);
        saveBlocksetButton.setToolTipText(saveAction.getValue(SaveBlocksetAction.NAME).toString());
        saveBlocksetButton.setFocusable(false);
        saveBlocksetButton.setHideActionText(true);
        saveBlocksetButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveBlocksetButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(saveBlocksetButton);

        saveAsButton.setAction(saveAsAction);
        saveAsButton.setToolTipText(saveAsAction.getValue(SaveBlocksetAsAction.NAME).toString());
        saveAsButton.setFocusable(false);
        saveAsButton.setHideActionText(true);
        saveAsButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveAsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(saveAsButton);

        saveAssemblyButton.setAction(saveAssemblyAction);
        saveAssemblyButton.setToolTipText(saveAssemblyAction.getValue(SaveBlocksetAsAssemblyAction.NAME).toString());
        saveAssemblyButton.setFocusable(false);
        saveAssemblyButton.setHideActionText(true);
        saveAssemblyButton.setHorizontalTextPosition(SwingConstants.CENTER);
        saveAssemblyButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(saveAssemblyButton);

        exportImageButton.setAction(exportImageAction);
        exportImageButton.setToolTipText(exportImageAction.getValue(ExportToImageAction.NAME).toString());
        exportImageButton.setFocusable(false);
        exportImageButton.setHideActionText(true);
        exportImageButton.setHorizontalTextPosition(SwingConstants.CENTER);
        exportImageButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(exportImageButton);
        jToolBar1.add(jSeparator2);

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
        jToolBar1.add(jSeparator3);

        addBlockButton.setAction(addBlockAction);
        addBlockButton.setToolTipText(addBlockAction.getValue(AddTileAction.NAME).toString());
        addBlockButton.setFocusable(false);
        addBlockButton.setHideActionText(true);
        addBlockButton.setHorizontalTextPosition(SwingConstants.CENTER);
        addBlockButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(addBlockButton);

        removeBlockButton.setAction(removeBlockAction);
        removeBlockButton.setToolTipText(removeBlockAction.getValue(RemoveTileAction.NAME).toString());
        removeBlockButton.setFocusable(false);
        removeBlockButton.setHideActionText(true);
        removeBlockButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeBlockButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        jToolBar1.add(removeBlockButton);

        jToolBar1.add(jSeparator4);
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

        getContentPane().add(jToolBar1, BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(350);
        jSplitPane1.setResizeWeight(1.0);

        jPanel4.setBorder(BorderFactory.createEtchedBorder());

        blockEditor.setEditingModel(tilesetAdapter);
        blockEditor.setModel(editorAdapter);
        blockEditor.setPreferredSize(new Dimension(160, 160));
        blockEditor.setTileViewLayout(new TileViewGridLayout());
        blockEditor.setZoomFactor(5.0F);
        blockEditor.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent evt) {
                blockEditorKeyPressed(evt);
            }
        });
        blockEditor.addTileSelectionListener(this);

        blockEditor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if(evt.getButton() == MouseEvent.BUTTON3) {
                    blockEditorPopup.show(blockEditor, evt.getX(), evt.getY());
                }
            }
        });

        GroupLayout blockEditorLayout = new GroupLayout(blockEditor);
        blockEditor.setLayout(blockEditorLayout);
        blockEditorLayout.setHorizontalGroup(
            blockEditorLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        blockEditorLayout.setVerticalGroup(
            blockEditorLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );

        jSeparator1.setOrientation(SwingConstants.HORIZONTAL);

        flipXCheck.setText("Flip X");
        flipXCheck.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                flipXCheckStateChanged(evt);
            }
        });
        flipXCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                flipXCheckActionPerformed(evt);
            }
        });

        flipYCheck.setText("Flip Y");
        flipYCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                flipYCheckActionPerformed(evt);
            }
        });

        useFgPaletteCheck.setText("Use FG Palette");
        useFgPaletteCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                useFgPaletteCheckActionPerformed(evt);
            }
        });

        highPriorityCheck.setText("High Priority");
        highPriorityCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                highPriorityCheckActionPerformed(evt);
            }
        });

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup()
                    .addComponent(blockEditor, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(flipXCheck)
                            .addGap(18, 18, 18)
                            .addComponent(useFgPaletteCheck))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                            .addComponent(flipYCheck)
                            .addGap(18, 18, 18)
                            .addComponent(highPriorityCheck)))
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(blockEditor, GroupLayout.PREFERRED_SIZE, 160, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, GroupLayout.DEFAULT_SIZE, 8, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(flipXCheck)
                            .addComponent(useFgPaletteCheck))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(flipYCheck)
                            .addComponent(highPriorityCheck)))
                )
                .addContainerGap())
        );

        tilesetView.setModel(tilesetAdapter);
        tilesetView.setZoomFactor(2.0F);
        tilesetView.addTileSelectionListener(this);

        GroupLayout tilesetViewLayout = new GroupLayout(tilesetView);
        tilesetView.setLayout(tilesetViewLayout);
        tilesetViewLayout.setHorizontalGroup(
            tilesetViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );
        tilesetViewLayout.setVerticalGroup(
            tilesetViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(tilesetView);

        jPanel5.setBorder(BorderFactory.createEtchedBorder());

        jLabel1.setText("Tile Offset:");

        tileOffsetSpinner.setModel(new SpinnerNumberModel(256, 0, 447, 1));
        tileOffsetSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                tileOffsetSpinnerStateChanged(evt);
            }
        });

        vramAddressLabel.setText("(VRAM: $2000)");

        zoomInButton2.setAction(zoomInAction2);
        zoomInButton2.setToolTipText(zoomInAction2.getValue(ZoomInAction.NAME).toString());
        zoomInButton2.setFocusable(false);
        zoomInButton2.setHideActionText(true);
        zoomInButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        zoomOutButton2.setAction(zoomOutAction2);
        zoomOutButton2.setToolTipText(zoomOutAction2.getValue(ZoomOutAction.NAME).toString());
        zoomOutButton2.setFocusable(false);
        zoomOutButton2.setHideActionText(true);
        zoomOutButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        GroupLayout jPanel5Layout = new GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createSequentialGroup()
            .addContainerGap()
            .addGroup(jPanel5Layout.createParallelGroup(Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(jLabel1)
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addComponent(tileOffsetSpinner, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                )
                .addComponent(vramAddressLabel, GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addComponent(zoomInButton2)
                    .addComponent(zoomOutButton2)
                )
            )
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tileOffsetSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                )
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(vramAddressLabel)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(zoomInButton2)
                    .addComponent(zoomOutButton2)
                )
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.TRAILING)
                    .addComponent(jScrollPane3, Alignment.LEADING)
                    .addComponent(jPanel5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Block Editor", jPanel2);

        mappingView.setCellRenderer(mappingRenderer);
        mappingView.setEditingModel(blocksetAdapter);
        mappingView.setModel(mappingAdapter);
        mappingView.addTileSelectionListener(this);

        mappingView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                if(evt.getButton() == MouseEvent.BUTTON3) {
                    mappingViewPopup.show(mappingView, evt.getX(), evt.getY());
                }
            }
        });

        GroupLayout mappingViewLayout = new GroupLayout(mappingView);
        mappingView.setLayout(mappingViewLayout);
        mappingViewLayout.setHorizontalGroup(
            mappingViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );
        mappingViewLayout.setVerticalGroup(
            mappingViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(mappingView);

        drawBlocksetGridCheck.setSelected(true);
        drawBlocksetGridCheck.setText("Show grid lines");
        drawBlocksetGridCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                drawBlocksetGridCheckActionPerformed(evt);
            }
        });

        showNumbersCheck.setSelected(true);
        showNumbersCheck.setText("Show mapping numbers");
        showNumbersCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showNumbersCheckActionPerformed(evt);
            }
        });

        zoomInButton3.setAction(zoomInAction3);
        zoomInButton3.setToolTipText(zoomInAction3.getValue(ZoomInAction.NAME).toString());
        zoomInButton3.setFocusable(false);
        zoomInButton3.setHideActionText(true);
        zoomInButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        zoomOutButton3.setAction(zoomOutAction3);
        zoomOutButton3.setToolTipText(zoomOutAction3.getValue(ZoomOutAction.NAME).toString());
        zoomOutButton3.setFocusable(false);
        zoomOutButton3.setHideActionText(true);
        zoomOutButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomOutButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(showNumbersCheck)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(drawBlocksetGridCheck)
                .addContainerGap(52, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(zoomInButton3)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(zoomOutButton3)
                    .addContainerGap(52, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(zoomInButton3)
                        .addComponent(zoomOutButton3))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(showNumbersCheck)
                    .addComponent(drawBlocksetGridCheck))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Mappings", jPanel3);

        jSplitPane1.setRightComponent(jTabbedPane1);

        blockView.setCellRenderer(blocksetRenderer);
        blockView.setModel(blocksetAdapter);
        blockView.addTileSelectionListener(this);

        GroupLayout blockViewLayout = new GroupLayout(blockView);
        blockView.setLayout(blockViewLayout);
        blockViewLayout.setHorizontalGroup(
            blockViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 207, Short.MAX_VALUE)
        );
        blockViewLayout.setVerticalGroup(
            blockViewLayout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(blockView);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        getContentPane().add(jSplitPane1, BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tileOffsetSpinnerStateChanged(ChangeEvent evt) {//GEN-FIRST:event_tileOffsetSpinnerStateChanged
        if(tileOffsetSpinner.getValue() instanceof Integer) {
            int newOffset = (Integer) tileOffsetSpinner.getValue();

            setTileOffset(newOffset);

        }
    }//GEN-LAST:event_tileOffsetSpinnerStateChanged

    private void showNumbersCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_showNumbersCheckActionPerformed
        mappingRenderer.setDrawIndex(showNumbersCheck.isSelected());
        mappingView.repaint();
}//GEN-LAST:event_showNumbersCheckActionPerformed

    private void flipXCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_flipXCheckActionPerformed
        Point p = getSelectedBlockElement();
        Block block = editorAdapter.getBlock();

        block.setFlipHorizontal(flipXCheck.isSelected(), p.x, p.y);

        blockEditor.repaint();
    }//GEN-LAST:event_flipXCheckActionPerformed

    private void flipYCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_flipYCheckActionPerformed
        Point p = getSelectedBlockElement();
        Block block = editorAdapter.getBlock();

        block.setFlipVertical(flipYCheck.isSelected(), p.x, p.y);

        blockEditor.repaint();
    }//GEN-LAST:event_flipYCheckActionPerformed

    private void useFgPaletteCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_useFgPaletteCheckActionPerformed
        Point p = getSelectedBlockElement();
        Block block = editorAdapter.getBlock();

        block.setUseSpritePalette(useFgPaletteCheck.isSelected(), p.x, p.y);

        blockEditor.repaint();
    }//GEN-LAST:event_useFgPaletteCheckActionPerformed

    private void highPriorityCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_highPriorityCheckActionPerformed
        Point p = getSelectedBlockElement();
        Block block = editorAdapter.getBlock();

        block.setHighPriority(highPriorityCheck.isSelected(), p.x, p.y);

        blockEditor.repaint();
    }//GEN-LAST:event_highPriorityCheckActionPerformed

    private void blockEditorEditModeMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_blockEditorEditModeMenuActionPerformed
        blockEditor.setMode(TileView.MODE_EDIT);
    }//GEN-LAST:event_blockEditorEditModeMenuActionPerformed

    private void blockEditorSelectModeMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_blockEditorSelectModeMenuActionPerformed
        blockEditor.setMode(TileView.MODE_SELECT);
    }//GEN-LAST:event_blockEditorSelectModeMenuActionPerformed

    private void mappingViewEditModeMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_mappingViewEditModeMenuActionPerformed
        mappingView.setMode(TileView.MODE_EDIT);
    }//GEN-LAST:event_mappingViewEditModeMenuActionPerformed

    private void mappingViewSelectModeMenuActionPerformed(ActionEvent evt) {//GEN-FIRST:event_mappingViewSelectModeMenuActionPerformed
        mappingView.setMode(TileView.MODE_SELECT);
    }//GEN-LAST:event_mappingViewSelectModeMenuActionPerformed

    private void formInternalFrameClosing(InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if(blockset.isModified()) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "The blockset has been modified.\nDo you want to save the changes?",
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

    private void flipXCheckStateChanged(ChangeEvent evt) {//GEN-FIRST:event_flipXCheckStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_flipXCheckStateChanged

    private void blockEditorKeyPressed(KeyEvent evt) {//GEN-FIRST:event_blockEditorKeyPressed
        if(blockEditor.getSelectedTileIndex() >= 0) {
            Block block = editorAdapter.getBlock();
            int index = blockEditor.getSelectedTileIndex();
            int x = index % 4;
            int y = index / 4;

            switch(evt.getKeyCode()) {
                case KeyEvent.VK_F:
                    //block.setFlipHorizontal(!block.isFlipHorizontal(x, y), x, y);
                    flipXCheck.setSelected(true);
                    break;

                case KeyEvent.VK_Y:
                    block.setFlipVertical(!block.isFlipVertical(x, y), x, y);
                    break;

                case KeyEvent.VK_P:
                    block.setHighPriority(!block.isHighPriority(x, y), x, y);
                    break;

                case KeyEvent.VK_S:
                    block.setUseSpritePalette(!block.isUseSpritePalette(x, y), x, y);
                    break;
            }
        }
    }//GEN-LAST:event_blockEditorKeyPressed

    private void drawBlocksetGridCheckActionPerformed(ActionEvent evt) {//GEN-FIRST:event_drawBlocksetGridCheckActionPerformed
        blocksetRenderer.setDrawBorder(drawBlocksetGridCheck.isSelected());
        mappingRenderer.setDrawBorder(drawBlocksetGridCheck.isSelected());

        blockView.repaint();
        mappingView.repaint();
}//GEN-LAST:event_drawBlocksetGridCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton addBlockButton;
    private TileView blockEditor;
    private JRadioButtonMenuItem blockEditorEditModeMenu;
    private JPopupMenu blockEditorPopup;
    private JRadioButtonMenuItem blockEditorSelectModeMenu;
    private TileView blockView;
    private JCheckBox drawBlocksetGridCheck;
    private ButtonGroup editorModeButtonGroup;
    private JButton exportImageButton;
    private JCheckBox flipXCheck;
    private JCheckBox flipYCheck;
    private JCheckBox highPriorityCheck;
    private JLabel jLabel1;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JSeparator jSeparator1;
    private Separator jSeparator2;
    private Separator jSeparator3;
    private Separator jSeparator4;
    private JSplitPane jSplitPane1;
    private JTabbedPane jTabbedPane1;
    private JToolBar jToolBar1;
    private TileView mappingView;
    private JRadioButtonMenuItem mappingViewEditModeMenu;
    private ButtonGroup mappingViewModeButtonGroup;
    private JPopupMenu mappingViewPopup;
    private JRadioButtonMenuItem mappingViewSelectModeMenu;
    private JButton openBgPaletteButton;
    private JButton openBlocksetButton;
    private JButton openFgPaletteButton;
    private JButton openTilesetButton;
    private JButton removeBlockButton;
    private JButton saveAsButton;
    private JButton saveAssemblyButton;
    private JButton saveBlocksetButton;
    private JCheckBox showNumbersCheck;
    private JSpinner tileOffsetSpinner;
    private TileView tilesetView;
    private JCheckBox useFgPaletteCheck;
    private JLabel vramAddressLabel;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    private javax.swing.JButton zoomInButton2;
    private javax.swing.JButton zoomOutButton2;
    private javax.swing.JButton zoomInButton3;
    private javax.swing.JButton zoomOutButton3;

    // End of variables declaration//GEN-END:variables

}
