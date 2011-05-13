/*
 * MissingMekLocation.java
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

import megamek.common.EquipmentType;
import megamek.common.Mech;
import mekhq.campaign.MekHqXmlUtil;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MissingMekLocation extends MissingPart {
	private static final long serialVersionUID = -122291037522319765L;
	protected int loc;
    protected int structureType;
    protected boolean tsm;
    double percent;

    public MissingMekLocation() {
    	this(false, 0, 0, 0, false);
    }
    
    public int getLoc() {
        return loc;
    }

    public boolean isTsm() {
        return tsm;
    }

    public int getStructureType() {
        return structureType;
    }
    
    public MissingMekLocation(boolean salvage, int loc, int tonnage, int structureType, boolean hasTSM) {
        super(salvage, tonnage);
        this.loc = loc;
        this.structureType = structureType;
        this.tsm = hasTSM;
        this.percent = 1.0;
        //TODO: need to account for internal structure and myomer types
        //crap, no static report for location names?
        this.name = "Mech Location";
        switch(loc) {
            case(Mech.LOC_HEAD):
                this.name = "Mech Head";
                break;
            case(Mech.LOC_CT):
                this.name = "Mech Center Torso";
                break;
            case(Mech.LOC_LT):
                this.name = "Mech Left Torso";
                break;
            case(Mech.LOC_RT):
                this.name = "Mech Right Torso";
                break;
            case(Mech.LOC_LARM):
                this.name = "Mech Left Arm";
                break;
            case(Mech.LOC_RARM):
                this.name = "Mech Right Arm";
                break;
            case(Mech.LOC_LLEG):
                this.name = "Mech Left Leg";
                break;
            case(Mech.LOC_RLEG):
                this.name = "Mech Right Leg";
                break;
        }
        if(structureType != EquipmentType.T_STRUCTURE_STANDARD) {
            this.name += " (" + EquipmentType.getStructureTypeName(structureType) + ")";
        }
        if(tsm) {
            this.name += " (TSM)";
        }
        this.time = 240;
        this.difficulty = 3;
    }
    
    public double getTonnage() {
    	//TODO: how much should this weigh?
    	return 0;
    }
    
    @Override
    public long getPurchasePrice() {
        double totalStructureCost = EquipmentType.getStructureCost(getStructureType()) * getUnitTonnage();
        int muscCost = isTsm() ? 16000 : 2000;
        double totalMuscleCost = muscCost * getUnitTonnage();
        double cost = 0.1 * (totalStructureCost + totalMuscleCost);

        if (loc == Mech.LOC_HEAD) {
            // Add cockpit cost
            // TODO create a class for cockpit or memorize cockpit type
            cost += 200000;
        }

        return (long) Math.round(cost);
    }

    @Override
    public boolean isSamePartTypeAndStatus (Part part) {
        return part instanceof MekLocation
                && getName().equals(part.getName())
                && getStatus().equals(part.getStatus())
                && getLoc() == ((MekLocation)part).getLoc()
                && getUnitTonnage() == ((MekLocation)part).getUnitTonnage()
                && isTsm() == ((MekLocation)part).isTsm()
                && getStructureType() == ((MekLocation) part).getStructureType();
    }

    @Override
    public int getPartType() {
        return PART_TYPE_MEK_BODY_PART;
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<loc>"
				+loc
				+"</loc>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<structureType>"
				+structureType
				+"</structureType>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<tsm>"
				+tsm
				+"</tsm>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<percent>"
				+percent
				+"</percent>");
		writeToXmlEnd(pw1, indent, id);
	}

	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		NodeList nl = wn.getChildNodes();
		
		for (int x=0; x<nl.getLength(); x++) {
			Node wn2 = nl.item(x);
			
			if (wn2.getNodeName().equalsIgnoreCase("loc")) {
				loc = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("structureType")) {
				structureType = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("percent")) {
				percent = Double.parseDouble(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("tsm")) {
				if (wn2.getTextContent().equalsIgnoreCase("true"))
					tsm = true;
				else
					tsm = false;
			} 
		}
	}

	@Override
	public int getAvailability(int era) {
		switch(structureType) {
		case EquipmentType.T_STRUCTURE_ENDO_STEEL:
		case EquipmentType.T_STRUCTURE_ENDO_PROTOTYPE:
			if(era == EquipmentType.ERA_SL) {
				return EquipmentType.RATING_D;
			} else if(era == EquipmentType.ERA_SW) {
				return EquipmentType.RATING_F;
			} else {
				return EquipmentType.RATING_E;
			}
		case EquipmentType.T_STRUCTURE_ENDO_COMPOSITE:
			if(era == EquipmentType.ERA_SL) {
				return EquipmentType.RATING_X;
			} else if(era == EquipmentType.ERA_SW) {
				return EquipmentType.RATING_X;
			} else {
				return EquipmentType.RATING_F;
			}
		case EquipmentType.T_STRUCTURE_REINFORCED:
		case EquipmentType.T_STRUCTURE_COMPOSITE:
			if(era == EquipmentType.ERA_SL) {
				return EquipmentType.RATING_X;
			} else if(era == EquipmentType.ERA_SW) {
				return EquipmentType.RATING_X;
			} else {
				return EquipmentType.RATING_E;
			}
		case EquipmentType.T_STRUCTURE_INDUSTRIAL:
		default:
			return EquipmentType.RATING_C;	
		}
	}

	@Override
	public int getTechRating() {
		switch(structureType) {
		case EquipmentType.T_STRUCTURE_ENDO_STEEL:
		case EquipmentType.T_STRUCTURE_ENDO_PROTOTYPE:
			return EquipmentType.RATING_E;
		case EquipmentType.T_STRUCTURE_ENDO_COMPOSITE:
		case EquipmentType.T_STRUCTURE_REINFORCED:
		case EquipmentType.T_STRUCTURE_COMPOSITE:
			return EquipmentType.RATING_E;
		case EquipmentType.T_STRUCTURE_INDUSTRIAL:
			return EquipmentType.RATING_C;
		default:
			return EquipmentType.RATING_D;
		}
		
	}

	@Override
	public boolean isAcceptableReplacement(Part part) {
		if(loc == Mech.LOC_CT) {
			//you can't replace a center torso
			return false;
		}
		if(part instanceof MekLocation) {
			MekLocation mekLoc = (MekLocation)part;
			return mekLoc.getLoc() == loc
			&& mekLoc.getUnitTonnage() == getUnitTonnage()
	        && mekLoc.isTsm() == tsm
	        && mekLoc.getStructureType() == structureType;
		}
		return false;
	}
	
	@Override
	public String checkFixable() {
		if (unit.getEntity() instanceof Mech) {
			// cant replace appendages when corresponding torso is gone
			if (loc == Mech.LOC_LARM
					&& unit.getEntity().isLocationBad(Mech.LOC_LT)) {
				return "must replace left torso first";
			} else if (loc == Mech.LOC_RARM
					&& unit.getEntity().isLocationBad(Mech.LOC_RT)) {
				return "must replace right torso first";
			}
		}
		return null;
	}

	@Override
	public Part getNewPart() {
		return new MekLocation(isSalvage(), loc, getUnitTonnage(), structureType, tsm);
	}
}
