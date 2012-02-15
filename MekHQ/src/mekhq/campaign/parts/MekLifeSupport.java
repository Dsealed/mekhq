/*
 * MekLifeSupport.java
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

import java.io.PrintWriter;

import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.EquipmentType;
import megamek.common.Mech;
import megamek.common.TechConstants;
import mekhq.campaign.Campaign;
import mekhq.campaign.personnel.SkillType;

import org.w3c.dom.Node;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekLifeSupport extends Part {
	private static final long serialVersionUID = -1989526319692474127L;

	public MekLifeSupport() {
		this(0, null);
	}
	
	public MekLifeSupport(int tonnage, Campaign c) {
        super(tonnage, c);
        this.name = "Mech Life Support System";
    }
	
	public MekLifeSupport clone() {
		MekLifeSupport clone = new MekLifeSupport(getUnitTonnage(), campaign);
		clone.hits = this.hits;
    	clone.time = this.time;
		return clone;
	}
	
	@Override
	public double getTonnage() {
		//TODO: what should this tonnage be?
		return 0;
	}
	
	@Override
	public long getStickerPrice() {
		return 50000;
	}

    @Override
    public boolean isSamePartTypeAndStatus (Part part) {
    	if(isReservedForRefit() || isBeingWorkedOn()
				|| part.isReservedForRefit() || part.isBeingWorkedOn()) {
    		return false;
    	}
        return part instanceof MekLifeSupport && this.getHits() == part.getHits()
				&& part.getSkillMin() == this.getSkillMin();
    }
    
    @Override
	public int getTechBase() {
		return T_BOTH;
	}

    @Override
    public int getPartType() {
        return PART_TYPE_MEK_LIFE_SUPPORT;
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent) {
		writeToXmlBegin(pw1, indent);
		writeToXmlEnd(pw1, indent);
	}

	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		// Do nothing - no fields to load.
	}

	@Override
	public int getAvailability(int era) {
		return EquipmentType.RATING_C;
	}

	@Override
	public int getTechRating() {
		return EquipmentType.RATING_C;
	}

	@Override
	public int getTechLevel() {
		return TechConstants.T_INTRO_BOXSET;
	}
	
	@Override
	public void fix() {
		super.fix();
		if(null != unit) {
			unit.repairSystem(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT);
		}
	}

	@Override
	public Part getMissingPart() {
		return new MissingMekLifeSupport(getUnitTonnage(), campaign);
	}

	@Override
	public void remove(boolean salvage) {
		if(null != unit) {
			unit.destroySystem(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT);
			Part spare = campaign.checkForExistingSparePart(this);
			if(!salvage) {
				campaign.removePart(this);
			} else if(null != spare) {
				spare.incrementQuantity();
				campaign.removePart(this);
			}
			unit.removePart(this);
			Part missing = getMissingPart();
			unit.addPart(missing);
			campaign.addPart(missing);
		}
		setSalvaging(false);
		setUnit(null);
		updateConditionFromEntity();
	}

	@Override
	public void updateConditionFromEntity() {
		if(null != unit) {
			Entity entity = unit.getEntity();
			for (int i = 0; i < entity.locations(); i++) {
				if (entity.getNumberOfCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT, i) > 0) {
					if (entity.isSystemRepairable(Mech.SYSTEM_LIFE_SUPPORT, i)) {					
						hits = entity.getDamagedCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT, i);	
						break;
					} else {
						remove(false);
						return;
					}
				}
			}
		}
		if(hits == 0) {
			time = 0;
			difficulty = 0;
		} 
		else if(hits == 1) {
			time = 60;
			difficulty = -1;
		}
		else if(hits > 1) {
			time = 120;
			difficulty = 1;
		}
		if(isSalvaging()) {
			this.time = 180;
			this.difficulty = -1;
		}
		
	}

	@Override
	public boolean needsFixing() {
		return hits > 0;
	}
	
	@Override
	public void updateConditionFromPart() {
		if(null != unit) {
			if(hits == 0) {
				unit.repairSystem(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT);
			} else {
				for(int i = 0; i < hits; i++) {
					unit.hitSystem(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT);
				}
			}
		}
	}
	
	@Override
    public String checkFixable() {
		if(isSalvaging()) {
			return null;
		}
        for(int i = 0; i < unit.getEntity().locations(); i++) {
            if(unit.getEntity().getNumberOfCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT, i) > 0) {
            	if(unit.isLocationBreached(i)) {
            		return unit.getEntity().getLocationName(i) + " is breached.";
            	}
            	if(unit.isLocationDestroyed(i)) {
            		return unit.getEntity().getLocationName(i) + " is destroyed.";
            	}
            }
        }
        return null;
    }
	
	@Override
	public boolean isMountedOnDestroyedLocation() {
		if(null == unit) {
			return false;
		}
		for(int i = 0; i < unit.getEntity().locations(); i++) {
			 if(unit.getEntity().getNumberOfCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT, i) > 0
					 && unit.isLocationDestroyed(i)) {
				 return true;
			 }
		 }
		return false;
	}
	
	@Override
	public boolean isPartForCriticalSlot(int index, int loc) {
		return Mech.SYSTEM_LIFE_SUPPORT == index;
	}
	
	@Override
	public boolean isRightTechType(String skillType) {
		return skillType.equals(SkillType.S_TECH_MECH);
	}
}
