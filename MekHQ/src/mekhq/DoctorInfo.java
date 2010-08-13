/*
 * TaskInfo.java
 *
 * Created on July 26, 2009, 11:32 PM
 */

package mekhq;

import java.awt.Color;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class DoctorInfo extends javax.swing.JPanel {
	private static final long serialVersionUID = -2890605770494694319L;

	/** Creates new form TaskInfo */
    public DoctorInfo() {
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

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(DoctorInfo.class);
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
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

}
