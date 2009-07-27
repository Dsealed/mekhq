/*
 * TechTeam.java
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

import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.Compute;
import megamek.common.Entity;
import megamek.common.Mech;
import megamek.common.Tank;
import mekhq.campaign.Campaign;
import mekhq.campaign.Utilities;
import mekhq.campaign.work.UnitWorkItem;
import mekhq.campaign.work.WorkItem;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class TechTeam extends SupportTeam {

    public static final int T_MECH = 0;
    public static final int T_MECHANIC = 1;
    public static final int T_AERO = 2;
    public static final int T_BA = 3;
    public static final int T_NUM = 4;
    
    private int type;
    
    public static String getTypeDesc(int type) {
        switch(type) {
            case T_MECH:
                return "Mech Tech";
            case T_MECHANIC:
                return "Mechanic";
            case T_AERO:
                return "Aero Tech";
            case T_BA:
                return "BA Tech";
        }
        return "?? Tech";
    }
    
    public TechTeam(Campaign c, String name, int rating, int type) {
        super(c, name, rating);
        this.type = type;
        this.fullSize = 7;
        this.currentSize = 7;
    }
    
    
    @Override
    public String getTypeDesc() {
        return getTypeDesc(type);
    }
    
    @Override
    public int getSkillBase() {
        int base = 11;
         switch(rating) {
           case SupportTeam.EXP_GREEN:
               base = 9;
               break;
           case SupportTeam.EXP_REGULAR:
               base = 7;
               break;
           case SupportTeam.EXP_VETERAN:
               base = 6;
               break;
           case SupportTeam.EXP_ELITE:
               base = 5;
               break;
       }
       return base;
    }
    
   @Override
   public String getTasksDesc() {
       int total = 0;
       int minutes = 0;
       for(WorkItem task : getTasksAssigned()) {
           total++;
           minutes += task.getTime();
       }
       return "" + total + " task(s), " + minutes + "/" + getHours()*60 + " minutes";
   }
   
   @Override
   public boolean canDo(WorkItem task) {
       return task.isNeeded() && task.getSkillMin() <= getRating();
   } 
    
   @Override
   public int makeRoll(WorkItem task) {
       if(task instanceof UnitWorkItem && isRightType(((UnitWorkItem)task).getUnit().getEntity())) {
           return Compute.d6(2);
       } else {
           return Utilities.roll3d6();
       }
   }
   
   public boolean isRightType(Entity en) {
       if((type == T_MECH && !(en instanceof Mech)) 
               || (type == T_MECHANIC && !(en instanceof Tank))
               || (type == T_AERO && !(en instanceof Aero))
               || (type == T_BA && !(en instanceof BattleArmor))) {
           return false;
       }               
       return true;
   }
   
   @Override
   public String getDescHTML() {
        String toReturn = "<html><b>" + getName() + "</b><br>";
        toReturn += getRatingName() + " " + getTypeDesc() + "<br>";
        toReturn += getMinutesLeft() + " minutes left";
        toReturn += "</html>";
        return toReturn;
   }
}
