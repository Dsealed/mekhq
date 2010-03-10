/*
 * PartInventiry.java
 *
 * Copyright (c) 2009 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ.  If not, see <http://www.gnu.org/licenses/>.
 */

package mekhq.campaign;

import java.io.Serializable;

/**
 *
 * @author natit
 */
public class CampaignOptions implements Serializable {

    public final static int REPAIR_SYSTEM_STRATOPS = 0;
    public final static int REPAIR_SYSTEM_WARCHEST_CUSTOM = 1;
    public final static int REPAIR_SYSTEM_GENERIC_PARTS = 2;
    //FIXME: This needs to be localized
    public final static String [] REPAIR_SYSTEM_NAMES = {"Strat Ops", "Warchest Custom", "Generic Spare Parts"};

    private boolean useFactionModifiers = true;
    private double clanPriceModifier = 1.0;
    private boolean useEasierRefit = false;
    private int repairSystem = REPAIR_SYSTEM_STRATOPS;

    public CampaignOptions () {
        useFactionModifiers = true;
        clanPriceModifier = 1.0;
        useEasierRefit = false;
        repairSystem = REPAIR_SYSTEM_STRATOPS;    
    }

    public static String getRepairSystemName (int repairSystem) {
        return REPAIR_SYSTEM_NAMES[repairSystem];
    }
    
    public boolean useFactionModifiers() {
        return useFactionModifiers;
    }
    
    public void setFactionModifiers(boolean b) {
        this.useFactionModifiers = b;
    }
    
    public boolean useEasierRefit() {
        return useEasierRefit;
    }
    
    public void setEasierRefit(boolean b) {
        this.useEasierRefit = b;
    }
    
    public double getClanPriceModifier() {
        return clanPriceModifier;
    }
    
    public void setClanPriceModifier(double d) {
        this.clanPriceModifier = d;
    }
    
    public int getRepairSystem() {
        return repairSystem;
    }
    
    public void setRepairSystem(int i) {
        this.repairSystem = i;
    }
}
