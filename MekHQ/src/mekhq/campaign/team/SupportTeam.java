/*
 * SupportTeam.java
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

package mekhq.campaign.team;

import mekhq.MekHQApp;
import mekhq.campaign.*;

import java.io.PrintWriter;
import java.io.Serializable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import megamek.common.Compute;
import megamek.common.EquipmentType;
import megamek.common.TargetRoll;
import mekhq.campaign.parts.Availability;
import mekhq.campaign.parts.GenericSparePart;
import mekhq.campaign.parts.Part;
import mekhq.campaign.work.FullRepairWarchest;
import mekhq.campaign.work.IPartWork;
import mekhq.campaign.work.Refit;
import mekhq.campaign.work.ReloadItem;
import mekhq.campaign.work.ReplacementItem;
import mekhq.campaign.work.UnitWorkItem;
import mekhq.campaign.work.WorkItem;

/**
 *
 * @author Taharqa
 * This is the code for a team (medical, technical, etc.)
 */
public abstract class SupportTeam implements Serializable, MekHqXmlSerializable {
	private static final long serialVersionUID = 2842840638600274021L;
	public static final int EXP_GREEN = 0;
    public static final int EXP_REGULAR = 1;
    public static final int EXP_VETERAN = 2;
    public static final int EXP_ELITE = 3;
    public static final int EXP_NUM = 4;
    
    protected String name;
    protected int rating; 
    protected int id;
    protected int fullSize;
    protected int currentSize;
    protected int hours;
    protected int minutesLeft;
    protected int overtimeLeft;
    
//    protected ArrayList<WorkItem> assignedTasks;
    
    protected Campaign campaign;
    
    //private Vector<WorkItem> tasksAssigned;
    
    public static String getRatingName(int rating) {
        switch(rating) {
           case SupportTeam.EXP_GREEN:
               return "Green";
           case SupportTeam.EXP_REGULAR:
               return "Regular";
           case SupportTeam.EXP_VETERAN:
               return "Veteran";
           case SupportTeam.EXP_ELITE:
               return "Elite";
            case SupportTeam.EXP_NUM:
                return "Impossible";
       }
       return "Unknown";
    }
    
    public SupportTeam(String name, int rating) {
        this.name = name;
        this.rating = rating;
        this.hours = 8;
    //    this.assignedTasks = new ArrayList<WorkItem>();
        resetMinutesLeft();
    }
    
    public abstract void reCalc();
    
    public void setCampaign(Campaign c) {
        this.campaign = c;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int i) {
        this.rating = i;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String s) {
    	this.name = s;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int i) {
        this.id = i;
    } 
    
    public int getFullStrength() {
        return fullSize;
    }
    
    public int getCurrentStrength() {
        return currentSize;
    }
    
    public void setCurrentStrength(int i) {
        this.currentSize = i;
    }
    
    public int getHours() {
        return hours;
    }
    
    public void setHours(int i) {
        this.hours = i;
    }
    
    public int getMinutesLeft() {
        return minutesLeft;
    }
    
    public void setMinutesLeft(int m) {
        this.minutesLeft = m;
    }
    
    public int getOvertimeLeft() {
        return overtimeLeft;
    }
    
    public void setOvertimeLeft(int m) {
        this.overtimeLeft = m;
    }
    
    public void resetMinutesLeft() {
        this.minutesLeft = 60 * getHours();
        this.overtimeLeft = 60 * 4;
    }
    
    public int getCasualties() {
        return getFullStrength() - getCurrentStrength();
    }
    
    public TargetRoll getTarget(int mode) {    
        int effRating = getRating();
        switch(mode) {
            case WorkItem.MODE_RUSH_THREE:
                effRating--;
            case WorkItem.MODE_RUSH_TWO:
                effRating--;
            case WorkItem.MODE_RUSH_ONE:
                effRating--;
                break;
        }
        if(effRating < EXP_GREEN) {
            return new TargetRoll(TargetRoll.IMPOSSIBLE, "the current team cannot perform this level of rush job");
        }
        TargetRoll target = new TargetRoll(getSkillBase(effRating), getRatingName(effRating));
        if(getCasualtyMods() > 0) {
            target.addModifier(getCasualtyMods(), "understaffed");
        }
        /*
         * TODO: need an option for era mods
        if(this instanceof TechTeam) {
            target.addModifier(campaign.getEraMod(), "era mod");
        }
         * */
        return target;
    }
    
   public abstract int getSkillBase(int effectiveRating);   
   
   public int getCasualtyMods() {
       int casualties = getCasualties();
       if(casualties > 0 && casualties < 3) {
           return 1;
       } 
       else if(casualties > 2 && casualties < 5) {
           return 2;
       }
       else if(casualties == 5) {
           return 3;
       }
       else if(casualties == 6) {
           return 4;
       }
       return 0;
   }
   
   public String getRatingName() {
       return getRatingName(rating);
   }
   
   public String getDesc() {
       return getName() + " (" + getRatingName() + " " + getTypeDesc() + ") ";
   }
   
   public abstract String getDescHTML();
   
  // public abstract String getTasksDesc();
   
   public abstract String getTypeDesc();
   
   public abstract boolean canDo(WorkItem task);
   
   public abstract int makeRoll(WorkItem task);
   
   public boolean isTaskOvertime(IPartWork partWork) {
       return partWork.getTimeLeft() > getMinutesLeft()
                && (campaign.isOvertimeAllowed()  
                    && (partWork.getTimeLeft() - getMinutesLeft()) <= getOvertimeLeft());
   }
   
   public boolean isNotEnoughTime(WorkItem task) {
       if(!campaign.isOvertimeAllowed()) {
           return (task.getTimeLeft() > getMinutesLeft());
       }
       else {
           return (task.getTimeLeft() - getMinutesLeft()) > getOvertimeLeft();
       }
   }
   
   public TargetRoll getTargetForAcquisition(WorkItem task) {
	   if(null == task) {
           return new TargetRoll(TargetRoll.IMPOSSIBLE, "no task?");
       }
	   if(!(task instanceof ReplacementItem)) {
           return new TargetRoll(TargetRoll.IMPOSSIBLE, "This is not a replacement task");
	   }
	   ReplacementItem replacement = (ReplacementItem)task;
	   if(replacement.hasPart()) {
           return new TargetRoll(TargetRoll.IMPOSSIBLE, "A part already exists for this replacement");
	   }
	   if(replacement.hasCheckedForPart()) {
           return new TargetRoll(TargetRoll.IMPOSSIBLE, "Already checked for this part in this cycle");
	   }
	   
	   TargetRoll target = getTarget(WorkItem.MODE_NORMAL);
	   target.append(replacement.getAllAcquisitionMods());
	   return target;
   }
   
   public String acquirePartFor(WorkItem task) {
       String report = "";
	   if(task instanceof ReplacementItem && !((ReplacementItem)task).hasPart()) {
           //first we need to source the part
           ReplacementItem replace = (ReplacementItem)task;
           Part part = replace.partNeeded();
           report += getName() + " attempts to find " + part.getDesc();          
           TargetRoll target = getTargetForAcquisition(replace);     
           replace.setPartCheck(true);
           int roll = Compute.d6(2);
           report += "  needs " + target.getValueAsString();
           report += " and rolls " + roll + ":";

           if(roll >= target.getValue()) {
              report += " <font color='green'><b>part found.</b></font><br/>";
              replace.setPart(part);
              campaign.buyPart(part);
           } else {
              report += " <font color='red'><b>part not available.</b></font>";
           }
       }
	   return report;
   }
   
   public String doAssigned(WorkItem task) {
       // Called by btnDoTaskActionPerformed
       // if team.getTargetFor(task).getValue() == TargetRoll.IMPOSSIBLE, doAssigned is not called
       // Not called if not enough funds as long as getTargetFor checks for funds
       String report = "";
          
       /*
       else if (task instanceof ReplacementItem
                    && ((ReplacementItem)task).hasPart()
                    && ((ReplacementItem) task).partNeeded() instanceof GenericSparePart
                    && !((ReplacementItem) task).hasEnoughGenericSpareParts()){
           GenericSparePart partNeeded = (GenericSparePart) ((ReplacementItem) task).partNeeded();
           GenericSparePart currentPart = (GenericSparePart) ((ReplacementItem) task).getPart();
           
           //first we need to source the amount missing
           ReplacementItem replace = (ReplacementItem)task;
           GenericSparePart partMissing = (GenericSparePart) replace.partNeeded();
           partMissing.setAmount(partNeeded.getAmount()-currentPart.getAmount());
           
           report += getName() + " must first obtain " + partMissing.getDesc();
           TargetRoll target = getTarget(WorkItem.MODE_NORMAL);
           replace.setPartCheck(true);

           char availability = GenericSparePart.getAvailability();
           int factionMod = 0;

           int availabilityMod = SSWLibHelper.getModifierFromAvailability(availability);
           target.addModifier(availabilityMod, "availability (" + availability + ")");
           target.addModifier(factionMod, "faction");

           int roll = Compute.d6(2);
           report += "  needs " + target.getValueAsString();
           report += "<font color='blue' size='-2'> [" + target.getDesc() + "] </font>";
           report += " and rolls " + roll + ":";

           if(roll >= target.getValue()) {
              report += " <font color='green'><b>part found.</b></font><br/>";
              currentPart.setAmount(partNeeded.getAmount());
              campaign.buyPart(partMissing);
           } else {
              report += " <font color='red'><b>part not available.</b></font>";
              return report;
           }
       }
       */
       /*
       report += getName() + " attempts to " + task.getDisplayName();    
       TargetRoll target = getTargetFor(task);
       
       //check and use time
       int minutes = task.getTimeLeft();
       if(minutes > getMinutesLeft()) {
           minutes -= getMinutesLeft();
           //check for overtime first
           if(campaign.isOvertimeAllowed() && minutes <= getOvertimeLeft()) {
               //we ar working overtime
               setMinutesLeft(0);
               setOvertimeLeft(getOvertimeLeft() - minutes);
           } else {
               //we need to finish the task tomorrow
               int minutesUsed = getMinutesLeft();
               if(campaign.isOvertimeAllowed()) {
                   minutesUsed += getOvertimeLeft();
               }
               task.addTimeSpent(minutesUsed);
               setMinutesLeft(0);
               setOvertimeLeft(0);
               task.setTeam(this);
               report += " - <b>Not enough time, the remainder of the task will be finished tomorrow.</b>";
               return report;
           }     
       } else {
           setMinutesLeft(getMinutesLeft() - minutes);
       }
       
       //make the roll
       int roll = makeRoll(task);
       report = report + "  needs " + target.getValueAsString() + " and rolls " + roll + ":";
       if(roll >= target.getValue()) {
           report = report + task.succeed();

           // Substract cost
           if (task instanceof FullRepairWarchest) {
               campaign.addFunds(-((FullRepairWarchest) task).getCost());
           } else if (task instanceof ReloadItem) {
               campaign.addFunds(-((ReloadItem) task).getCost());
           }
       } else {
           report = report + task.fail(getRating());
       }
       */
       return report;
   }
 

	public abstract void writeToXml(PrintWriter pw1, int indent, int id);
	
	protected void writeToXmlBegin(PrintWriter pw1, int indent, int id) {
		pw1.println(MekHqXmlUtil.indentStr(indent) + "<supportTeam id=\""
				+id
				+"\" type=\""
				+this.getClass().getName()
				+"\">");

		// There is a campaign object on here...
		// But instead of even trying to write it out, we'll deal with it in post-process on load.
		// Recursive saves to what's effectively a parent object in the architecture isn't worth the trouble.  :)
		
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<currentSize>"
				+currentSize
				+"</currentSize>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<fullSize>"
				+fullSize
				+"</fullSize>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<hours>"
				+hours
				+"</hours>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<id>"
				+this.id
				+"</id>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<minutesLeft>"
				+minutesLeft
				+"</minutesLeft>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<name>"
				+name
				+"</name>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<overtimeLeft>"
				+overtimeLeft
				+"</overtimeLeft>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<rating>"
				+rating
				+"</rating>");
	}
	
	protected void writeToXmlEnd(PrintWriter pw1, int indent, int id) {
		pw1.println(MekHqXmlUtil.indentStr(indent) + "</supportTeam>");
	}

	public static SupportTeam generateInstanceFromXML(Node wn) {
		SupportTeam retVal = null;
		NamedNodeMap attrs = wn.getAttributes();
		Node classNameNode = attrs.getNamedItem("type");
		String className = classNameNode.getTextContent();
		
		try {
			// Instantiate the correct child class, and call its parsing function.
			retVal = (SupportTeam) Class.forName(className).newInstance();
			retVal.loadFieldsFromXmlNode(wn);
			
			// Okay, now load Part-specific fields!
			NodeList nl = wn.getChildNodes();
			
			for (int x=0; x<nl.getLength(); x++) {
				Node wn2 = nl.item(x);
				
				if (wn2.getNodeName().equalsIgnoreCase("currentSize")) {
					retVal.currentSize = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("fullSize")) {
					retVal.fullSize = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("hours")) {
					retVal.hours = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("id")) {
					retVal.id = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("minutesLeft")) {
					retVal.minutesLeft = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("name")) {
					retVal.name = wn2.getTextContent();
				} else if (wn2.getNodeName().equalsIgnoreCase("overtimeLeft")) {
					retVal.overtimeLeft = Integer.parseInt(wn2.getTextContent());
				} else if (wn2.getNodeName().equalsIgnoreCase("rating")) {
					retVal.rating = Integer.parseInt(wn2.getTextContent());
				}
			}
		} catch (Exception ex) {
			// Errrr, apparently either the class name was invalid...
			// Or the listed name doesn't exist.
			// Doh!
			MekHQApp.logError(ex);
		}
		
		return retVal;
	}
	
	protected abstract void loadFieldsFromXmlNode(Node wn);
}
