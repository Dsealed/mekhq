/*
 * Armor.java
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

package mekhq.campaign.parts;

import java.util.ArrayList;
import megamek.common.EquipmentType;
import megamek.common.TechConstants;
import mekhq.campaign.Faction;
import mekhq.campaign.work.ArmorReplacement;
import mekhq.campaign.work.ReplacementItem;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class Armor extends Part {

    protected int type;
    protected int amount;
    
    public Armor(boolean salvage, int tonnage, int t, int points) {
        // Amount is used for armor quantity, not tonnage
        super(false, tonnage);
        this.type = t;
        this.name = EquipmentType.getArmorTypeName(type) + " Armor";
        this.amount = points;

        // TechBase needs to be set to calculate cost more precisely
        computeCost();
    }

    private void computeCost () {
        this.cost = (int) Math.round(getArmorWeight(getAmount()) * EquipmentType.getArmorCost(getType()));
    }
    
    public int getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        computeCost();
    }
    
    @Override
    public String getDesc() {
        return super.getDesc() + " (" + getAmount() + ")";
    }
    
    @Override
    public boolean canBeUsedBy(ReplacementItem task) {
        return task instanceof ArmorReplacement 
                && ((ArmorReplacement)task).getUnit().getEntity().getArmorType() == type;
    }

    @Override
    public boolean isSamePartTypeAndStatus (Part part) {
        return part instanceof Armor
                && getName().equals(part.getName())
                && getStatus().equals(part.getStatus())
                && getType() == ((Armor)part).getType();
    }

    @Override
    public int getPartType() {
        return PART_TYPE_ARMOR;
    }

    @Override
    public boolean isClanTechBase() {
        // Armor tech base is not used (Clan/IS can use each other's armor for now)
        // TODO Set Tech base correctly for armor
        // Clan FF and IS FF do not have the same armor points per ton
        return false;
    }

    @Override
    public int getTech () {
        // Armor tech base is not used (Clan/IS can use each other's armor for now)
        // TODO Set Tech base correctly for armor
        // Clan FF and IS FF do not have the same armor points per ton
        return TechConstants.T_INTRO_BOXSET;
    }

    @Override
    public ArrayList<String> getPotentialSSWNames(int faction) {
        ArrayList<String> sswNames = new ArrayList<String>();

        // The tech base of the part doesn't matter (Clan and IS can use each other's Ferro-Fibrous armor)
        // However the tech base of the faction is important : Clans get Ferro-Fibrous armor before IS
        String techBase = (Faction.isClanFaction(faction) ? "(CL)" : "(IS)");

        String sswName = getName();

        sswNames.add(techBase + " " + sswName);
        sswNames.add(sswName);

        return sswNames;
    }

    public double getArmorWeight(int points) {
        // from megamek.common.Entity.getArmorWeight()
        
        // this roundabout method is actually necessary to avoid rounding
        // weirdness. Yeah, it's dumb.

        boolean isClanArmor = false;
        if (isClanTechBase())
            isClanArmor= true;

        double armorPointMultiplier = EquipmentType.getArmorPointMultiplier(getType(), isClanArmor);
        double armorPerTon = 16.0 * armorPointMultiplier;
        if (getType() == EquipmentType.T_ARMOR_HARDENED) {
            armorPerTon = 8.0;
        }
        
        double armorWeight = points / armorPerTon;
        armorWeight = Math.ceil(armorWeight * 2.0) / 2.0;
        return armorWeight;
    }

    public int getCost (int amount) {
        return (int) Math.round(getArmorWeight(amount) * EquipmentType.getArmorCost(getType()));
    }

    @Override
    public String getSaveString () {
        return getName() + ";" + getTonnage() + ";" + getType() + ";" + getAmount();
    }
}
