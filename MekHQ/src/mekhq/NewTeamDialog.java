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

import mekhq.campaign.SupportTeam;
import mekhq.campaign.Campaign;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author  Taharqa
 */
public class NewTeamDialog extends javax.swing.JDialog {

    Campaign campaign;
    
    /** Creates new form NewTeamDialog */
    public NewTeamDialog(java.awt.Frame parent, boolean modal, Campaign c) {
        super(parent, modal);
        this.campaign = c;
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
        chTeamType = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(NewTeamDialog.class);
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

        lblTeamName.setText(resourceMap.getString("lblTeamName.text")); // NOI18N
        lblTeamName.setName("lblTeamName"); // NOI18N

        lblTeamRating.setText(resourceMap.getString("lblTeamRating.text")); // NOI18N
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

        lblTeamType.setText(resourceMap.getString("lblTeamType.text")); // NOI18N
        lblTeamType.setName("lblTeamType"); // NOI18N

        DefaultComboBoxModel teamTypeModel = new DefaultComboBoxModel();
        for(int i = 0; i < SupportTeam.T_NUM; i++) {
            teamTypeModel.addElement(SupportTeam.getTypeDesc(i));
        }
        chTeamType.setModel(teamTypeModel);
        chTeamType.setName("chTeamType"); // NOI18N
        chTeamType.setSelectedIndex(SupportTeam.T_MECH);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblTeamName)
                            .add(lblTeamRating))
                        .add(50, 50, 50)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(chTeamRating, 0, 258, Short.MAX_VALUE)
                            .add(txtTeamName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                            .add(chTeamType, 0, 258, Short.MAX_VALUE))
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
                    .add(txtTeamName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chTeamRating, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblTeamRating))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblTeamType)
                    .add(chTeamType, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
    int type = chTeamType.getSelectedIndex();
    String name = txtTeamName.getText();
    SupportTeam t = new SupportTeam(campaign, name, rating, type);
    campaign.addTeam(t);
    this.setVisible(false);
}//GEN-LAST:event_btnDoneActionPerformed

private void txtTeamNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTeamNameActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_txtTeamNameActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewTeamDialog dialog = new NewTeamDialog(new javax.swing.JFrame(), true, null);
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
    private javax.swing.JButton btnDone;
    private javax.swing.JComboBox chTeamRating;
    private javax.swing.JComboBox chTeamType;
    private javax.swing.JLabel lblTeamName;
    private javax.swing.JLabel lblTeamRating;
    private javax.swing.JLabel lblTeamType;
    private javax.swing.JTextField txtTeamName;
    // End of variables declaration//GEN-END:variables

}
