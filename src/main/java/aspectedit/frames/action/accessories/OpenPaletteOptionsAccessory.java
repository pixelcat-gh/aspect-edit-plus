/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LevelOptionsAccessory.java
 *
 * Created on 21-Feb-2009, 19:03:00
 */

package aspectedit.frames.action.accessories;

import aspectedit.util.OffsetSpinnerBuilder;
import javax.swing.JPanel;

/**
 *
 * @author mark
 */
public class OpenPaletteOptionsAccessory extends JPanel {

    /** Creates new form LevelOptionsAccessory */
    public OpenPaletteOptionsAccessory() {
        initComponents();
    }

    public int getOffset() {
        return (Integer) offsetSpinner.getValue();
    }

    public void setOffset(int offset) {
        offsetSpinner.setValue(offset);
    }
    
    public boolean isGGPalette() {
        return ggRadio.isSelected();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        s2Radio = new javax.swing.JRadioButton();
        ggRadio = new javax.swing.JRadioButton();
        offsetSpinner = OffsetSpinnerBuilder.build();

        setBorder(javax.swing.BorderFactory.createTitledBorder("Options"));

        jLabel1.setText("Offset:");

        jLabel2.setText("Type:");

        buttonGroup1.add(s2Radio);
        s2Radio.setSelected(true);
        s2Radio.setText("SMS");

        buttonGroup1.add(ggRadio);
        ggRadio.setText("GG");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ggRadio, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(s2Radio, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                    .addComponent(offsetSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(offsetSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(s2Radio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ggRadio)
                .addContainerGap(56, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton ggRadio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSpinner offsetSpinner;
    private javax.swing.JRadioButton s2Radio;
    // End of variables declaration//GEN-END:variables

}
