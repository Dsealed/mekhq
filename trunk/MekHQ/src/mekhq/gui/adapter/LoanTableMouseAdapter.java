package mekhq.gui.adapter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.UUID;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputAdapter;

import mekhq.campaign.finances.Loan;
import mekhq.campaign.parts.Part;
import mekhq.gui.CampaignGUI;
import mekhq.gui.dialog.PayCollateralDialog;

public class LoanTableMouseAdapter extends MouseInputAdapter implements
        ActionListener {
    private CampaignGUI gui;

    public LoanTableMouseAdapter(CampaignGUI gui) {
        super();
        this.gui = gui;
    }

    public void actionPerformed(ActionEvent action) {
        String command = action.getActionCommand();
        int row = gui.getLoanTable().getSelectedRow();
        if (row < 0) {
            return;
        }
        Loan selectedLoan = gui.getLoanModel().getLoan(gui.getLoanTable()
                .convertRowIndexToModel(row));
        if (null == selectedLoan) {
            return;
        }
        if (command.equalsIgnoreCase("DEFAULT")) {
            if (0 == JOptionPane
                    .showConfirmDialog(
                            null,
                            "Defaulting on this loan will affect your unit rating the same as a contract breach.\nDo you wish to proceed?",
                            "Default on " + selectedLoan.getDescription()
                                    + "?", JOptionPane.YES_NO_OPTION)) {
                PayCollateralDialog pcd = new PayCollateralDialog(
                        gui.getFrame(), true, gui.getCampaign(), selectedLoan);
                pcd.setVisible(true);
                if (pcd.wasCancelled()) {
                    return;
                }
                gui.getCampaign().getFinances().defaultOnLoan(selectedLoan,
                        pcd.wasPaid());
                if (pcd.wasPaid()) {
                    for (UUID id : pcd.getUnits()) {
                        gui.getCampaign().removeUnit(id);
                    }
                    for (int[] part : pcd.getParts()) {
                        Part p = gui.getCampaign().getPart(part[0]);
                        if (null != p) {
                            int quantity = part[1];
                            while (quantity > 0 && p.getQuantity() > 0) {
                                p.decrementQuantity();
                                quantity--;
                            }
                        }
                    }
                    gui.getCampaign().getFinances().setAssets(
                            pcd.getRemainingAssets());
                }
                gui.refreshFinancialTransactions();
                gui.refreshUnitList();
                gui.refreshReport();
                gui.refreshPartsList();
                gui.refreshOverview();
            }
        } else if (command.equalsIgnoreCase("PAY_BALANCE")) {
            gui.getCampaign().payOffLoan(selectedLoan);
            gui.refreshFinancialTransactions();
            gui.refreshReport();
        } else if (command.equalsIgnoreCase("REMOVE")) {
            gui.getCampaign().getFinances().removeLoan(selectedLoan);
            gui.refreshFinancialTransactions();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        if (e.isPopupTrigger()) {
            if (gui.getLoanTable().getSelectedRowCount() == 0) {
                return;
            }
            int row = gui.getLoanTable().getSelectedRow();
            Loan loan = gui.getLoanModel().getLoan(gui.getLoanTable()
                    .convertRowIndexToModel(row));
            JMenuItem menuItem = null;
            JMenu menu = null;
            // **lets fill the pop up menu**//
            menuItem = new JMenuItem("Pay Off Full Balance ("
                    + DecimalFormat.getInstance().format(
                            loan.getRemainingValue()) + ")");
            menuItem.setActionCommand("PAY_BALANCE");
            menuItem.setEnabled(gui.getCampaign().getFunds() >= loan
                    .getRemainingValue());
            menuItem.addActionListener(this);
            popup.add(menuItem);
            menuItem = new JMenuItem("Default on This Loan");
            menuItem.setActionCommand("DEFAULT");
            menuItem.addActionListener(this);
            popup.add(menuItem);
            // GM mode
            menu = new JMenu("GM Mode");
            // remove part
            menuItem = new JMenuItem("Remove Loan");
            menuItem.setActionCommand("REMOVE");
            menuItem.addActionListener(this);
            menuItem.setEnabled(gui.getCampaign().isGM());
            menu.add(menuItem);
            // end
            popup.addSeparator();
            popup.add(menu);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
