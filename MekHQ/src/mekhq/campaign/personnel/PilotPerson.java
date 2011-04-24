/*
 * PilotPerson.java
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

package mekhq.campaign.personnel;

import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.Pilot;
import megamek.common.Protomech;
import megamek.common.Tank;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import mekhq.campaign.Campaign;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.Unit;
import mekhq.campaign.work.HealPilot;

/**
 * A Person wrapper for pilots and vee crews
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class PilotPerson extends Person {
	private static final long serialVersionUID = -4758195062070601267L;
	public static final int T_MECH = 0;
    public static final int T_VEE = 1;
    public static final int T_AERO = 2;
    public static final int T_PROTO = 3;
    public static final int T_BA = 4;
    public static final int T_NUM = 5;
    
    private Pilot pilot;
    private int type;
    private Unit unit;
    private int unitId;
    
    public PilotPerson() {
    	this(null, 0);
    }
    
    public PilotPerson(Pilot p, int t) {
        super();
        this.pilot = p;
        this.type = t;
        reCalc();
    }
    
    public PilotPerson(Pilot p, int t, Unit u) {
        this(p,t);
        this.unit = u;
    }

    @Override
    public void reCalc() {
        if (pilot == null)
        	return;
        
        this.portraitCategory = pilot.getPortraitCategory();
        this.portraitFile = pilot.getPortraitFileName();
    }
    
    public int getType() {
        return type;
    }
    
    public static String getTypeDesc(int type) {
        switch(type) {
            case(T_MECH):
                return "Mechwarrior";
            case(T_VEE):
                return "Vehicle crew";
            case(T_AERO):
                return "Aero Pilot";
            case(T_PROTO):
                return "Proto Pilot";
            case(T_BA):
                return "Battle armor Pilot";
            default:
                return "??";
        }
    }
    
    public String getTypeDesc() {
        return getTypeDesc(type);
    }
    
    @Override
    public String getSkillSummary() {
    	String skillString = " (" + pilot.getGunnery() + "/" + pilot.getPiloting() + ")";
    	double average = (pilot.getGunnery() + pilot.getPiloting())/2.0;
    	String level = "Green";
    	if(average<=2.5) {
    		level = "Elite";
    	} else if (average <=3.5) {
    		level = "Veteran";
    	} else if (average<=4.5) {
    		level = "Regular";
    	}
    	return level + skillString;
    }
    
    public static int getType(Entity en) {
        if(en instanceof Mech) {
            return T_MECH;
        }
        else if(en instanceof Protomech) {
            return T_PROTO;
        } 
        else if(en instanceof Aero) {
            return T_AERO;
        }
        else if(en instanceof Tank) {
            return T_VEE;
        }
        else if(en instanceof BattleArmor) {
            return T_BA;
        }
        return -1;
    }
    
    public boolean canPilot(Entity en) {
        if(en instanceof Mech && type == T_MECH) {
            return true;
        }
        else if(en instanceof Protomech && type == T_PROTO) {
            return true;
        } 
        else if(en instanceof Aero && type == T_AERO) {
            return true;
        }
        else if(en instanceof Tank && type == T_VEE) {
            return true;
        }
        else if(en instanceof BattleArmor && type == T_BA) {
            return true;
        }
        return false;
    }
    
    public Pilot getPilot() {
        return pilot;
    }
    
    public void setPilot(Pilot p) {
        this.pilot = p;
    }

    @Override
    public String getDesc() {
        String care = "";
        String status = "";
        if(pilot.getHits() > 0) {
            status = " (" + pilot.getStatusDesc() + ")";
        }
        return care + pilot.getName() + " [" + pilot.getGunnery() + "/" + pilot.getPiloting() + " " + getTypeDesc() + "]" + status;
    }
    
    @Override
    public String getDescHTML() {
        String toReturn = "<html><font size='2'><b>" + pilot.getName() + "</b><br/>";
        toReturn += getTypeDesc() + " (" + pilot.getGunnery() + "/" + pilot.getPiloting() + ")<br/>";
        toReturn += pilot.getStatusDesc() + getAssignedDoctorString();
        if(isDeployed()) {
            toReturn += " (DEPLOYED)";
        }
        toReturn += "</font></html>";
        return toReturn;
    }
    
    public boolean isAssigned() {
        return null != unit;
    }
    
    public Unit getAssignedUnit() {
        return unit;
    }
    
    public void setAssignedUnit(Unit u) {
        this.unit = u;
    }
    
    @Override
    public void runDiagnostic(Campaign campaign) {
        if(pilot.getHits() > 0) {
            setTask(new HealPilot(this));
            campaign.addWork(getTask());
            
        }
    }
    
    /**
     * heal one hit on the pilot/crew
     */
    @Override
    public void heal() {
        if(needsHealing()) {
            getPilot().setHits(getPilot().getHits() - 1);
        }
        if(!needsHealing() && null != task) {
            task.complete();
        }
    }

    @Override
    public boolean needsHealing() {
        return (getPilot().getHits() > 0);
    }

    @Override
    public String getDossier() {
        File file = new File(".");
        String path = file.getAbsolutePath();
        
        path = path.replace(".", "");
        String category = getPortraitCategory();
        String image = getPortraitFileName();
        if(category.equals(Pilot.ROOT_PORTRAIT)) {
            category = "";
        }
        String toReturn = "<html><b>" + pilot.getName() + "</b><br/>";
        toReturn += "<i>" + pilot.getNickname() + "</i><br/><br/>";
        if(!image.equals(Pilot.PORTRAIT_NONE)) {
            toReturn += "<img src=\"file://" + path + "data/images/portraits/" + category + image + "\"/>";
        }
        toReturn += "<table>";
        toReturn += "<tr><td><b>Gunnery:</b></td><td>" + pilot.getGunnery() + "</td></tr>";
        toReturn += "<tr><td><b>Piloting:</b></td><td>" + pilot.getPiloting() + "</td></tr>";
        toReturn += "<tr><td><b>Iniative Bonus:</b></td><td>" + pilot.getInitBonus() + "</td></tr>";
        toReturn += "<tr><td><b>Commander Bonus:</b></td><td>" + pilot.getCommandBonus() + "</td></tr>";
        toReturn += "<tr><td><b>XP:</b></td><td>" + getXp() + "</td></tr>";
        toReturn += "</table>";
     
        for (Enumeration<IOptionGroup> advGroups = pilot.getOptions().getGroups(); advGroups.hasMoreElements();) {
            IOptionGroup advGroup = advGroups.nextElement();
            if(pilot.countOptions(advGroup.getKey()) > 0) {
                toReturn += "<p/><b><u>" + advGroup.getDisplayableName() + "</u></b><br/>";    
                for (Enumeration<IOption> advs = advGroup.getOptions(); advs.hasMoreElements();) {
                    IOption adv = advs.nextElement();
                    if(adv.booleanValue()) {
                        toReturn += "  " + adv.getDisplayableNameWithValue() + "<br/>";
                    }
                }
            }
        }
        
        if(null != getBiography() && getBiography().length() > 0) {
            toReturn += "<br/><br/><b>Biography</b><br/>";
            toReturn += getBiography();
        }
        toReturn += "</html>";
        
        
        return toReturn;
    }
    
    @Override
    public void setPortraitCategory(String s) {
        super.setPortraitCategory(s);
        pilot.setPortraitCategory(s);
        
    }

    @Override
    public void setPortraitFileName(String s) {
        super.setPortraitFileName(s);
        pilot.setPortraitFileName(s);
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<type>"
				+type
				+"</type>");
		
		// If a pilot doesn't have a unit, well...
		// This should be null.
		if (unit != null) {
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<unitId>"
					+unit.getId()
					+"</unitId>");
		}

		// Pilot is a megamek class with no XML serialization support.
		// But there's a constructor for building them...
		// Plus a bunch of "set" functions...
		//TODO: Are any other items on Pilot important for XML serialization?
		//TODO: Handle separate ballistic/missile/energy gunneries
		//TODO: Handle pilot special abilities
		if (pilot != null) {
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotName>"
					+pilot.getName()
					+"</pilotName>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotGunnery>"
					+pilot.getGunnery()
					+"</pilotGunnery>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotPiloting>"
					+pilot.getPiloting()
					+"</pilotPiloting>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotHits>"
					+pilot.getHits()
					+"</pilotHits>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotCommandBonus>"
					+pilot.getCommandBonus()
					+"</pilotCommandBonus>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotInitBonus>"
					+pilot.getInitBonus()
					+"</pilotInitBonus>");
			pw1.println(MekHqXmlUtil.indentStr(indent+1)
					+"<pilotNickname>"
					+pilot.getNickname()
					+"</pilotNickname>");
		}
		
		writeToXmlEnd(pw1, indent, id);
	}
	
	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		NodeList nl = wn.getChildNodes();
		String pilotName = null;
		int pilotGunnery = -1;
		int pilotPiloting = -1;
		int pilotHits = -1;
		int pilotCommandBonus = -1;
		int pilotInitBonus = -1;
		String pilotNickname = null;
		
		for (int x=0; x<nl.getLength(); x++) {
			Node wn2 = nl.item(x);
			
			if (wn2.getNodeName().equalsIgnoreCase("pilotNickname")) {
				pilotNickname = wn2.getTextContent();
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotName")) {
				pilotName = wn2.getTextContent();
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotGunnery")) {
				pilotGunnery = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotPiloting")) {
				pilotPiloting = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotHits")) {
				pilotHits = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotCommandBonus")) {
				pilotCommandBonus = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("pilotInitBonus")) {
				pilotInitBonus = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("type")) {
				type = Integer.parseInt(wn2.getTextContent());
			} else if (wn2.getNodeName().equalsIgnoreCase("unitId")) {
				unitId = Integer.parseInt(wn2.getTextContent());
			}
		}
		
		pilot = new Pilot(pilotName, pilotGunnery, pilotPiloting);
		
		if (pilotHits >= 0)
			pilot.setHits(pilotHits);
		
		if (pilotNickname != null)
			pilot.setNickname(pilotNickname);
		
		if (pilotInitBonus >= 0)
			pilot.setInitBonus(pilotInitBonus);

		if (pilotCommandBonus >= 0)
			pilot.setCommandBonus(pilotCommandBonus);
	}

	public int getUnitIt() {
		return unitId;
	}
	
	@Override
	public String getName() {
		if(null != pilot) {
			return pilot.getName();
		} else {
			return "Unknown";
		}
	}
	
	@Override
	public String getCallsign() {
		if(null != pilot) {
			String nick = pilot.getNickname();
			if(null == nick || nick.equals("")) {
				nick = "-";
			}
			return nick;
		} else {
			return "-";
		}
	}
	
	public void setUnit(Unit nt) {
		unit = nt;
	}
    
    @Override
    public int getMonthlySalary() {
    	int retVal = 0;

    	switch (type) {
		case T_MECH:
			retVal = 1500;
			break;
		case T_VEE:
			retVal = 900;
			break;
		case T_AERO:
			retVal = 1500;
			break;
		case T_PROTO:
			//TODO: Confirm ProtoMech pilots should be paid as BA pilots?
			retVal = 960;
			break;
		case T_BA:
			retVal = 960;
			break;
		case T_NUM:
			// Not a real pilot type. If someone has this, they don't get paid!
			break;
    	}

    	//TODO: Add conventional aircraft pilots.
    	//TODO: Add regular infantry.
    	//TODO: Add specialist/Anti-Mech infantry.
    	//TODO: Add vessel crewmen (DropShip).
    	//TODO: Add vessel crewmen (JumpShip).
    	//TODO: Add vessel crewmen (WarShip).
    	
    	//TODO: Properly pay vehicle crews for actual size.
    	//TODO: Properly pay large ship crews for actual size.

    	//TODO: Add quality mod to salary calc..
    	//TODO: Add era mod to salary calc..
    	
    	return retVal;
    }
}
