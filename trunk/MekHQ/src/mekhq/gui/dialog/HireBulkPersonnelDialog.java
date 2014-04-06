/*
 * HireBulkPersonnel.java
 *
 * Created on Jan 6, 2010, 10:46:02 PM
 */

package mekhq.gui.dialog;

import java.awt.GridBagLayout;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.Rank;
import mekhq.campaign.personnel.Ranks;
import mekhq.gui.CampaignGUI;


/**
 *
 * @author Jay Lawson
 */
public class HireBulkPersonnelDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = -6946480787293179307L;

	private Campaign campaign;
	
    private JComboBox choiceType;
    private JComboBox choiceRanks;
    private JSpinner spnNumber;
    
    private JLabel lblType;
    private JLabel lblRank;
    private JLabel lblNumber;
    private JButton btnHire;
    private JButton btnClose;
    private JPanel panButtons;
    
    private CampaignGUI hqView;
    
    private ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.HireBulkPersonnelDialog");

    public HireBulkPersonnelDialog(java.awt.Frame parent, boolean modal, Campaign c, CampaignGUI view) {
        super(parent, modal);
        this.campaign = c;
        this.hqView = view;
        initComponents();
        setLocationRelativeTo(getParent());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        
        choiceType = new javax.swing.JComboBox();
        choiceRanks = new javax.swing.JComboBox();

        lblType = new javax.swing.JLabel(resourceMap.getString("lblType.text"));
        lblRank = new javax.swing.JLabel(resourceMap.getString("lblRank.text"));
        lblNumber = new javax.swing.JLabel(resourceMap.getString("lblNumber.text"));
        btnHire = new JButton(resourceMap.getString("btnHire.text"));
        btnClose = new JButton(resourceMap.getString("btnClose.text"));
        panButtons = new JPanel(new GridBagLayout());
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        setTitle(resourceMap.getString("Form.title"));
        getContentPane().setLayout(new GridBagLayout());
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblType, gridBagConstraints);
        
        DefaultComboBoxModel personTypeModel = new DefaultComboBoxModel();
        for(int i = 1; i < Person.T_NUM; i++) {
        	personTypeModel.addElement(Person.getRoleDesc(i,campaign.getFaction().isClan()));
        }
        choiceType.setModel(personTypeModel);
        choiceType.setName("choiceType"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 1.0;
        choiceType.setSelectedIndex(0);
        choiceType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	// If we change the type, we need to setup the ranks for that type
            	refreshRanksCombo();
            }
        });
        getContentPane().add(choiceType, gridBagConstraints);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblRank, gridBagConstraints);
        
        DefaultComboBoxModel rankModel = new DefaultComboBoxModel();
        choiceRanks.setModel(rankModel);
        choiceRanks.setName("choiceRanks"); // NOI18N
        refreshRanksCombo();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(choiceRanks, gridBagConstraints);
        
        spnNumber = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(lblNumber, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(spnNumber, gridBagConstraints);
        
        btnHire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hire();
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;

        panButtons.add(btnHire, gridBagConstraints);
        gridBagConstraints.gridx++;

        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisible(false);
            }
        });
        panButtons.add(btnClose, gridBagConstraints);

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(panButtons, gridBagConstraints);
        
        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void hire() {
    	int number = (Integer)spnNumber.getModel().getValue();
    	while(number > 0) {
    		Person p = campaign.newPerson(choiceType.getSelectedIndex() + 1);
    		p.setRankNumeric(campaign.getRanks().getRankNumericFromNameAndProfession(p.getProfession(), (String)choiceRanks.getSelectedItem()));
    		if(!campaign.recruitPerson(p)) {
    		    number = 0;
    		} else {
    		    number--;
    		}
    	}
    	hqView.refreshPersonnelList();
        hqView.refreshPatientList();
        hqView.refreshTechsList();
        hqView.refreshDoctorsList();
        hqView.refreshReport();
        hqView.refreshFinancialTransactions();
    }
   
    private void refreshRanksCombo() {
    	DefaultComboBoxModel ranksModel = new DefaultComboBoxModel();
    	
    	// Determine correct profession to pass into the loop
    	int profession = Person.getProfessionFromPrimaryRole((choiceType.getSelectedIndex() + 1));
    	while (campaign.getRanks().isEmptyProfession(profession) && profession != Ranks.RPROF_MW) {
    		profession = campaign.getRanks().getAlternateProfession(profession);
    	}
    	
        for(Rank rank : campaign.getRanks().getAllRanks()) {
        	int p = profession;
        	// Grab rank from correct profession as needed
        	while (rank.getName(p).startsWith("--") && p != Ranks.RPROF_MW) {
            	if (rank.getName(p).equals("--")) {
            		p = campaign.getRanks().getAlternateProfession(p);
            	} else if (rank.getName(p).startsWith("--")) {
            		p = campaign.getRanks().getAlternateProfession(rank.getName(p));
            	}
        	}
        	if (rank.getName(p).equals("-")) {
        		continue;
        	}
        	
        	ranksModel.addElement(rank.getName(p));
        }
        choiceRanks.setModel(ranksModel);
        choiceRanks.setSelectedIndex(0);
    }
}
