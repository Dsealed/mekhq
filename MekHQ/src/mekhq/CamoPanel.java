/*
 * CamoPanel.java
 *
 * Created on October 1, 2009, 4:04 PM
 */

package mekhq;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import megamek.client.ui.AWT.util.PlayerColors;
import megamek.client.ui.swing.util.ImageFileFactory;
import megamek.common.Player;
import megamek.common.util.DirectoryItems;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class CamoPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = -4106360800407452822L;
	private DirectoryItems camos;
    
    /** Creates new form CamoPanel */
    public CamoPanel() {
         try {
            camos = new DirectoryItems(new File("data/images/camo"), "", //$NON-NLS-1$ //$NON-NLS-2$
                    ImageFileFactory.getInstance());
        } catch (Exception e) {
            camos = null;
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(CamoPanel.class);
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
    
    public void setImage(String category, String name, int colorInd) {

        if (null == category) {
            return;
        }
        
        if(Player.NO_CAMO.equals(category)) {
            if (colorInd == -1) {
                colorInd = 0;
            }
            BufferedImage tempImage = new BufferedImage(84, 72,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = tempImage.createGraphics();
            graphics.setColor(PlayerColors.getColor(colorInd));
            graphics.fillRect(0, 0, 84, 72);
            lblImage.setIcon(new ImageIcon(tempImage));
            return;
        }

        // Try to get the camo file.
        try {

            // Translate the root camo directory name.
            if (Player.ROOT_CAMO.equals(category))
                category = ""; //$NON-NLS-1$
            Image camo = (Image) camos.getItem(category, name);
            lblImage.setIcon(new ImageIcon(camo));
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

}
