/*
 * EngineRepair.java
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

import megamek.common.CriticalSlot;
import megamek.common.Mech;
import mekhq.campaign.Unit;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekEngineRepair extends RepairItem {

    public MekEngineRepair(Unit unit, int h) {
        super(unit, h);
        this.name = "Repair engine";
        this.time = 100;
        this.difficulty = -1;
        if(hits == 2) {
            this.time = 200;
            this.difficulty = 0;
        } else if (hits > 2) {
            this.time = 300;
            this.difficulty = 1;
        }
    }
    
    @Override
    public WorkItem replace() {
        return new MekEngineReplacement(unit);
    }

    @Override
    public void fix() {
        for(int i = 0; i < unit.getEntity().locations(); i++) {
            unit.getEntity().removeCriticals(i, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_ENGINE));
        }
    }
    
    @Override
    public String checkFixable() {
        for(int i = 0; i < unit.getEntity().locations(); i++) {
            if(unit.getEntity().getNumberOfCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_ENGINE, i) > 0
                    && unit.isLocationDestroyed(i)) {
                return unit.getEntity().getLocationName(i) + " is destroyed.";
            }
        }
        return super.checkFixable();
    }

}
