/*
 * MekHandActuator.java
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
public class MekHandActuatorRepair extends MekActuatorRepair {

    public MekHandActuatorRepair(Unit unit, int h, int i) {
        super(unit, h, i);
        this.name = "Repair hand actuator (" + unit.getEntity().getLocationName(loc) + ")";
    }
    
    @Override
    public void fix() {
        unit.getEntity().removeCriticals(loc, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, Mech.ACTUATOR_HAND));
    }

    @Override
    public WorkItem replace() {
        return new MekHandActuatorReplacement(unit, loc);
    }

    @Override
    public boolean sameAs(WorkItem task) {
        return (task instanceof MekHandActuatorRepair
                && ((MekHandActuatorRepair)task).getUnitId() == this.getUnitId()
                && ((MekHandActuatorRepair)task).getLoc() == this.getLoc());
    }
}
