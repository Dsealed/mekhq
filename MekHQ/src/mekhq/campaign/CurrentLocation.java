/*
 * CurrentLocation.java
 * 
 * Copyright (c) 2011 Jay Lawson <jaylawson39 at yahoo.com>. All rights reserved.
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

package mekhq.campaign;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;

import megamek.common.EquipmentType;
import megamek.common.PlanetaryConditions;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * This keeps track of a location, which includes both the planet
 * and the current position in-system. It may seem a little like
 * overkill to have a separate object here, but when we reach a point
 * where we want to let a force be in different locations, this will
 * make it easier to keep track of everything
 * 
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class CurrentLocation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4337642922571022697L;
	
	private Planet currentPlanet;
	//keep track of jump path
	private JumpPath jumpPath;
	private double rechargeTime;
	//I would like to keep track of distance, but I ain't too good with fyziks
	private double transitTime;
	
	
	public CurrentLocation(Planet planet, double time) {
		this.currentPlanet = planet;
		this.transitTime = time;
		this.rechargeTime = 0.0;
		this.transitTime = 0.0;
	}
	
	public void setCurrentPlanet(Planet p) {
		currentPlanet = p;
	}
	
	public void setTransitTime(double time) {
		transitTime = time;
	}
	
	public boolean isOnPlanet() {
		return transitTime <= 0;
	}
	
	public boolean isAtJumpPoint() {
		return transitTime >= currentPlanet.getTimeToJumpPoint(1.0);
	}
	
	public boolean isInTransit() {
		return !isOnPlanet() && !isAtJumpPoint();
	}
	
	public Planet getCurrentPlanet() {
		return currentPlanet;
	}
	
	public double getTransitTime() {
		return transitTime;
	}
	
	public String getReport(Date date) {
		String toReturn = "<b>Current Location</b><br>";
		toReturn += currentPlanet.getShortName() + " (" + Faction.getFactionName(currentPlanet.getCurrentFaction(date)) + ")<br>";
		if(isOnPlanet()) {
			toReturn += "<i>On Planet</i><br>";
		} 
		else if(isAtJumpPoint()) {
			toReturn += "<i>At Jump Point</i><br>";
		} else {
			toReturn += "<i>" + Math.round(100.0*getTransitTime())/100.0 + " days out </i><br>";
		}
		toReturn += "<i>" + Math.round(100.0*rechargeTime/currentPlanet.getRechargeTime()) + "% charged</i>";
		return "<html>" + toReturn + "</html>";
	}
	
	public JumpPath getJumpPath() {
		return jumpPath;
	}
	
	public void setJumpPath(JumpPath path) {
		jumpPath = path;
	}
	
	/**
	 * Check for a jump path and if found, do whatever needs to be done to move 
	 * forward
	 */
	public ArrayList<String> newDay() {
		ArrayList<String> reports = new ArrayList<String>();
		//recharge even if there is no jump path
		//because jumpships don't go anywhere
		double hours = 24.0;
		double usedRechargeTime = Math.min(hours, currentPlanet.getRechargeTime() - rechargeTime);
		if(usedRechargeTime > 0) {
			reports.add("Jumpships spent " + Math.round(100.0 * usedRechargeTime)/100.0 + " hours recharging drives");
			rechargeTime += usedRechargeTime;
			if(rechargeTime >= currentPlanet.getRechargeTime()) {
				reports.add("Jumpship drives full charged");
			}
		}
		if(null == jumpPath || jumpPath.isEmpty()) {
			return reports;
		}
		//if we are not at the final jump point, then check to see if we are transiting
		//or if we can jump
		if(jumpPath.size() > 1) {
			//first check to see if we are transiting
			double usedTransitTime = Math.min(hours, 24.0 * (currentPlanet.getTimeToJumpPoint(1.0) - transitTime));
			if(usedTransitTime > 0) {
				transitTime += usedTransitTime/24.0;
				reports.add("Dropships spent " + Math.round(100.0 * usedTransitTime)/100.0 + " hours in transit to jump point");
				if(isAtJumpPoint()) {
					reports.add("Jump point reached");
				}
			}
			if(isAtJumpPoint() && rechargeTime >= currentPlanet.getRechargeTime()) {
				//jump
				reports.add("Jumping to " + jumpPath.get(1).getShortName());
				currentPlanet = jumpPath.get(1);
				jumpPath.removeFirstPlanet();
				//reduce remaining hours by usedRechargeTime or usedTransitTime, whichever is greater
				hours -= Math.max(usedRechargeTime, usedTransitTime);
				rechargeTime = hours;
				transitTime = currentPlanet.getTimeToJumpPoint(1.0);
				//if there are hours remaining, then begin recharging jump drive
				usedRechargeTime = Math.min(hours, currentPlanet.getRechargeTime() - rechargeTime);
				if(usedRechargeTime > 0) {
					reports.add("Jumpships spent " + Math.round(100.0 * usedRechargeTime)/100.0 + " hours recharging drives");
					rechargeTime += usedRechargeTime;
					if(rechargeTime >= currentPlanet.getRechargeTime()) {
						reports.add("Jumpship drives full charged");
					}
				}
			}
		}
		//if we are now at the final jump point, then lets begin in-system transit
		if(jumpPath.size() == 1) {
			double usedTransitTime = Math.min(hours, 24.0 * transitTime);
			reports.add("Dropships spent " + Math.round(100.0 * usedTransitTime)/100.0 + " hours transiting into system");
			transitTime -= usedTransitTime/24.0;
			if(transitTime <= 0) {
				reports.add(jumpPath.getLastPlanet().getShortName() + " reached.");
				//we are here!
				transitTime = 0;
				jumpPath = null;
			}
		}
		return reports;
	}
}