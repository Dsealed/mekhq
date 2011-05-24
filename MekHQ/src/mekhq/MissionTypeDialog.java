/*
 * MissionTypeDialog.java
 *
 * Created on Jan 6, 2010, 10:46:02 PM
 */

package mekhq;

import java.awt.Frame;

import mekhq.campaign.Campaign;

/**
 *
 * @author natit
 */
public class MissionTypeDialog extends javax.swing.JDialog {

	private Campaign campaign;
	private MekHQView hqview;
	private Frame frame;
	
	private static final long serialVersionUID = 8376874926997734492L;
	/** Creates new form */
    public MissionTypeDialog(java.awt.Frame parent, boolean modal, Campaign c, MekHQView view) {
        super(parent, modal);
        frame = parent;
        this.campaign = c;
        this.hqview = view;
        initComponents();      
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {


        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane().setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(MissionTypeDialog.class);
        
        btnMission = new javax.swing.JButton(resourceMap.getString("btnMission.text"));
        btnMission.setToolTipText(resourceMap.getString("btnMission.tooltip"));
        btnMission.setName("btnMission"); // NOI18N
        btnMission.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMission();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(btnMission, gridBagConstraints);
        
        btnContract = new javax.swing.JButton(resourceMap.getString("btnContract.text"));
        btnMission.setToolTipText(resourceMap.getString("btnContract.tooltip"));
        btnContract.setName("btnContract"); // NOI18N
        btnContract.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newContract();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(btnContract, gridBagConstraints);

        pack();
    }
    
    private void newMission() {
    	CustomizeMissionDialog cmd = new CustomizeMissionDialog(frame, true, null, campaign);
		cmd.setVisible(true);
		this.setVisible(false);
		if(cmd.getMissionId() != -1) {
			hqview.selectedMission = cmd.getMissionId();
		}
		hqview.refreshMissions();
    }
    
    private void newContract() {
    	NewContractDialog ncd = new NewContractDialog(frame, true, campaign);
		ncd.setVisible(true);
		this.setVisible(false);
		if(ncd.getContractId() != -1) {
			hqview.selectedMission = ncd.getContractId();
		}
		hqview.refreshMissions();
		hqview.refreshFinancialTransactions();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnMission;
    private javax.swing.JButton btnContract;
    // End of variables declaration//GEN-END:variables

}
