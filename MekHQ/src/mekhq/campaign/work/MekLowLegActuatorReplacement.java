/*
 * MekLowLegActuatorReplacement.java
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
import mekhq.campaign.parts.MekLowLegActuator;
import mekhq.campaign.parts.Part;
/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekLowLegActuatorReplacement extends MekActuatorReplacement {

    public MekLowLegActuatorReplacement(Unit unit, int i) {
        super(unit, i);
        this.name = "Replace lower leg actuator (" + unit.getEntity().getLocationName(loc) + ")";
    }
    
    @Override
    public void fix() {
        unit.getEntity().removeCriticals(loc, new CriticalSlot(CriticalSlot.TYPE_SYSTEM, Mech.ACTUATOR_LOWER_LEG));
    }

    @Override
    public boolean sameAs(WorkItem task) {
        return (task instanceof MekLowLegActuatorReplacement
                && ((MekLowLegActuatorReplacement)task).getUnitId() == this.getUnitId()
                && ((MekLowLegActuatorReplacement)task).getLoc() == this.getLoc());
    }
    
    @Override
    public Part partNeeded() {
        return new MekLowLegActuator(false, unit.getEntity().getWeight());
    }
    
}
