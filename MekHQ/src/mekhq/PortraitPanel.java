/*
 * CamoPanel.java
 *
 * Created on October 1, 2009, 4:04 PM
 */

package mekhq;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;
import megamek.client.ui.swing.util.ImageFileFactory;
import megamek.common.Pilot;
import megamek.common.util.DirectoryItems;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class PortraitPanel extends javax.swing.JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3724175393116586310L;
	private DirectoryItems portraits;
    
    /** Creates new form CamoPanel */
    public PortraitPanel() {
         try {
            portraits = new DirectoryItems(new File("data/images/portraits"), "", //$NON-NLS-1$ //$NON-NLS-2$
                    ImageFileFactory.getInstance());
        } catch (Exception e) {
            portraits = null;
        }
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
        java.awt.GridBagConstraints gridBagConstraints;

        lblImage = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(PortraitPanel.class);
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setName("lblImage"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(lblImage, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    public void setText(String text) {
        lblImage.setText(text);
    }
    
    //public void setImage(Image image) {
      //  lblImage.setIcon(new ImageIcon(image));
    //}
    
    public void setImage(String category, String name) {

        if (null == category
                || name.equals(Pilot.PORTRAIT_NONE)) {
            return;
        }

        // Try to get the portrait file.
        try {

            // Translate the root portrait directory name.
            if (Pilot.ROOT_PORTRAIT.equals(category))
                category = ""; //$NON-NLS-1$
            Image portrait = (Image) portraits.getItem(category, name);
            lblImage.setIcon(new ImageIcon(portrait));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

}
