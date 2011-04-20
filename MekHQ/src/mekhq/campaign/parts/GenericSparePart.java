/*
 * MekSensor.java
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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.EquipmentType;
import megamek.common.TechConstants;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.work.ReplacementItem;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class GenericSparePart extends Part {
	private static final long serialVersionUID = 1835336863342932900L;

	public static int UNITARY_COST = 1;

    protected int tech;
    protected int amount;

    public GenericSparePart() {
    	this(0, 0);
    }
    
    public GenericSparePart(int tech, int amount) {
        super(false, 0);
        this.tech = tech;
        this.amount = amount;
        reCalc();
    }
    
    @Override
    public void reCalc() {
        this.name = "Spare Part " + TechConstants.getLevelName(tech);
        this.cost = GenericSparePart.UNITARY_COST * getAmount();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        reCalc();
    }

    @Override
    public int getTech () {
        return this.tech;
    }
  
    @Override
    public boolean canBeUsedBy(ReplacementItem task) {
        return (task instanceof ReplacementItem
                && ((ReplacementItem) task).partNeeded().getTech() == getTech());
    }

    @Override
    public boolean isSamePartTypeAndStatus (Part part) {
        return part instanceof GenericSparePart
                && getName().equals(part.getName())
                && getStatus().equals(part.getStatus())
                && ((GenericSparePart) part).getTech() == getTech();
    }

    @Override
    public int getPartType() {
        return PART_TYPE_GENERIC_SPARE_PART;
    }

    @Override
    public String getSaveString () {
        return getName() + ";" + getTonnage() + ";" + getTech() + ";" + getAmount();
    }

    @Override
    public String getDesc() {
        return super.getDesc();
    }

    @Override
    public String getCostString () {
        NumberFormat numberFormat = DecimalFormat.getIntegerInstance();
        String text = numberFormat.format(getAmount()) + " " + (getAmount()!=0?"Parts":"Part");
        return text;
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<amount>"
				+amount
				+"</amount>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<tech>"
				+tech
				+"</tech>");
		writeToXmlEnd(pw1, indent, id);
	}

	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		NodeList nl = wn.getChildNodes();
		
		for (int x=0; x<nl.getLength(); x++) {
			Node wn2 = nl.item(x);
			
			if (wn2.getNodeName().equalsIgnoreCase("amount")) {
				amount = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("tech")) {
				tech = Integer.parseInt(wn2.getTextContent());
			} 
		}
	}

	@Override
	public int getAvailability(int era) {
		return EquipmentType.RATING_C;
	}

	@Override
	public int getTechRating() {
		// TODO Auto-generated method stub
		return EquipmentType.RATING_C;
	}
}
