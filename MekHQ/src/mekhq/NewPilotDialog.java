/*
 * NewPilotDialog.java
 *
 * Created on July 16, 2009, 5:30 PM
 */

package mekhq;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;

import megamek.client.ui.swing.DialogOptionComponent;
import megamek.client.ui.swing.DialogOptionListener;
import megamek.common.EquipmentType;
import megamek.common.Pilot;
import megamek.common.WeaponType;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import megamek.common.options.PilotOptions;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.NameGen;
import mekhq.campaign.personnel.PilotPerson;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class NewPilotDialog extends javax.swing.JDialog implements DialogOptionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6265589976779860566L;
	private Pilot pilot;
    private ArrayList<DialogOptionComponent> optionComps = new ArrayList<DialogOptionComponent>();
    private PilotOptions options;
    private static NameGen nameGen;

    private Campaign campaign;
    
    private MekHQView hqView;

    /** Creates new form NewPilotDialog */
    public NewPilotDialog(java.awt.Frame parent, boolean modal, Campaign campaign, MekHQView view) {
        super(parent, modal);
        this.campaign = campaign;
        this.hqView = view;
        initializePilotAndOptions();
    }

    private void initializePilotAndOptions () {
        refreshPilotAndOptions();
    }

    private void refreshPilotAndOptions () {
        pilot = new Pilot("Roy Fokker", 4, 5);
        pilot.setNickname("Big Brother");
        options = pilot.getOptions();

        getContentPane().removeAll();
        initComponents();
        refreshOptions();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lblName = new javax.swing.JLabel();
        lblNickname = new javax.swing.JLabel();
        textName = new javax.swing.JTextField();
        textNickname = new javax.swing.JTextField();
        textGunnery = new javax.swing.JTextField();
        textPiloting = new javax.swing.JTextField();
        lblGunnery = new javax.swing.JLabel();
        lblPiloting = new javax.swing.JLabel();
        textInitB = new javax.swing.JTextField();
        textCommandB = new javax.swing.JTextField();
        lblInitB = new javax.swing.JLabel();
        lblCommandB = new javax.swing.JLabel();
        choiceType = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        panOptions = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBio = new javax.swing.JTextPane();
        panButtons = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(NewPilotDialog.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblName, gridBagConstraints);

        lblNickname.setText(resourceMap.getString("lblNickname.text")); // NOI18N
        lblNickname.setName("lblNickname"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblNickname, gridBagConstraints);

        if (nameGen == null)
        {
        	nameGen = new NameGen();
        	nameGen.populateNames("/mekhq/resources/firstNames.txt",
        			"/mekhq/resources/lastNames.txt",
        			"/mekhq/resources/namePatterns.txt");
        }

        //textName.setText(pilot.getName());
        textName.setText(nameGen.generate());
        textName.setMinimumSize(new java.awt.Dimension(250, 28));
        textName.setName("textName"); // NOI18N
        textName.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textName, gridBagConstraints);

        textNickname.setText(pilot.getNickname());
        textNickname.setMinimumSize(new java.awt.Dimension(250, 28));
        textNickname.setName("textNickname"); // NOI18N
        textNickname.setPreferredSize(new java.awt.Dimension(250, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textNickname, gridBagConstraints);

        textGunnery.setText(Integer.toString(pilot.getGunnery()));
        textGunnery.setMinimumSize(new java.awt.Dimension(50, 28));
        textGunnery.setName("textGunnery"); // NOI18N
        textGunnery.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textGunnery, gridBagConstraints);

        textPiloting.setText(Integer.toString(pilot.getPiloting()));
        textPiloting.setMinimumSize(new java.awt.Dimension(50, 28));
        textPiloting.setName("textPiloting"); // NOI18N
        textPiloting.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textPiloting, gridBagConstraints);

        lblGunnery.setText(resourceMap.getString("lblGunnery.text")); // NOI18N
        lblGunnery.setName("lblGunnery"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblGunnery, gridBagConstraints);

        lblPiloting.setText(resourceMap.getString("lblPiloting.text")); // NOI18N
        lblPiloting.setName("lblPiloting"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblPiloting, gridBagConstraints);

        textInitB.setText(Integer.toString(pilot.getInitBonus()));
        textInitB.setMinimumSize(new java.awt.Dimension(50, 28));
        textInitB.setName("textInitB"); // NOI18N
        textInitB.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textInitB, gridBagConstraints);

        textCommandB.setText(Integer.toString(pilot.getCommandBonus()));
        textCommandB.setMinimumSize(new java.awt.Dimension(50, 28));
        textCommandB.setName("textCommandB"); // NOI18N
        textCommandB.setPreferredSize(new java.awt.Dimension(50, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(textCommandB, gridBagConstraints);

        lblInitB.setText(resourceMap.getString("lblInitB.text")); // NOI18N
        lblInitB.setName("lblInitB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblInitB, gridBagConstraints);

        lblCommandB.setText(resourceMap.getString("lblCommandB.text")); // NOI18N
        lblCommandB.setName("lblCommandB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(lblCommandB, gridBagConstraints);

        DefaultComboBoxModel pilotTypeModel = new DefaultComboBoxModel();
        for(int i = 0; i < PilotPerson.T_NUM; i++) {
            pilotTypeModel.addElement(PilotPerson.getTypeDesc(i));
        }
        choiceType.setModel(pilotTypeModel);
        choiceType.setMinimumSize(new java.awt.Dimension(200, 27));
        choiceType.setName("choiceType"); // NOI18N
        choiceType.setPreferredSize(new java.awt.Dimension(200, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(choiceType, gridBagConstraints);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(300, 500));
        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.setPreferredSize(new java.awt.Dimension(300, 500));

        panOptions.setName("panOptions"); // NOI18N

        org.jdesktop.layout.GroupLayout panOptionsLayout = new org.jdesktop.layout.GroupLayout(panOptions);
        panOptions.setLayout(panOptionsLayout);
        panOptionsLayout.setHorizontalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 516, Short.MAX_VALUE)
        );
        panOptionsLayout.setVerticalGroup(
            panOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 510, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(panOptions);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtBio.setName("txtBio"); // NOI18N
        jScrollPane2.setViewportView(txtBio);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jScrollPane2, gridBagConstraints);

        panButtons.setName("panButtons"); // NOI18N
        panButtons.setLayout(new java.awt.GridBagLayout());

        btnOk.setText(resourceMap.getString("btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panButtons.add(btnOk, gridBagConstraints);

        btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
        btnClose.setName("btnClose"); // NOI18N
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        panButtons.add(btnClose, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(panButtons, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        PilotPerson person = null;
        int piloting = Integer.parseInt(textPiloting.getText());
        int gunnery = Integer.parseInt(textGunnery.getText());
        int initb = Integer.parseInt(textInitB.getText());
        int commandb = Integer.parseInt(textCommandB.getText());
        String name = textName.getText();
        String nick = textNickname.getText();
        pilot = new Pilot(name, gunnery, piloting);
        pilot.setInitBonus(initb);
        pilot.setCommandBonus(commandb);
        pilot.setNickname(nick);
        setOptions();
        person = new PilotPerson(pilot, choiceType.getSelectedIndex());
        person.setBiography(txtBio.getText());

        campaign.addPerson(person);
        hqView.refreshPersonnelList();
        refreshPilotAndOptions();
    }//GEN-LAST:event_btnOkActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                NewPilotDialog dialog = new NewPilotDialog(new javax.swing.JFrame(), true, null, null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public void refreshOptions() {
        panOptions.removeAll();
        optionComps = new ArrayList<DialogOptionComponent>();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        panOptions.setLayout(gridbag);

        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        c.ipadx = 0;
        c.ipady = 0;

        for (Enumeration<IOptionGroup> i = options.getGroups(); i
        .hasMoreElements();) {
            IOptionGroup group = i.nextElement();

            addGroup(group, gridbag, c);

            for (Enumeration<IOption> j = group.getOptions(); j
            .hasMoreElements();) {
                IOption option = j.nextElement();

                addOption(option, gridbag, c, true);
            }
        }
    }

    private void addGroup(IOptionGroup group, GridBagLayout gridbag,
            GridBagConstraints c) {
        JLabel groupLabel = new JLabel(group.getDisplayableName());

        gridbag.setConstraints(groupLabel, c);
        panOptions.add(groupLabel);
    }

    private void addOption(IOption option, GridBagLayout gridbag,
            GridBagConstraints c, boolean editable) {
        DialogOptionComponent optionComp = new DialogOptionComponent(this,
                option, editable);

        if ("weapon_specialist".equals(option.getName())) { //$NON-NLS-1$
            optionComp.addValue("None"); //$NON-NLS-1$
            //holy crap, do we really need to add every weapon?
            for (Enumeration<EquipmentType> i = EquipmentType.getAllTypes(); i.hasMoreElements();) {
                EquipmentType etype = i.nextElement();
                if(etype instanceof WeaponType) {
                    optionComp.addValue(etype.getName());
                }
            }
        }

        gridbag.setConstraints(optionComp, c);
        panOptions.add(optionComp);

        optionComps.add(optionComp);
    }

    private void setOptions() {
        IOption option;
        for (final Object newVar : optionComps) {
            DialogOptionComponent comp = (DialogOptionComponent) newVar;
            option = comp.getOption();
            if ((comp.getValue().equals("None"))) { // NON-NLS-$1
                pilot.getOptions().getOption(option.getName())
                .setValue("None"); // NON-NLS-$1
            } else {
                pilot.getOptions().getOption(option.getName())
                .setValue(comp.getValue());
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnOk;
    private javax.swing.JComboBox choiceType;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCommandB;
    private javax.swing.JLabel lblGunnery;
    private javax.swing.JLabel lblInitB;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNickname;
    private javax.swing.JLabel lblPiloting;
    private javax.swing.JPanel panButtons;
    private javax.swing.JPanel panOptions;
    private javax.swing.JTextField textCommandB;
    private javax.swing.JTextField textGunnery;
    private javax.swing.JTextField textInitB;
    private javax.swing.JTextField textName;
    private javax.swing.JTextField textNickname;
    private javax.swing.JTextField textPiloting;
    private javax.swing.JTextPane txtBio;
    // End of variables declaration//GEN-END:variables

    public void optionClicked(DialogOptionComponent arg0, IOption arg1, boolean arg2) {
        //IMplement me!!
    }

}
