/*
 * WorkItem.java
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

import java.io.Serializable;
import megamek.common.Entity;
import megamek.common.TargetRoll;
import mekhq.campaign.Campaign;
import mekhq.campaign.team.SupportTeam;
import mekhq.campaign.Unit;

/**
 * Abstract extension of WorkItem for all work on units
 * @author Taharqa
 */
public abstract class UnitWorkItem extends WorkItem {

    //the unit for whom the work is being performed
    protected Unit unit;
    
    public UnitWorkItem(Unit unit) {
        super();
        this.unit = unit;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public int getUnitId() {
        return unit.getId();
    }
    
    @Override
    public String getDisplayName() {
        return unit.getEntity().getDisplayName() + ": " + getName();
    }
}
