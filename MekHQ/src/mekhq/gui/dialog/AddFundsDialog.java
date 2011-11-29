/*
 * AlertPopup.java
 *
 * Created on Jan 6, 2010, 10:46:02 PM
 */

package mekhq.gui.dialog;

import mekhq.campaign.finances.Transaction;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ResourceBundle;

/**
 *
 * @author natit
 */
public class AddFundsDialog extends javax.swing.JDialog implements FocusListener {
	private static final long serialVersionUID = -6946480787293179307L;

    private JFormattedTextField descriptionField;
    private JComboBox categoryCombo;
    private ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.AddFundsDialog");

	/** Creates new form AlertPopup */
    public AddFundsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        btnAddFunds = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        setTitle(resourceMap.getString("Form.title"));

        btnAddFunds.setText(resourceMap.getString("btnAddFunds.text")); // NOI18N
        btnAddFunds.setActionCommand(resourceMap.getString("btnAddFunds.actionCommand")); // NOI18N
        btnAddFunds.setName("btnAddFunds"); // NOI18N
        btnAddFunds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddFundsActionPerformed(evt);
            }
        });

        getContentPane().add(buildFieldsPanel(), BorderLayout.NORTH);
        getContentPane().add(btnAddFunds, BorderLayout.PAGE_END);

        setLocationRelativeTo(getParent());
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private JPanel buildFieldsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));

        
        jFormattedTextFieldFundsQuantity = new javax.swing.JFormattedTextField();
        jFormattedTextFieldFundsQuantity.addFocusListener(this);
        jFormattedTextFieldFundsQuantity.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(java.text.NumberFormat.getIntegerInstance())));
        jFormattedTextFieldFundsQuantity.setText(resourceMap.getString("jFormattedTextFieldFundsQuantity.text")); // NOI18N
        jFormattedTextFieldFundsQuantity.setToolTipText(resourceMap.getString("jFormattedTextFieldFundsQuantity.toolTipText")); // NOI18N
        jFormattedTextFieldFundsQuantity.setName("jFormattedTextFieldFundsQuantity"); // NOI18N
        jFormattedTextFieldFundsQuantity.setColumns(10);
        panel.add(jFormattedTextFieldFundsQuantity);

        categoryCombo = new JComboBox(Transaction.getCategoryList());
        categoryCombo.setSelectedItem(Transaction.getCategoryName(Transaction.C_MISC));
        categoryCombo.setToolTipText("The category the transaction falls into.");
        categoryCombo.setName("categoryCombo");
        panel.add(categoryCombo);

        descriptionField = new JFormattedTextField("Rich Uncle");
        descriptionField.addFocusListener(this);
        descriptionField.setToolTipText("Description of the transaction.");
        descriptionField.setName("descriptionField");
        descriptionField.setColumns(20);
        panel.add(descriptionField);

        return panel;
    }

    private void btnAddFundsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFundsActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btnAddFundsActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddFundsDialog dialog = new AddFundsDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public long getFundsQuantity () {
        long fundsQuantity = ((Long) jFormattedTextFieldFundsQuantity.getValue()).longValue();
        return fundsQuantity;
    }

    public String getFundsDescription() {
        return descriptionField.getText();
    }

    public int getCategory() {
        return Transaction.getCategoryIndex((String)categoryCombo.getSelectedItem());
    }

    private javax.swing.JButton btnAddFunds;
    private javax.swing.JFormattedTextField jFormattedTextFieldFundsQuantity;

    @Override
    public void focusGained(FocusEvent e) {
        if (jFormattedTextFieldFundsQuantity.equals(e.getSource())) {
            selectAllTextInField(jFormattedTextFieldFundsQuantity);
        } else if (descriptionField.equals(e.getSource())) {
            selectAllTextInField(descriptionField);
        }
    }

    private void selectAllTextInField(final JFormattedTextField field) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                field.selectAll();
            }
        });
    }

    @Override
    public void focusLost(FocusEvent e) {
        //not used
    }
}
