/*
 * NewTeamDialog.java
 * 
 * Copyright (c) 2009 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
 * 
 * This file is part of MekHQ.
 * 
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq;

import mekhq.campaign.team.SupportTeam;
import javax.swing.DefaultComboBoxModel;
import mekhq.campaign.team.MedicalTeam;

/**
 *
 * @author  Taharqa
 */
public class NewMedicalTeamDialog extends javax.swing.JDialog {

    private MedicalTeam doc;
    
    /** Creates new form NewTeamDialog */
    public NewMedicalTeamDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.doc = null;
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

        txtTeamName = new javax.swing.JTextField();
        chTeamRating = new javax.swing.JComboBox();
        lblTeamName = new javax.swing.JLabel();
        lblTeamRating = new javax.swing.JLabel();
        btnDone = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblTeamType = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(NewMedicalTeamDialog.class);
        txtTeamName.setText(resourceMap.getString("txtTeamName.text")); // NOI18N
        txtTeamName.setName("txtTeamName"); // NOI18N
        txtTeamName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTeamNameActionPerformed(evt);
            }
        });

        DefaultComboBoxModel teamRatingModel = new DefaultComboBoxModel();
        for(int i = 0; i < SupportTeam.EXP_NUM; i++) {
            teamRatingModel.addElement(SupportTeam.getRatingName(i));
        }
        chTeamRating.setModel(teamRatingModel);
        chTeamRating.setName("chTeamRating"); // NOI18N
        chTeamRating.setSelectedIndex(SupportTeam.EXP_REGULAR);

        lblTeamName.setName("lblTeamName"); // NOI18N

        lblTeamRating.setName("lblTeamRating"); // NOI18N

        btnDone.setText(resourceMap.getString("btnDone.text")); // NOI18N
        btnDone.setName("btnDone"); // NOI18N
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });

        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        lblTeamType.setName("lblTeamType"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(layout.createSequentialGroup()
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(lblTeamName)
                                        .add(lblTeamRating))
                                    .add(50, 50, 50))
                                .add(layout.createSequentialGroup()
                                    .add(jLabel1)
                                    .add(18, 18, 18)))
                            .add(jLabel2))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(chTeamRating, 0, 242, Short.MAX_VALUE)
                            .add(txtTeamName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(lblTeamType)
                        .addContainerGap(338, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(btnDone)
                        .add(18, 18, 18)
                        .add(btnCancel)
                        .add(100, 100, 100))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblTeamName)
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtTeamName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel1)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chTeamRating, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblTeamRating)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(lblTeamType)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(btnCancel)
                    .add(btnDone))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
    int rating = chTeamRating.getSelectedIndex();
    String name = txtTeamName.getText();
    doc = new MedicalTeam(name, rating);
    this.setVisible(false);
}//GEN-LAST:event_btnDoneActionPerformed

private void txtTeamNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTeamNameActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_txtTeamNameActionPerformed

private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
    this.setVisible(false);
}//GEN-LAST:event_btnCancelActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewMedicalTeamDialog dialog = new NewMedicalTeamDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    public MedicalTeam getMedicalTeam() {
        return doc;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDone;
    private javax.swing.JComboBox chTeamRating;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblTeamName;
    private javax.swing.JLabel lblTeamRating;
    private javax.swing.JLabel lblTeamType;
    private javax.swing.JTextField txtTeamName;
    // End of variables declaration//GEN-END:variables

}
