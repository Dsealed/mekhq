/*
 * MekActuatorSalvage.java
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

package mekhq.campaign.work;

import java.io.PrintWriter;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.CriticalSlot;
import megamek.common.Mech;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.Unit;
import mekhq.campaign.parts.MekActuator;
import mekhq.campaign.parts.Part;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekActuatorSalvage extends SalvageItem {
	private static final long serialVersionUID = 3249213765491910138L;
	protected int loc;
    protected int type;

	public MekActuatorSalvage() {
		this(null, 0, 0);
	}
   
    public MekActuatorSalvage(Unit unit, int i, int t) {
        super(unit);
        this.loc = i;
        this.type = t;
        this.time = 90;
        this.difficulty = -3;
        reCalc();
    }
    
    @Override
    public void reCalc() {
    	if (unit == null)
        	return;
        
        this.name = "Salvage " + ((Mech)unit.getEntity()).getSystemName(type) + " Actuator";
        super.reCalc();
    }

    @Override
    public String getDetails() {
        return unit.getEntity().getLocationName(loc) + ", " + super.getDetails();
    }
    
    @Override
    public ReplacementItem getReplacement() {
        return new MekActuatorReplacement(unit, loc, type);
    }
    
    public int getLoc() {
        return loc;
    }
    
    public int getType() {
        return type;
    }

    @Override
    public Part getPart() {
        return new MekActuator(true, (int) unit.getEntity().getWeight(), type);
    }

    @Override
    public boolean sameAs(WorkItem task) {
        return (task instanceof MekActuatorSalvage
                && ((MekActuatorSalvage)task).getUnitId() == this.getUnitId()
                && ((MekActuatorSalvage)task).getLoc() == this.getLoc()
                && ((MekActuatorSalvage)task).getType() == this.getType());
    }

    @Override
    public void removePart() {
        unit.destroySystem(CriticalSlot.TYPE_SYSTEM, type, loc);
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<loc>"
				+loc
				+"</loc>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<type>"
				+type
				+"</type>");
		writeToXmlEnd(pw1, indent, id);
	}
	
	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		NodeList nl = wn.getChildNodes();
		
		for (int x=0; x<nl.getLength(); x++) {
			Node wn2 = nl.item(x);
			
			if (wn2.getNodeName().equalsIgnoreCase("loc")) {
				loc = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("type")) {
				type = Integer.parseInt(wn2.getTextContent());
			}
		}
		
		super.loadFieldsFromXmlNode(wn);
	}
}
