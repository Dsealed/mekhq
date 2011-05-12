/*
 * TaskInfo.java
 *
 * Created on July 26, 2009, 11:32 PM
 */

package mekhq;

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import megamek.client.ui.swing.util.ImageFileFactory;
import megamek.common.Pilot;
import megamek.common.util.DirectoryItems;
import mekhq.campaign.personnel.Person;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class PersonInfo extends javax.swing.JPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4433136616647946125L;
	// keep track of portrait images
    private DirectoryItems portraits;
    
    /** Creates new form TaskInfo */
    public PersonInfo() {     
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
    private void initComponents() {

        lblImage = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(1, 0));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(PersonInfo.class);
        lblImage.setIcon(resourceMap.getIcon("lblImage.icon")); // NOI18N
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lblImage.setName("lblImage"); // NOI18N
        add(lblImage);
    }// </editor-fold>//GEN-END:initComponents

    public void setText(String text) {
        lblImage.setText(text);
    }
    
    public void setImage(Image img) {
        lblImage.setIcon(new ImageIcon(img));
    }
    
    public void select() {
        lblImage.setBorder(new javax.swing.border.LineBorder(Color.BLACK, 5, true));
    }
    
    public void unselect() {
        lblImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    }
    
    /**
     * set the portrait for the given person.
     *
     * @return The <code>Image</code> of the pilot's portrait. This value
     *         will be <code>null</code> if no portrait was selected
     *          or if there was an error loading it.
     */
    public void setPortrait(Person person) {

        String category = person.getPortraitCategory();
        String file = person.getPortraitFileName();

        if(Pilot.ROOT_PORTRAIT.equals(category)) {
            category = "";
        }

        // Return a null if the player has selected no portrait file.
        if ((null == category) || (null == file) || Pilot.PORTRAIT_NONE.equals(file)) {
            return;
        }

        // Try to get the player's portrait file.
        Image portrait = null;
        try {
            portrait = (Image) portraits.getItem(category, file);
            //make sure no images are longer than 72 pixels
            if(null != portrait) {
                portrait = portrait.getScaledInstance(-1, 65, Image.SCALE_DEFAULT);
                this.setImage(portrait);
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

}
