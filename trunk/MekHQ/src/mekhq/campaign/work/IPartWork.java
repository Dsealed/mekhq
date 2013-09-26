/*
 * IPartWork.java
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

import java.util.UUID;

import megamek.common.TargetRoll;
import mekhq.campaign.parts.MissingPart;
import mekhq.campaign.unit.Unit;

/**
 * 
 * @author Jay
 */
public interface IPartWork extends IWork {
	
	public String getPartName();
	
	public int getSkillMin();

	public int getBaseTime();
	public int getActualTime();
	public int getTimeSpent();
	public int getTimeLeft();
	public void addTimeSpent(int time);
	public void resetTimeSpent();
	public void resetOvertime();
	public void resetRepairStatus();
	public boolean isRightTechType(String skillType);
	
	public TargetRoll getAllModsForMaintenance();
	
	public UUID getAssignedTeamId();
	public void setTeamId(UUID id);
	public boolean hasWorkedOvertime();
	public void setWorkedOvertime(boolean b);
	public int getShorthandedMod();
	public void setShorthandedMod(int i);
	
	public void updateConditionFromEntity();
	public void updateConditionFromPart();
	public void fix();
	public void remove(boolean salvage);
	public MissingPart getMissingPart();
	
	public String getDesc();
	public String getDetails();
	
	public Unit getUnit();
	
	public boolean isSalvaging();
	
	public String checkFixable();
	
}