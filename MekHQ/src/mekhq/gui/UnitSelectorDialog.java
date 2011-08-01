/*
 * UnitSelectorDialog.java
 *
 * Created on August 21, 2009, 4:26 PM
 */

package mekhq.gui;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import megamek.client.ui.swing.MechTileset;
import megamek.client.ui.swing.MechView;
import megamek.common.Entity;
import megamek.common.EntityWeightClass;
import megamek.common.MechFileParser;
import megamek.common.MechSummary;
import megamek.common.MechSummaryCache;
import megamek.common.TechConstants;
import megamek.common.UnitType;
import megamek.common.loaders.EntityLoadingException;
import mekhq.MekHQApp;
import mekhq.campaign.Campaign;
import mekhq.campaign.Unit;

import org.jdesktop.application.ResourceMap;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 * (code borrowed heavily from MegaMekLab UnitSelectorDialog
 */
public class UnitSelectorDialog extends JDialog {
	private static final long serialVersionUID = 304389003995743004L;

	private MechSummary[] mechs;

    private MechTableModel unitModel;

    private static MechTileset mt;

    Entity selectedUnit = null;

    private TableRowSorter<MechTableModel> sorter;

    private Campaign campaign;
    
    private CampaignGUI hqView;

    /** Creates new form UnitSelectorDialog */
    public UnitSelectorDialog(java.awt.Frame parent, boolean modal, Campaign campaign, CampaignGUI view) {
        super(parent, modal);
        unitModel = new MechTableModel();
        initComponents();

        this.campaign = campaign;

        this.hqView = view;
        
        MechSummary [] allMechs = MechSummaryCache.getInstance().getAllMechs();
        setMechs(allMechs);
        setLocationRelativeTo(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrTableUnits = new javax.swing.JScrollPane();
        tableUnits = new javax.swing.JTable();
        scrTxtUnitView = new javax.swing.JScrollPane();
        txtUnitView = new javax.swing.JTextPane();
        panelFilterBtns = new javax.swing.JPanel();
        lblType = new javax.swing.JLabel();
        comboType = new javax.swing.JComboBox();
        lblWeight = new javax.swing.JLabel();
        comboWeight = new javax.swing.JComboBox();
        lblUnitType = new javax.swing.JLabel();
        comboUnitType = new javax.swing.JComboBox();
        txtFilter = new javax.swing.JTextField();
        lblFilter = new javax.swing.JLabel();
        lblImage = new javax.swing.JLabel();
        checkCanon = new javax.swing.JCheckBox();
        panelOKBtns = new javax.swing.JPanel();
        btnBuy = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

		ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.UnitSelectorDialog");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        scrTableUnits.setMinimumSize(new java.awt.Dimension(500, 400));
        scrTableUnits.setName("scrTableUnits"); // NOI18N
        scrTableUnits.setPreferredSize(new java.awt.Dimension(500, 400));

        tableUnits.setFont(Font.decode(resourceMap.getString("tableUnits.font"))); // NOI18N
        tableUnits.setModel(unitModel);
        tableUnits.setName("tableUnits"); // NOI18N
        tableUnits.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<MechTableModel>(unitModel);
        tableUnits.setRowSorter(sorter);
        tableUnits.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                UnitChanged(evt);
            }
        });
        TableColumn column = null;
        for (int i = 0; i < MechTableModel.N_COL; i++) {
            column = tableUnits.getColumnModel().getColumn(i);
            if (i == MechTableModel.COL_CHASSIS) {
                column.setPreferredWidth(125);
            }
            else if(i == MechTableModel.COL_MODEL
                || i == MechTableModel.COL_COST) {
                column.setPreferredWidth(75);
            }
            else if(i == MechTableModel.COL_WEIGHT
                || i == MechTableModel.COL_BV) {
                column.setPreferredWidth(50);
            }
            else {
                column.setPreferredWidth(25);
            }
        }
        scrTableUnits.setViewportView(tableUnits);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(scrTableUnits, gridBagConstraints);

        scrTxtUnitView.setName("scrTxtUnitView"); // NOI18N

        txtUnitView.setBorder(null);
        txtUnitView.setContentType(resourceMap.getString("txtUnitView.contentType")); // NOI18N
        txtUnitView.setEditable(false);
        txtUnitView.setFont(Font.decode(resourceMap.getString("txtUnitView.font"))); // NOI18N
        txtUnitView.setMinimumSize(new java.awt.Dimension(300, 500));
        txtUnitView.setName("txtUnitView"); // NOI18N
        txtUnitView.setPreferredSize(new java.awt.Dimension(300, 500));
        scrTxtUnitView.setViewportView(txtUnitView);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(scrTxtUnitView, gridBagConstraints);

        panelFilterBtns.setMinimumSize(new java.awt.Dimension(300, 120));
        panelFilterBtns.setName("panelFilterBtns"); // NOI18N
        panelFilterBtns.setPreferredSize(new java.awt.Dimension(300, 120));
        panelFilterBtns.setLayout(new java.awt.GridBagLayout());

        lblType.setText(resourceMap.getString("lblType.text")); // NOI18N
        lblType.setName("lblType"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(lblType, gridBagConstraints);

        DefaultComboBoxModel techModel = new DefaultComboBoxModel();
        for (int i = 0; i < TechConstants.SIZE; i++) {
            techModel.addElement(TechConstants.getLevelDisplayableName(i));
        }
        techModel.setSelectedItem(TechConstants.getLevelDisplayableName(TechConstants.T_INTRO_BOXSET));
        comboType.setModel(techModel);
        comboType.setMinimumSize(new java.awt.Dimension(200, 27));
        comboType.setName("comboType"); // NOI18N
        comboType.setPreferredSize(new java.awt.Dimension(200, 27));
        comboType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(comboType, gridBagConstraints);

        lblWeight.setText(resourceMap.getString("lblWeight.text")); // NOI18N
        lblWeight.setName("lblWeight"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(lblWeight, gridBagConstraints);

        DefaultComboBoxModel weightModel = new DefaultComboBoxModel();
        for (int i = 0; i < EntityWeightClass.SIZE; i++) {
            weightModel.addElement(EntityWeightClass.getClassName(i));
        }
        weightModel.setSelectedItem(EntityWeightClass.getClassName(EntityWeightClass.WEIGHT_LIGHT));
        comboWeight.setModel(weightModel);
        comboWeight.setMinimumSize(new java.awt.Dimension(200, 27));
        comboWeight.setName("comboWeight"); // NOI18N
        comboWeight.setPreferredSize(new java.awt.Dimension(200, 27));
        comboWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboWeightActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(comboWeight, gridBagConstraints);

        lblUnitType.setText(resourceMap.getString("lblUnitType.text")); // NOI18N
        lblUnitType.setName("lblUnitType"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(lblUnitType, gridBagConstraints);

        DefaultComboBoxModel unitTypeModel = new DefaultComboBoxModel();
        for (int i = 0; i < UnitType.SIZE; i++) {
            unitTypeModel.addElement(UnitType.getTypeDisplayableName(i));
        }
        unitTypeModel.setSelectedItem(UnitType.getTypeName(UnitType.MEK));
        comboUnitType.setModel(unitTypeModel);
        comboUnitType.setMinimumSize(new java.awt.Dimension(200, 27));
        comboUnitType.setName("comboUnitType"); // NOI18N
        comboUnitType.setPreferredSize(new java.awt.Dimension(200, 27));
        comboUnitType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboUnitTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilterBtns.add(comboUnitType, gridBagConstraints);

        txtFilter.setText(resourceMap.getString("txtFilter.text")); // NOI18N
        txtFilter.setMinimumSize(new java.awt.Dimension(200, 28));
        txtFilter.setName("txtFilter"); // NOI18N
        txtFilter.setPreferredSize(new java.awt.Dimension(200, 28));
        txtFilter.getDocument().addDocumentListener(
            new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    filterUnits();
                }
                public void insertUpdate(DocumentEvent e) {
                    filterUnits();
                }
                public void removeUpdate(DocumentEvent e) {
                    filterUnits();
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 1;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            panelFilterBtns.add(txtFilter, gridBagConstraints);

            lblFilter.setText(resourceMap.getString("lblFilter.text")); // NOI18N
            lblFilter.setName("lblFilter"); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            panelFilterBtns.add(lblFilter, gridBagConstraints);

            lblImage.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
            lblImage.setName("lblImage"); // NOI18N
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 3;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridheight = 4;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.weighty = 1.0;
            panelFilterBtns.add(lblImage, gridBagConstraints);

            checkCanon.setSelected(true);
            checkCanon.setText(resourceMap.getString("checkCanon.text")); // NOI18N
            checkCanon.setName("checkCanon"); // NOI18N
            checkCanon.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    checkCanonActionPerformed(evt);
                }
            });
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 2;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            panelFilterBtns.add(checkCanon, gridBagConstraints);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
            getContentPane().add(panelFilterBtns, gridBagConstraints);

            panelOKBtns.setName("panelOKBtns"); // NOI18N
            panelOKBtns.setLayout(new java.awt.GridBagLayout());

            btnBuy.setText(resourceMap.getString("btnBuy.text")); // NOI18N
            btnBuy.setName("btnBuy"); // NOI18N
            btnBuy.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnBuyActionPerformed(evt);
                }
            });
            panelOKBtns.add(btnBuy, new java.awt.GridBagConstraints());

            btnClose.setText(resourceMap.getString("btnClose.text")); // NOI18N
            btnClose.setName("btnClose"); // NOI18N
            btnClose.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    btnCloseActionPerformed(evt);
                }
            });
            panelOKBtns.add(btnClose, new java.awt.GridBagConstraints());

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            getContentPane().add(panelOKBtns, gridBagConstraints);

            pack();
        }// </editor-fold>//GEN-END:initComponents

	private void comboUnitTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboUnitTypeActionPerformed
	    filterUnits();
	}//GEN-LAST:event_comboUnitTypeActionPerformed
	
	private void comboWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboWeightActionPerformed
	    filterUnits();
	}//GEN-LAST:event_comboWeightActionPerformed
	
	private void comboTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTypeActionPerformed
	    filterUnits();
	}//GEN-LAST:event_comboTypeActionPerformed
	
	private void btnBuyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuyActionPerformed
	    Entity en = getSelectedEntity();
	    if(null != en) {
	        if (campaign.isGM()) {
	            //FIXME: we should not do this here or players will accidently
	            //buy units for free with GM mode turned on. It should be a
	            //separate button
	            campaign.addUnit(en, false);
	        } else if (campaign.buyUnit(en, false)) {
	        	hqView.refreshUnitList();
	            hqView.refreshServicedUnitList();
	        } else {
	            ResourceMap resourceMap = MekHQApp.getApplication().getContext().getResourceMap(UnitSelectorDialog.class);
	            String text = resourceMap.getString("NotEnoughMoneyText.text");
	
	            NumberFormat numberFormat = DecimalFormat.getIntegerInstance();
	            int unitCost = (new Unit(en, campaign)).getBuyCost();
	            String unitCostString = numberFormat.format(unitCost) + " " + (unitCost!=0?"CBills":"CBill");
	            String fundsString = numberFormat.format(campaign.getFunds()) + " " + (campaign.getFunds()!=0?"CBills":"CBill");
	            
	            text += System.getProperty("line.separator");
	            text += "(Cost : " + unitCostString + ", Funds : " + fundsString + ")";
	            JOptionPane.showMessageDialog(null, text);
	        }
	    }
	
	    // Necessary if the used wants to buy the same unit twice without reselecting it
	    UnitChanged(null);
	}//GEN-LAST:event_btnBuyActionPerformed
	
	private void btnBuySelectActionPerformed(java.awt.event.ActionEvent evt) {                                       
	    setVisible(false);
	}
	
	private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
	    selectedUnit = null;
	    setVisible(false);
	}//GEN-LAST:event_btnCloseActionPerformed
	
	private void checkCanonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkCanonActionPerformed
	    filterUnits();
	}//GEN-LAST:event_checkCanonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                UnitSelectorDialog dialog = new UnitSelectorDialog(new javax.swing.JFrame(), true, null, null);
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

    private void filterUnits() {
        RowFilter<MechTableModel, Integer> unitTypeFilter = null;
        final int nType = comboType.getSelectedIndex();
        final int nClass = comboWeight.getSelectedIndex();
        final int nUnit = comboUnitType.getSelectedIndex();
        //If current expression doesn't parse, don't update.
        try {
            unitTypeFilter = new RowFilter<MechTableModel,Integer>() {
                @Override
                public boolean include(Entry<? extends MechTableModel, ? extends Integer> entry) {
                    MechTableModel mechModel = entry.getModel();
                    MechSummary mech = mechModel.getMechSummary(entry.getIdentifier());
                if (/* Weight */
                    (mech.getWeightClass() == nClass) &&
                            (mech.isCanon() || !checkCanon.isSelected()) &&
                /*
                 * Technology Level
                 */
                ((nType == TechConstants.T_ALL)
                            || (nType == mech.getType())
                            || ((nType == TechConstants.T_IS_TW_ALL)
                                && ((mech.getType() <= TechConstants.T_IS_TW_NON_BOX) || (mech.getType() == TechConstants.T_INTRO_BOXSET)))
                            || ((nType == TechConstants.T_TW_ALL) && ((mech.getType() <= TechConstants.T_IS_TW_NON_BOX)
                                || (mech.getType() <= TechConstants.T_INTRO_BOXSET) || (mech.getType() <= TechConstants.T_CLAN_TW))))
                                && ((nUnit == UnitType.SIZE) || mech.getUnitType().equals(UnitType.getTypeName(nUnit)))) {
                    //yuck, I have to pull up a full Entity to get MechView to search in
                    //TODO: why not put mechview into the mech summary itself?
                    if(txtFilter.getText().length() > 0) {
                        //TODO: this search routine is too slow
                        //I think putting a copy of the mechreadout in
                        //the mechsummary would speed things up enormously
                        //NOTE: now getting weirdness on txtFilter when I do this

                        String text = txtFilter.getText();
                        //String [] ind_words = text.split(" "); //split with regex as space
                        /*
                        MechView mv = null;
                        try {
                                Entity entity = new MechFileParser(mech.getSourceFile(), mech.getEntryName()).getEntity();
                                mv = new MechView(entity, true);
                        } catch (EntityLoadingException ex) {
                            // do nothing, I guess
                        }
                        if(null == mv) {
                            return false;
                        }
                         * */
                        /**
                        boolean match = true;
                        for(int i = 0; i < ind_words.length; i++) {
                            if(!mv.getMechReadout().contains(ind_words[i])) {
                                match = false;
                                break;
                            }
                        }
                        return match;
                        */
                        return mech.getName().contains(text);
                    }
                    return true;
                }
                return false;
                }
            };
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(unitTypeFilter);
    }

    private void UnitChanged(javax.swing.event.ListSelectionEvent evt) {
        int view = tableUnits.getSelectedRow();
        if(view < 0) {
            //selection got filtered away
            selectedUnit = null;
            refreshUnitView();
            return;
        }
        int selected = tableUnits.convertRowIndexToModel(view);
        // else
        MechSummary ms = mechs[selected];
        try {
             // For some unknown reason the base path gets screwed up after you
             // print so this sets the source file to the full path.
             Entity entity = new MechFileParser(ms.getSourceFile(), ms.getEntryName()).getEntity();
             selectedUnit = entity;
             refreshUnitView();
        } catch (EntityLoadingException ex) {
            selectedUnit = null;
            MekHQApp.logError("Unable to load mech: " + ms.getSourceFile() + ": " + ms.getEntryName() + ": " + ex.getMessage());
            MekHQApp.logError(ex);
            refreshUnitView();
            return;
       }
    }

     void refreshUnitView() {

        boolean populateTextFields = true;

        // null entity, so load a default unit.
        if (selectedUnit == null) {
            txtUnitView.setText("");
            lblImage.setIcon(null);
            return;
        }

        MechView mechView = null;
        try {
            mechView = new MechView(selectedUnit, true);
        } catch (Exception e) {
            // error unit didn't load right. this is bad news.
            populateTextFields = false;
        }
        txtUnitView.setEditable(false);
        if (populateTextFields && (mechView != null)) {
            txtUnitView.setText(mechView.getMechReadout());
        } else {
            txtUnitView.setText("No Unit Selected");
        }
        txtUnitView.setCaretPosition(0);

        if (mt == null) {
            mt = new MechTileset("data/images/units/");
            try {
                mt.loadFromFile("mechset.txt");
            } catch (IOException ex) {
            	MekHQApp.logError(ex);
                //TODO: do something here
                return;
            }
        }// end if(null tileset)
        Image unitImage = mt.imageFor(selectedUnit, lblImage, -1);
        if(null != unitImage) {
            lblImage.setIcon(new ImageIcon(unitImage));
        }
    }

     public Entity getSelectedEntity() {
        return selectedUnit;

    }

     public void setMechs (MechSummary [] m) {
         this.mechs = m;

         // break out if there are no units to filter
         if (mechs == null) {
             System.err.println("No units to filter!");
         } else {
             unitModel.setData(mechs);
         }
         filterUnits();
     }

     public void restrictToChassis (String chassis) {
         ArrayList<MechSummary> allowedMechs = new ArrayList<MechSummary>();
         for (MechSummary mechSummary : mechs) {
             if (mechSummary.getChassis().equals(chassis))
                 allowedMechs.add(mechSummary);
         }
         setMechs(allowedMechs.toArray(new MechSummary[0]));
     }

     public void restrictToYear (int year) {
         ArrayList<MechSummary> allowedMechs = new ArrayList<MechSummary>();
         for (MechSummary mechSummary : mechs) {
             if (mechSummary.getYear()<=year)
                 allowedMechs.add(mechSummary);
         }
         setMechs(allowedMechs.toArray(new MechSummary[0]));
     }

    public void changeBuyBtnToSelectBtn () {
        for (ActionListener actionListener : btnBuy.getActionListeners()) {
            btnBuy.removeActionListener(actionListener);
        }

        ResourceMap resourceMap = MekHQApp.getInstance().getContext().getResourceMap(UnitSelectorDialog.class);
        btnBuy.setText(resourceMap.getString("btnBuy.textSelect")); // NOI18N

        btnBuy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuySelectActionPerformed(evt);
            }
        });
    }

    public JComboBox getComboType() {
        return comboType;
    }

    public JComboBox getComboUnitType() {
        return comboUnitType;
    }

    public JComboBox getComboWeight() {
        return comboWeight;
    }


    /**
	 * A table model for displaying work items
	 */
	public class MechTableModel extends AbstractTableModel {
			private static final long serialVersionUID = 8472587304279640434L;
			private final static int COL_MODEL = 0;
	        private final static int COL_CHASSIS = 1;
	        private final static int COL_WEIGHT = 2;
	        private final static int COL_BV = 3;
	        private final static int COL_YEAR = 4;
	        private final static int COL_COST = 5;
	        private final static int N_COL = 6;
	
	        private MechSummary[] data = new MechSummary[0];
	
	        public MechTableModel() {
	            //this.columnNames = new String[] {"Model", "Chassis"};
	            //this.data = new MechSummary[0];
	        }
	
	        public int getRowCount() {
	            return data.length;
	        }
	
	        public int getColumnCount() {
	            return N_COL;
	        }
	
	        @Override
	        public String getColumnName(int column) {
	            switch(column) {
	                case COL_MODEL:
	                    return "Model";
	                case COL_CHASSIS:
	                    return "Chassis";
	                case COL_WEIGHT:
	                    return "Weight";
	                case COL_BV:
	                    return "BV";
	                case COL_YEAR:
	                    return "Year";
	                case COL_COST:
	                    return "Price";
	                default:
	                    return "?";
	            }
	        }
	
	        @Override
	        public Class<? extends Object> getColumnClass(int c) {
	            return getValueAt(0, c).getClass();
	        }
	
	        @Override
	        public boolean isCellEditable(int row, int col) {
	            return false;
	        }
	
	        public MechSummary getMechSummary(int i) {
	            return data[i];
	        }
	
	        //fill table with values
	        public void setData(MechSummary[] ms) {
	            data = ms;
	            fireTableDataChanged();
	        }
	
	        public Object getValueAt(int row, int col) {
	            MechSummary ms = data[row];
	            if(col == COL_MODEL) {
	                return ms.getModel();
	            }
	            if(col == COL_CHASSIS) {
	                return ms.getChassis();
	            }
	            if(col == COL_WEIGHT) {
	                return ms.getTons();
	            }
	            if(col == COL_BV) {
	                return ms.getBV();
	            }
	            if(col == COL_YEAR) {
	                return ms.getYear();
	            }
	            if(col == COL_COST) {
	                //return NumberFormat.getInstance().format(ms.getCost());
	                return ms.getCost();
	            }
	            return "?";
	        }
	
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuy;
    private javax.swing.JButton btnClose;
    private javax.swing.JCheckBox checkCanon;
    private javax.swing.JComboBox comboType;
    private javax.swing.JComboBox comboUnitType;
    private javax.swing.JComboBox comboWeight;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblImage;
    private javax.swing.JLabel lblType;
    private javax.swing.JLabel lblUnitType;
    private javax.swing.JLabel lblWeight;
    private javax.swing.JPanel panelFilterBtns;
    private javax.swing.JPanel panelOKBtns;
    private javax.swing.JScrollPane scrTableUnits;
    private javax.swing.JScrollPane scrTxtUnitView;
    private javax.swing.JTable tableUnits;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextPane txtUnitView;
    // End of variables declaration//GEN-END:variables

}
