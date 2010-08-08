/*
 * MekInternalRepair.java
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

import megamek.common.Mech;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.Unit;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class MekInternalRepair extends InternalRepair {
	private static final long serialVersionUID = -4275717871512159638L;
	double percent;
    
    public MekInternalRepair(Unit unit, int i, double pct) {
        super(unit, i);
        this.time = 90;
        this.difficulty = -1;
        this.percent = pct;
        if(percent > 0.75) {
            this.time = 270;
            this.difficulty = 2;
        } else if(percent > 0.5) {
            this.time = 180;
            this.difficulty = 1;
        } else if (percent > 0.25) {
            this.time = 135;
            this.difficulty = 0;
        }
    }
    
    @Override
    public String getDetails() {
        String perString = Integer.toString((int)Math.floor(percent * 100)) + "% damage";
        return unit.getEntity().getLocationName(loc) + ", " + perString;
    }

    @Override
    public boolean sameAs(WorkItem task) {
        return (task instanceof MekInternalRepair
                && ((MekInternalRepair)task).getUnitId() == this.getUnitId()
                && ((MekInternalRepair)task).getLoc() == this.getLoc());
    }
    
    
    @Override
    public boolean canScrap() {
        return loc != Mech.LOC_CT;
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);
		writeToXmlEnd(pw1, indent, id);
	}
}

