/*
 * MekLifeSupportReplacement.java
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

import megamek.common.CriticalSlot;
import megamek.common.Mech;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.Unit;
import mekhq.campaign.parts.MekLifeSupport;
import mekhq.campaign.parts.Part;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekLifeSupportReplacement extends ReplacementItem {
	private static final long serialVersionUID = 2868956239838192108L;

	public MekLifeSupportReplacement(Unit unit) {
        super(unit);
        this.name = "Replace life support";
        this.time = 180;
        this.difficulty = -1;
    }
    
    @Override
    public void fix() {
        super.fix();
        unit.repairSystem(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT);
    }
    
    @Override
    public String checkFixable() {
        for(int i = 0; i < unit.getEntity().locations(); i++) {
            if(unit.getEntity().getNumberOfCriticals(CriticalSlot.TYPE_SYSTEM, Mech.SYSTEM_LIFE_SUPPORT, i) > 0
                    && unit.isLocationDestroyed(i)) {
                return unit.getEntity().getLocationName(i) + " is destroyed.";
            }
        }
        return super.checkFixable();
    }

    @Override
    public boolean sameAs(WorkItem task) {
        return (task instanceof MekLifeSupportReplacement
                && ((MekLifeSupportReplacement)task).getUnitId() == this.getUnitId());
    }

    @Override
    public Part stratopsPartNeeded() {
        return new MekLifeSupport(false, (int) unit.getEntity().getWeight());
    }

    @Override
    public SalvageItem getSalvage() {
        return new MekLifeSupportSalvage(unit);
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		writeToXmlEnd(pw1, indent, id);
	}
}
