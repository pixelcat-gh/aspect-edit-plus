
package aspectedit.frames;

import aspectedit.components.tileview.DefaultTileViewCellRenderer;
import aspectedit.components.tileview.TileSelectionEvent;
import aspectedit.components.tileview.TileSelectionListener;
import aspectedit.components.tileview.TileView;
import aspectedit.components.tileview.adapters.PaletteViewAdapter;
import aspectedit.components.tileview.adapters.TileArrangerModel;
import aspectedit.components.tileview.adapters.TilesetViewAdapter;
import aspectedit.components.tileview.layout.TileViewGridLayout;
import aspectedit.frames.action.*;
import aspectedit.palette.Palette;
import aspectedit.tiles.FlipHorizontalOp;
import aspectedit.tiles.FlipVerticalOp;
import aspectedit.tiles.Tile;
import aspectedit.tiles.Tileset;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.undo.UndoManager;

/**
 *
 * @author mark
 */
public class TilesetEditorFrame
        extends JInternalFrame
        implements TileSelectionListener {

    /* Actions */
    private ZoomInAction zoomInAction;
    private ZoomOutAction zoomOutAction;
    private OpenTilesetAction openTilesetAction;
    private OpenPaletteAction openPaletteAction;
    private SaveTilesetAction saveAction;
    private SaveTilesetAsAction saveAsAction;
    private AddTileAction addTileAction;
    private RemoveTileAction removeTileAction;
    private MoveTileLeftAction moveLeftAction;
    private MoveTileRightAction moveRightAction;
    private ExportToImageAction exportImageAction;

    private Palette palette;
    private PaletteViewAdapter paletteAdapter;
    private Tileset tileset;
    private TilesetViewAdapter tilesetAdapter;
    private TileArrangerModel arrangerModel;
    private DefaultTileViewCellRenderer renderer;

    /** An UndoManager for the tile editor. */
    private UndoManager editorUndoManager;


    /**
     * Creates a new Tileset Editor frame with an empty tileset.
     */
    public TilesetEditorFrame() {
        this(new Tileset());
    }


    /**
     * Creates a new Tileset Editor frame with the specified tileset.
     * @param tileset
     */
    public TilesetEditorFrame(Tileset tileset) {
        zoomInAction = new ZoomInAction();
        zoomOutAction = new ZoomOutAction();
        openTilesetAction = new OpenTilesetAction();
        openPaletteAction = new OpenPaletteAction();
        saveAction = new SaveTilesetAction();
        saveAsAction = new SaveTilesetAsAction();
        addTileAction = new AddTileAction();
        removeTileAction = new RemoveTileAction();
        moveLeftAction = new MoveTileLeftAction();
        moveRightAction = new MoveTileRightAction();
        exportImageAction = new ExportToImageAction();

        //init the TileView models
        tilesetAdapter = new TilesetViewAdapter();
        paletteAdapter = new PaletteViewAdapter();
        arrangerModel = new TileArrangerModel();


        editorUndoManager = new UndoManager();


        initComponents();

        renderer = new DefaultTileViewCellRenderer();
        tilesetView.setCellRenderer(renderer);

        addTileAction.setTileView(tilesetView);
        removeTileAction.setTileView(tilesetView);
        zoomInAction.setTileView(tilesetView);
        zoomOutAction.setTileView(tilesetView);
        moveLeftAction.setTileView(tilesetView);
        moveRightAction.setTileView(tilesetView);
        exportImageAction.setTileView(tilesetView);

        //handler for the OpenTilesetAction's property change events
        openTilesetAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                tilesetChanged(evt);
            }
        });

        openPaletteAction.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                paletteChanged(evt);
            }
        });


        //add undo support to the tile editor
        tileEditor.addUndoableEditListener(editorUndoManager);
//        tileEditor.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyPressed(KeyEvent evt) {
//                if(evt.isControlDown()) {
//                    switch(evt.getKeyCode()) {
//
//                        case KeyEvent.VK_Z:
//                            if(editorUndoManager.canUndo()) {
//                                editorUndoManager.undo();
//                                tileEditor.repaint();
//                            }
//                            break;
//
//                        case KeyEvent.VK_Y:
//                            if(editorUndoManager.canRedo()) {
//                                editorUndoManager.redo();
//                                tileEditor.repaint();
//                            }
//                            break;
//                    }
//                }
//            }
//
//        });

        
        if(tileset != null) setTileset(tileset);
    }


    private void paletteChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Palette) {
            setPalette( (Palette) evt.getNewValue() );
        }

    }


    private void tilesetChanged(PropertyChangeEvent evt) {
        if(evt.getNewValue() instanceof Tileset) {
            setTileset( (Tileset) evt.getNewValue() );
        }
    }


    public void setPalette(Palette palette) {
        if(palette == null) {
            throw new IllegalArgumentException("Palette cannot be null.");
        }

        this.palette = palette;
        paletteAdapter.setPalette(palette);
        
        if(tileset != null) tileset.setPalette(palette);

        repaint();
    }

    public void setTileset(Tileset tileset) {
        if(tileset == null)
            throw new IllegalArgumentException("Tileset cannot be null.");
        
        this.tileset = tileset;

        if(palette != null && tileset.getPalette() == Palette.BLANK_PALETTE)
            tileset.setPalette(palette);
        
        tilesetAdapter.setTileset(tileset);
        arrangerModel.setTileset(tileset);
        
        saveAction.setTileset(tileset);
        saveAsAction.setTileset(tileset);

        // invalidate all undoable edits since the tileset has changed
        editorUndoManager.discardAllEdits();
    }


    @Override
    public void notifyTileSelected(TileSelectionEvent evt) {
        if(evt.getSource() == tilesetView) {
            tileEditor.setTile(evt.getTile());
            editorUndoManager.discardAllEdits();
            arrangerView.setEditTileIndex(evt.getIndex());
            
        } else if(evt.getSource() == paletteView) {
            int colour = paletteAdapter.indexOf(evt.getTile());
            tileEditor.setColour(colour);

        } else if(evt.getSource() == arrangerView && evt.getTile() instanceof Tile) {
            if(tileset.indexOf((Tile) evt.getTile()) != -1) {
                int index = tileset.indexOf((Tile) evt.getTile());
                tilesetView.setSelectedTileIndex(index);
            }
        }
    }


    private void arrangerClicked(MouseEvent evt) {
        if(evt.getButton() == MouseEvent.BUTTON3) {
            autoArrangeMenu.setEnabled(
                    arrangerView.getMode() == TileView.MODE_SELECT);
            
            arrangerPopup.show(arrangerView, evt.getX(), evt.getY());
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
        java.awt.GridBagConstraints gridBagConstraints;

        arrangerPopup = new javax.swing.JPopupMenu();
        arrangerEditModeMenu = new javax.swing.JRadioButtonMenuItem();
        arrangerSelectModeMenu = new javax.swing.JRadioButtonMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        autoArrangeMenu = new javax.swing.JMenuItem();
        clearArrangerMenu = new javax.swing.JMenuItem();
        arrangerModeButtonGroup = new javax.swing.ButtonGroup();
        tileOpsPopup = new javax.swing.JPopupMenu();
        flipHorizontalMenu = new javax.swing.JMenuItem();
        flipVerticalMenu = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        openTilesetButton = new javax.swing.JButton();
        saveTilesetButton = new javax.swing.JButton();
        saveAsTilesetButton = new javax.swing.JButton();
        exportImageButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        openPaletteButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        zoomInButton = new javax.swing.JButton();
        zoomOutButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        addTileButton = new javax.swing.JButton();
        removeTileButton = new javax.swing.JButton();
        moveLeftButton = new javax.swing.JButton();
        moveRightButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tilesetView = new aspectedit.components.tileview.TileView();
        drawGridCheck = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        tileEditor = new aspectedit.components.TileEditor();
        paletteView = new aspectedit.components.tileview.TileView();
        jPanel3 = new javax.swing.JPanel();
        arrangerView = new aspectedit.components.tileview.TileView();

        arrangerModeButtonGroup.add(arrangerEditModeMenu);
        arrangerEditModeMenu.setSelected(true);
        arrangerEditModeMenu.setText("Edit Mode");
        arrangerEditModeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arrangerEditModeMenuActionPerformed(evt);
            }
        });
        arrangerPopup.add(arrangerEditModeMenu);

        arrangerModeButtonGroup.add(arrangerSelectModeMenu);
        arrangerSelectModeMenu.setText("Select Mode");
        arrangerSelectModeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                arrangerSelectModeMenuActionPerformed(evt);
            }
        });
        arrangerPopup.add(arrangerSelectModeMenu);
        arrangerPopup.add(jSeparator4);

        autoArrangeMenu.setText("Auto Arrange");
        autoArrangeMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoArrangeMenuActionPerformed(evt);
            }
        });
        arrangerPopup.add(autoArrangeMenu);

        clearArrangerMenu.setText("Clear");
        clearArrangerMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearArrangerMenuActionPerformed(evt);
            }
        });
        arrangerPopup.add(clearArrangerMenu);

        flipHorizontalMenu.setText("Flip Horizontal");
        flipHorizontalMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flipHorizontalMenuActionPerformed(evt);
            }
        });
        tileOpsPopup.add(flipHorizontalMenu);

        flipVerticalMenu.setText("Flip Vertical");
        flipVerticalMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flipVerticalMenuActionPerformed(evt);
            }
        });
        tileOpsPopup.add(flipVerticalMenu);

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Tileset Editor");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosing(evt);
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        openTilesetButton.setAction(openTilesetAction);
        openTilesetButton.setToolTipText(openTilesetAction.getValue(OpenTilesetAction.NAME).toString());
        openTilesetButton.setFocusable(false);
        openTilesetButton.setHideActionText(true);
        openTilesetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openTilesetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(openTilesetButton);

        saveTilesetButton.setAction(saveAction);
        saveTilesetButton.setToolTipText(saveAction.getValue(SaveTilesetAction.NAME).toString());
        saveTilesetButton.setFocusable(false);
        saveTilesetButton.setHideActionText(true);
        saveTilesetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveTilesetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveTilesetButton);

        saveAsTilesetButton.setAction(saveAsAction);
        saveAsTilesetButton.setToolTipText(saveAsAction.getValue(SaveTilesetAsAction.NAME).toString());
        saveAsTilesetButton.setFocusable(false);
        saveAsTilesetButton.setHideActionText(true);
        saveAsTilesetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveAsTilesetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(saveAsTilesetButton);

        exportImageButton.setAction(exportImageAction);
        exportImageButton.setToolTipText(exportImageAction.getValue(ExportToImageAction.NAME).toString());
        exportImageButton.setFocusable(false);
        exportImageButton.setHideActionText(true);
        exportImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exportImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(exportImageButton);
        jToolBar1.add(jSeparator3);

        openPaletteButton.setAction(openPaletteAction);
        openPaletteButton.setToolTipText(openPaletteAction.getValue(OpenPaletteAction.NAME).toString());
        openPaletteButton.setFocusable(false);
        openPaletteButton.setHideActionText(true);
        openPaletteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openPaletteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(openPaletteButton);
        jToolBar1.add(jSeparator1);

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
        jToolBar1.add(jSeparator2);

        addTileButton.setAction(addTileAction);
        addTileButton.setToolTipText(addTileAction.getValue(AddTileAction.NAME).toString());
        addTileButton.setFocusable(false);
        addTileButton.setHideActionText(true);
        addTileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addTileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(addTileButton);

        removeTileButton.setAction(removeTileAction);
        removeTileButton.setToolTipText(removeTileAction.getValue(RemoveTileAction.NAME).toString());
        removeTileButton.setFocusable(false);
        removeTileButton.setHideActionText(true);
        removeTileButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeTileButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(removeTileButton);

        moveLeftButton.setAction(moveLeftAction);
        moveLeftButton.setToolTipText(moveLeftAction.getValue(MoveTileLeftAction.NAME).toString());
        moveLeftButton.setFocusable(false);
        moveLeftButton.setHideActionText(true);
        moveLeftButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveLeftButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(moveLeftButton);

        moveRightButton.setAction(moveRightAction);
        moveRightButton.setToolTipText(moveRightAction.getValue(MoveTileRightAction.NAME).toString());
        moveRightButton.setFocusable(false);
        moveRightButton.setHideActionText(true);
        moveRightButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        moveRightButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(moveRightButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        getContentPane().add(jToolBar1, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tileset"));
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 400));

        tilesetView.setModel(tilesetAdapter);
        tilesetView.setZoomFactor(2.0F);
        tilesetView.addTileSelectionListener(this);

        javax.swing.GroupLayout tilesetViewLayout = new javax.swing.GroupLayout(tilesetView);
        tilesetView.setLayout(tilesetViewLayout);
        tilesetViewLayout.setHorizontalGroup(
            tilesetViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 228, Short.MAX_VALUE)
        );
        tilesetViewLayout.setVerticalGroup(
            tilesetViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(tilesetView);

        drawGridCheck.setSelected(true);
        drawGridCheck.setText("Draw Grid");
        drawGridCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawGridCheckActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(drawGridCheck))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(drawGridCheck)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Editor"));

        tileEditor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        tileEditor.setZoomFactor(10.0F);
        tileEditor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tileEditorMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout tileEditorLayout = new javax.swing.GroupLayout(tileEditor);
        tileEditor.setLayout(tileEditorLayout);
        tileEditorLayout.setHorizontalGroup(
            tileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 120, Short.MAX_VALUE)
        );
        tileEditorLayout.setVerticalGroup(
            tileEditorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        paletteView.setModel(paletteAdapter);
        paletteView.setPreferredSize(new java.awt.Dimension(64, 104));
        paletteView.addTileSelectionListener(this);

        javax.swing.GroupLayout paletteViewLayout = new javax.swing.GroupLayout(paletteView);
        paletteView.setLayout(paletteViewLayout);
        paletteViewLayout.setHorizontalGroup(
            paletteViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        paletteViewLayout.setVerticalGroup(
            paletteViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tileEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paletteView, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paletteView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tileEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Arranger"));

        arrangerView.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        arrangerView.setEditingModel(tilesetAdapter);
        arrangerView.setMode(TileView.MODE_EDIT);
        arrangerView.setModel(arrangerModel);
        arrangerView.setPreferredSize(new java.awt.Dimension(190, 190));
        arrangerView.setTileViewLayout(new TileViewGridLayout());
        arrangerView.setZoomFactor(2.0F);
        arrangerView.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent evt) {
                arrangerClicked(evt);
            }

        });

        arrangerView.addTileSelectionListener(this);

        javax.swing.GroupLayout arrangerViewLayout = new javax.swing.GroupLayout(arrangerView);
        arrangerView.setLayout(arrangerViewLayout);
        arrangerViewLayout.setHorizontalGroup(
            arrangerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );
        arrangerViewLayout.setVerticalGroup(
            arrangerViewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 186, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(arrangerView, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(arrangerView, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearArrangerMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearArrangerMenuActionPerformed
        arrangerModel.clear();
        arrangerView.repaint();
    }//GEN-LAST:event_clearArrangerMenuActionPerformed

    private void arrangerSelectModeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arrangerSelectModeMenuActionPerformed
        arrangerView.setMode(TileView.MODE_SELECT);
    }//GEN-LAST:event_arrangerSelectModeMenuActionPerformed

    private void arrangerEditModeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_arrangerEditModeMenuActionPerformed
        arrangerView.setMode(TileView.MODE_EDIT);
    }//GEN-LAST:event_arrangerEditModeMenuActionPerformed

    private void formInternalFrameClosing(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosing
        if(tileset.isModified()) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "The tileset has been modified.\nDo you want to save the changes?",
                    AspectEdit.APP_NAME,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            switch(result) {

                case JOptionPane.YES_OPTION:
                    saveAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "save"));
                    //fall through
                case JOptionPane.NO_OPTION:
                    this.dispose();
            }
            
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_formInternalFrameClosing

    private void flipHorizontalMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flipHorizontalMenuActionPerformed
        if(tileEditor.getTile() instanceof Tile) {
            Tile tile = (Tile) tileEditor.getTile();

            new FlipHorizontalOp().filter(tile);

            tileEditor.repaint();
        }

    }//GEN-LAST:event_flipHorizontalMenuActionPerformed

    private void flipVerticalMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flipVerticalMenuActionPerformed
        if(tileEditor.getTile() instanceof Tile) {
            Tile tile = (Tile) tileEditor.getTile();

            new FlipVerticalOp().filter(tile);

            tileEditor.repaint();
        }
    }//GEN-LAST:event_flipVerticalMenuActionPerformed

    private void tileEditorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tileEditorMouseClicked
        if(evt.getButton() == MouseEvent.BUTTON3) {
            tileOpsPopup.show(tileEditor, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tileEditorMouseClicked

    private void autoArrangeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoArrangeMenuActionPerformed
        AutoArrangeParametersDialog dlg = new AutoArrangeParametersDialog();

        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);

        int index = tilesetView.getSelectedTileIndex();
        // check that a tile has been selected.
        if(index < 0) return;

        boolean doubled = dlg.isSpriteDoubled();

        int arrangerX = arrangerView.getSelectedTileIndex() % arrangerModel.getWidth();
        int originalX = arrangerX;
        int arrangerY = arrangerView.getSelectedTileIndex() / arrangerModel.getHeight();

        if(dlg.getReturnStatus() == AutoArrangeParametersDialog.RET_OK) {
            for(int y=0; y<dlg.getSpriteHeight(); y++) {

                for(int x=0; x<dlg.getSpriteWidth(); x++) {
                    arrangerModel.setTileAt(arrangerX, arrangerY, index);

                    ++index;

                    if(doubled) {
                        arrangerModel.setTileAt(arrangerX, arrangerY+1, index);
                        ++index;
                    }

                    ++arrangerX;
                }

                arrangerY += doubled ? 2 : 1;
                arrangerX = originalX;
            }
        }
    }//GEN-LAST:event_autoArrangeMenuActionPerformed

    private void drawGridCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawGridCheckActionPerformed
        renderer.setDrawBorder(drawGridCheck.isSelected());
        tilesetView.repaint();
    }//GEN-LAST:event_drawGridCheckActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addTileButton;
    private javax.swing.JRadioButtonMenuItem arrangerEditModeMenu;
    private javax.swing.ButtonGroup arrangerModeButtonGroup;
    private javax.swing.JPopupMenu arrangerPopup;
    private javax.swing.JRadioButtonMenuItem arrangerSelectModeMenu;
    private aspectedit.components.tileview.TileView arrangerView;
    private javax.swing.JMenuItem autoArrangeMenu;
    private javax.swing.JMenuItem clearArrangerMenu;
    private javax.swing.JCheckBox drawGridCheck;
    private javax.swing.JButton exportImageButton;
    private javax.swing.JMenuItem flipHorizontalMenu;
    private javax.swing.JMenuItem flipVerticalMenu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton moveLeftButton;
    private javax.swing.JButton moveRightButton;
    private javax.swing.JButton openPaletteButton;
    private javax.swing.JButton openTilesetButton;
    private aspectedit.components.tileview.TileView paletteView;
    private javax.swing.JButton removeTileButton;
    private javax.swing.JButton saveAsTilesetButton;
    private javax.swing.JButton saveTilesetButton;
    private aspectedit.components.TileEditor tileEditor;
    private javax.swing.JPopupMenu tileOpsPopup;
    private aspectedit.components.tileview.TileView tilesetView;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    // End of variables declaration//GEN-END:variables

}
