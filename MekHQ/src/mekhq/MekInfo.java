/*
 * MekInfo.java
 *
 * Created on July 24, 2009, 2:56 PM
 */

package mekhq;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import megamek.client.ui.swing.MechTileset;
import megamek.client.ui.swing.util.ImageFileFactory;
import megamek.client.ui.swing.util.PlayerColors;
import megamek.common.Player;
import megamek.common.util.DirectoryItems;
import mekhq.campaign.Campaign;
import mekhq.campaign.Unit;

/**
 * This JPanel borrows extensively from the MechInfo in MekWars
 * @author  Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekInfo extends JPanel {
    
    protected static MechTileset mt;
    private DirectoryItems camos;
    
    /** Creates new form MekInfo */
    public MekInfo() {
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

        lblImage = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(1, 0));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(mekhq.MekHQApp.class).getContext().getResourceMap(MekInfo.class);
        lblImage.setIcon(resourceMap.getIcon("lblImage.icon")); // NOI18N
        lblImage.setText(resourceMap.getString("lblImage.text")); // NOI18N
        lblImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        lblImage.setName("lblImage"); // NOI18N
        add(lblImage);
    }// </editor-fold>//GEN-END:initComponents


    public void setText(String text) {
        lblImage.setText(text);
    }
    
    public void setImage(Image img) {
        lblImage.setIcon(new ImageIcon(img));
    }
    
    public void setUnit(Unit u) {
        Image unit = getImageFor(u, lblImage);     
        setImage(unit);
    }
    
    public void select() {
        lblImage.setBorder(new javax.swing.border.LineBorder(Color.BLACK, 5, true));
    }
    
    public void unselect() {
        lblImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    }
    
    public Image getImageFor(Unit u, Component c) {
        
        if (mt == null) {
            mt = new MechTileset("data/images/units/");
            try {
                mt.loadFromFile("mechset.txt");
            } catch (IOException ex) {
                //TODO: do something here
            }
        }// end if(null tileset)
        Image base = mt.imageFor(u.getEntity(), c, -1);
        int tint = PlayerColors.getColorRGB(u.campaign.getColorIndex());
        EntityImage entityImage = new EntityImage(base, tint, getCamo(u.campaign), c);
        return entityImage.loadPreviewImage();
    }
    
    public Image getCamo(Campaign c) {

        // Return a null if the campaign has selected no camo file.
        if (null == c.getCamoCategory()
                || Player.NO_CAMO.equals(c.getCamoCategory())) {
            return null;
        }

        // Try to get the player's camo file.
        Image camo = null;
        try {

            // Translate the root camo directory name.
            String category = c.getCamoCategory();
            if (Player.ROOT_CAMO.equals(category))
                category = ""; //$NON-NLS-1$
            camo = (Image) camos.getItem(category, c.getCamoFileName());

        } catch (Exception err) {
            err.printStackTrace();
        }
        return camo;
    }
    
    /**
     * A class to handle the image permutations for an entity (borrowed from MegaMek#TileSetManager
     */
    private class EntityImage {
        private Image base;
        private Image wreck;
        private Image icon;
        int tint;
        private Image camo;
        private Component parent;

        private static final int IMG_WIDTH = 84;
        private static final int IMG_HEIGHT = 72;
        private static final int IMG_SIZE = IMG_WIDTH * IMG_HEIGHT;

        public EntityImage(Image base, int tint, Image camo, Component comp) {
            this(base, null, tint, camo, comp);
        }

        public EntityImage(Image base, Image wreck, int tint, Image camo, Component comp) {
            this.base = base;
            this.tint = tint;
            this.camo = camo;
            this.parent = comp;
            this.wreck = wreck;
        }

        public Image loadPreviewImage() {
            base = applyColor(base);
            return base;
        }

        public Image getBase() {
            return base;
        }

        public Image getIcon() {
            return icon;
        }

        private Image applyColor(Image image) {
            Image iMech;
            boolean useCamo = (camo != null);

            iMech = image;

            int[] pMech = new int[IMG_SIZE];
            int[] pCamo = new int[IMG_SIZE];
            PixelGrabber pgMech = new PixelGrabber(iMech, 0, 0, IMG_WIDTH, IMG_HEIGHT, pMech, 0, IMG_WIDTH);

            try {
                pgMech.grabPixels();
            } catch (InterruptedException e) {
                System.err
                        .println("EntityImage.applyColor(): Failed to grab pixels for mech image." + e.getMessage()); //$NON-NLS-1$
                return image;
            }
            if ((pgMech.getStatus() & ImageObserver.ABORT) != 0) {
                System.err
                        .println("EntityImage.applyColor(): Failed to grab pixels for mech image. ImageObserver aborted."); //$NON-NLS-1$
                return image;
            }

            if (useCamo) {
                PixelGrabber pgCamo = new PixelGrabber(camo, 0, 0, IMG_WIDTH,
                        IMG_HEIGHT, pCamo, 0, IMG_WIDTH);
                try {
                    pgCamo.grabPixels();
                } catch (InterruptedException e) {
                    System.err
                            .println("EntityImage.applyColor(): Failed to grab pixels for camo image." + e.getMessage()); //$NON-NLS-1$
                    return image;
                }
                if ((pgCamo.getStatus() & ImageObserver.ABORT) != 0) {
                    System.err
                            .println("EntityImage.applyColor(): Failed to grab pixels for mech image. ImageObserver aborted."); //$NON-NLS-1$
                    return image;
                }
            }

            for (int i = 0; i < IMG_SIZE; i++) {
                int pixel = pMech[i];
                int alpha = (pixel >> 24) & 0xff;

                if (alpha != 0) {
                    int pixel1 = useCamo ? pCamo[i] : tint;
                    float red1 = ((float) ((pixel1 >> 16) & 0xff)) / 255;
                    float green1 = ((float) ((pixel1 >> 8) & 0xff)) / 255;
                    float blue1 = ((float) ((pixel1) & 0xff)) / 255;

                    float black = ((pMech[i]) & 0xff);

                    int red2 = Math.round(red1 * black);
                    int green2 = Math.round(green1 * black);
                    int blue2 = Math.round(blue1 * black);

                    pMech[i] = (alpha << 24) | (red2 << 16) | (green2 << 8)
                            | blue2;
                }
            }

            image = parent.createImage(new MemoryImageSource(IMG_WIDTH,
                    IMG_HEIGHT, pMech, 0, IMG_WIDTH));
            return image;
        }
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblImage;
    // End of variables declaration//GEN-END:variables

}
