/*
 * TurretReplacement.java
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

import mekhq.campaign.Unit;
import mekhq.campaign.parts.Part;
import mekhq.campaign.parts.Turret;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class TurretReplacement extends LocationReplacement {
	private static final long serialVersionUID = -1023243731780275935L;

	public TurretReplacement() {
		this(null, 0);
	}

	public TurretReplacement(Unit unit, int i) {
        super(unit, i);
        this.time = 160;
        this.difficulty = -1;
        reCalc();
    }
    
    @Override
    public void reCalc() {
    	// Do nothing.
    	super.reCalc();
    }

    @Override
    public Part stratopsPartNeeded() {
        return new Turret(false, (int) unit.getEntity().getWeight());
    }
}
