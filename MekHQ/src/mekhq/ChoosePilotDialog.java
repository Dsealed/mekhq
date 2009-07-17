/*
 * ChoosePilotDialog.java
 *
 * Created on July 16, 2009, 6:11 PM
 */

package mekhq;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import mekhq.campaign.Unit;
import mekhq.campaign.personnel.PilotPerson;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class ChoosePilotDialog extends javax.swing.JDialog {

    private Unit unit;
    private Vector<PilotPerson> pilots;
    
    /** Creates new form ChoosePilotDialog */
    public ChoosePilotDialog(java.awt.Frame parent, boolean modal, Unit u, Vector<PilotPerson> p) {
        super(parent, modal);
        this.unit = u;
        this.pilots = p;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        choicePilot = new javax.swing.JComboBox();
        btnOkay = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        DefaultComboBoxModel pilotChoicesModel = new DefaultComboBoxModel();
        for(PilotPerson pp : pilots) {
            pilotChoicesModel.addElement(pp.getPilot().getDesc());
        }
        choicePilot.setModel(pilotChoicesModel);
        choicePilot.setName("choicePilot"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(ChoosePilotDialog.class);
        btnOkay.setText(resourceMap.getString("btnOkay.text")); // NOI18N
        btnOkay.setName("btnOkay"); // NOI18N
        btnOkay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkayActionPerformed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(choicePilot, 0, 366, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(108, 108, 108)
                        .add(btnOkay)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(choicePilot, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 32, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnOkay)
                    .add(btnCancel))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

private void btnOkayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkayActionPerformed
    if(choicePilot.getSelectedIndex() > -1 && choicePilot.getSelectedIndex() < pilots.size()) {
        PilotPerson pp = pilots.elementAt(choicePilot.getSelectedIndex());
        unit.changePilot(pp);
    }
    this.setVisible(false);
}//GEN-LAST:event_btnOkayActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ChoosePilotDialog dialog = new ChoosePilotDialog(new javax.swing.JFrame(), true, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOkay;
    private javax.swing.JComboBox choicePilot;
    // End of variables declaration//GEN-END:variables

}
