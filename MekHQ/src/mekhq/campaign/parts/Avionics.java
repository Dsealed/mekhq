/*
 * Avionics.java
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

import megamek.common.Aero;
import megamek.common.CriticalSlot;
import megamek.common.EquipmentType;
import megamek.common.Mech;
import megamek.common.TechConstants;
import mekhq.campaign.MekHqXmlUtil;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class Avionics extends Part {

	/**
	 * 
	 */
	private static final long serialVersionUID = -717866644605314883L;

	public Avionics() {
    	this(0);
    }
    
    public Avionics(int tonnage) {
        super(tonnage);
        this.name = "Avionics";
    }
        
	@Override
	public void updateConditionFromEntity() {
		if(null != unit && unit.getEntity() instanceof Aero) {
			hits = ((Aero)unit.getEntity()).getAvionicsHits();
		}
		if(hits > 0) {
			time = 480;
			difficulty = 0;
		} else {
			time = 0;
			difficulty = 0;
		}
		if(isSalvaging()) {
			time = 4800;
			difficulty = 1;
		}
	}

	@Override
	public void updateConditionFromPart() {
		if(null != unit && unit.getEntity() instanceof Aero) {
			((Aero)unit.getEntity()).setAvionicsHits(hits);
		}
		
	}

	@Override
	public void fix() {
		hits = 0;
		if(null != unit && unit.getEntity() instanceof Aero) {
			((Aero)unit.getEntity()).setAvionicsHits(0);
		}
	}

	@Override
	public void remove(boolean salvage) {
		if(null != unit && unit.getEntity() instanceof Aero) {
			((Aero)unit.getEntity()).setAvionicsHits(3);
			if(!salvage) {
				unit.campaign.removePart(this);
			}
			unit.removePart(this);
			Part missing = getMissingPart();
			unit.campaign.addPart(missing);
			unit.addPart(missing);
		}
		setUnit(null);
	}

	@Override
	public Part getMissingPart() {
		return new MissingAvionics(getUnitTonnage());
	}

	@Override
	public String checkFixable() {
		return null;
	}

	@Override
	public boolean needsFixing() {
		return hits > 0;
	}

	@Override
	public long getCurrentValue() {
		//TODO: table in TechManual makes no sense - where are control systems for ASFs?
		return 0;
	}

	@Override
	public double getTonnage() {
		return 0;
	}

	@Override
	public int getTechRating() {
		//go with conventional fighter avionics
		return EquipmentType.RATING_B;
	}

	@Override
	public int getAvailability(int era) {
		//go with conventional fighter avionics
		if(era == EquipmentType.ERA_SL) {
			return EquipmentType.RATING_C;
		} else if(era == EquipmentType.ERA_SW) {
			return EquipmentType.RATING_D;
		} else {
			return EquipmentType.RATING_C;
		}
	}
	
	@Override
	public int getTech() {
		return TechConstants.T_IS_TW_ALL;
	}
	
	@Override 
	public int getTechBase() {
		return T_BOTH;	
	}

	@Override
	public boolean isSamePartTypeAndStatus(Part part) {
		if(needsFixing() || part.needsFixing()) {
    		return false;
    	}
		return part instanceof Avionics;
	}

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		//nothing to write
	}

	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		//nothing to load
	}
	
}