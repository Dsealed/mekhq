/*
 * Campaign.java
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

package mekhq.campaign;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import megamek.client.RandomNameGenerator;
import megamek.common.ASFBay;
import megamek.common.Aero;
import megamek.common.BattleArmor;
import megamek.common.BattleArmorBay;
import megamek.common.Bay;
import megamek.common.CargoBay;
import megamek.common.Compute;
import megamek.common.Dropship;
import megamek.common.Entity;
import megamek.common.EntityMovementMode;
import megamek.common.Game;
import megamek.common.HeavyVehicleBay;
import megamek.common.Infantry;
import megamek.common.InfantryBay;
import megamek.common.Jumpship;
import megamek.common.LightVehicleBay;
import megamek.common.Mech;
import megamek.common.MechBay;
import megamek.common.MechFileParser;
import megamek.common.MechSummary;
import megamek.common.MechSummaryCache;
import megamek.common.Pilot;
import megamek.common.Player;
import megamek.common.Protomech;
import megamek.common.SmallCraftBay;
import megamek.common.Tank;
import megamek.common.TargetRoll;
import megamek.common.loaders.EntityLoadingException;
import mekhq.MekHQApp;
import mekhq.campaign.finances.Finances;
import mekhq.campaign.finances.Transaction;
import mekhq.campaign.mission.Contract;
import mekhq.campaign.mission.Mission;
import mekhq.campaign.mission.Scenario;
import mekhq.campaign.parts.AmmoBin;
import mekhq.campaign.parts.Armor;
import mekhq.campaign.parts.EquipmentPart;
import mekhq.campaign.parts.GenericSparePart;
import mekhq.campaign.parts.MissingEquipmentPart;
import mekhq.campaign.parts.MissingPart;
import mekhq.campaign.parts.Part;
import mekhq.campaign.personnel.Person;
import mekhq.campaign.personnel.Skill;
import mekhq.campaign.personnel.SkillType;
import mekhq.campaign.team.MedicalTeam;
import mekhq.campaign.team.SupportTeam;
import mekhq.campaign.team.TechTeam;
import mekhq.campaign.work.IAcquisitionWork;
import mekhq.campaign.work.IMedicalWork;
import mekhq.campaign.work.IPartWork;
import mekhq.campaign.work.Modes;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author Taharqa The main campaign class, keeps track of teams and units
 */
public class Campaign implements Serializable {
	private static final long serialVersionUID = -6312434701389973056L;
	// we have three things to track: (1) teams, (2) units, (3) repair tasks
	// we will use the same basic system (borrowed from MegaMek) for tracking
	// all three
	// OK now we have more, parts, personnel, forces, missions, and scenarios.
	private ArrayList<SupportTeam> teams = new ArrayList<SupportTeam>();
	private Hashtable<Integer, SupportTeam> teamIds = new Hashtable<Integer, SupportTeam>();
	private ArrayList<Unit> units = new ArrayList<Unit>();
	private Hashtable<Integer, Unit> unitIds = new Hashtable<Integer, Unit>();
	private ArrayList<Person> personnel = new ArrayList<Person>();
	private Hashtable<Integer, Person> personnelIds = new Hashtable<Integer, Person>();
	private ArrayList<Part> parts = new ArrayList<Part>();
	private Hashtable<Integer, Part> partIds = new Hashtable<Integer, Part>();
	private Hashtable<Integer, Force> forceIds = new Hashtable<Integer, Force>();
	private ArrayList<Mission> missions = new ArrayList<Mission>();
	private Hashtable<Integer, Mission> missionIds = new Hashtable<Integer, Mission>();
	private Hashtable<Integer, Scenario> scenarioIds = new Hashtable<Integer, Scenario>();

	private int astechPool;
	private int astechPoolMinutes;
	private int astechPoolOvertime;
	
	private int lastTeamId;
	private int lastUnitId;
	private int lastPersonId;
	private int lastPartId;
	private int lastForceId;
	private int lastMissionId;
	private int lastScenarioId;

	// I need to put a basic game object in campaign so that I can
	// asssign it to the entities, otherwise some entity methods may get NPE
	// if they try to call up game options
	private Game game;

	private String name;

	private RandomNameGenerator rng;
	
	//hierarchically structured Force object to define TO&E
	private Force forces;
	
	// calendar stuff
	public GregorianCalendar calendar;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat shortDateFormat;

	private int faction;
	private Ranks ranks;
	private SkillCosts skillCosts;

	private ArrayList<String> currentReport;

	private boolean overtime;
	private boolean gmMode;

	private String camoCategory = Player.NO_CAMO;
	private String camoFileName = null;
	private int colorIndex = 0;

	private Finances finances;

	private CurrentLocation location;
	
	private CampaignOptions campaignOptions = new CampaignOptions();

	public Campaign() {
		game = new Game();
		currentReport = new ArrayList<String>();
		calendar = new GregorianCalendar(3067, Calendar.JANUARY, 1);
		dateFormat = new SimpleDateFormat("EEEE, MMMM d yyyy");
		shortDateFormat = new SimpleDateFormat("MMddyyyy");
		addReport("<b>" + getDateAsString() + "</b>");
		name = "My Campaign";
		rng = new RandomNameGenerator();
		rng.populateNames();
		overtime = false;
		gmMode = false;
		faction = Faction.F_MERC;
		ranks = new Ranks();
		skillCosts = new SkillCosts();
		forces = new Force(name);
		forceIds.put(new Integer(lastForceId), forces);
		lastForceId++;
		finances = new Finances();
		location = new CurrentLocation(Planets.getInstance().getPlanets().get("Outreach"), 0);
		SkillType.initializeTypes();
		astechPool = 0;
		resetAstechMinutes();
	}

	public String getName() {
		return name;
	}

	public void setName(String s) {
		this.name = s;
	}

	public String getEraName() {
		return Era.getEraNameFromYear(calendar.get(Calendar.YEAR));
	}

	public int getEraMod() {
		return Era.getEraMod(getEra(),
				getFaction());
	}
	
	public int getEra() {
		return Era.getEra(calendar.get(Calendar.YEAR));
	}

	public String getTitle() {
		return getName() + " (" + getFactionName() + ")" + " - "
				+ getDateAsString() + " (" + getEraName() + ")";
	}

	public GregorianCalendar getCalendar() {
		return calendar;
	}

	public RandomNameGenerator getRNG() {
		return rng;
	}
	
	public void setRNG(RandomNameGenerator g) {
		this.rng = g;
	}
	
	public String getCurrentPlanetName() {
		return location.getCurrentPlanet().getShortName();
	}
	
	public Planet getCurrentPlanet() {
		return location.getCurrentPlanet();
	}

	public SkillCosts getSkillCosts() {
		return skillCosts;
	}
	
	public long getFunds() {
		return finances.getBalance();
	}
	
	public Force getForces() {
		return forces;
	}

	/**
	 * Add force to an existing superforce. This method will also
	 * assign the force an id and place it in the forceId hash
	 * @param force - the Force to add
	 * @param superForce - the superforce to add the new force to
	 */
	public void addForce(Force force, Force superForce) {
		int id = lastForceId + 1;
		force.setId(id);
		superForce.addSubForce(force, true);
		forceIds.put(new Integer(id), force);
		lastForceId = id;
		
	}
	
	/**
	 * This is used by the XML loader. The id should already be
	 * set for this force so dont increment
	 * @param force
	 */
	public void addForceToHash(Force force) {
		forceIds.put(force.getId(), force);
	}
	
	/**
	 * This is used by the XML loader. The id should already be
	 * set for this scenario so dont increment
	 * @param force
	 */
	public void addScenarioToHash(Scenario scenario) {
		scenarioIds.put(scenario.getId(), scenario);
	}
	
	/**
	 * Add unit to an existing force. This method will also
	 * assign that force's id to the unit.
	 * @param p
	 * @param id
	 */
	public void addUnitToForce(Unit u, int id) {
		Force force = forceIds.get(id);
		if(null != force) {
			u.setForceId(id);
			force.addUnit(u.getId());
			//p.setScenarioId(force.getScenarioId());
		}
	}
	
	/**
	 * Add a support team to the campaign
	 * 
	 * @param t
	 *            The support team to be added
	 */
	public void addTeam(SupportTeam t) {
		int id = lastTeamId + 1;
		t.setId(id);
		teams.add(t);
		teamIds.put(new Integer(id), t);
		lastTeamId = id;
	}

	private void addTeamWithoutId(SupportTeam t) {
		teams.add(t);
		teamIds.put(new Integer(t.getId()), t);
		
		if (t.getId() > lastTeamId)
			lastTeamId = t.getId();
	}
	
	/**
	 * Add a mission to the campaign
	 * 
	 * @param 
	 *            The mission to be added
	 */
	public int addMission(Mission m) {
		int id = lastMissionId + 1;
		m.setId(id);
		missions.add(m);
		missionIds.put(new Integer(id), m);
		lastMissionId = id;
		return id;
	}

	private void addMissionWithoutId(Mission m) {
		missions.add(m);
		missionIds.put(new Integer(m.getId()), m);
		
		if (m.getId() > lastMissionId)
			lastMissionId = m.getId();
	}
	
	/**
	 * @return an <code>ArrayList</code> of missions in the campaign
	 */
	public ArrayList<Mission> getMissions() {
		return missions;
	}
	
	/**
	 * Add scenario to an existing mission. This method will also
	 * assign the scenario an id and place it in the scenarioId hash
	 * @param s - the Scenario to add
	 * @param m - the mission to add the new scenario to
	 */
	public void addScenario(Scenario s, Mission m) {
		int id = lastScenarioId + 1;
		s.setId(id);
		m.addScenario(s);
		scenarioIds.put(new Integer(id), s);
		lastScenarioId = id;
		
	}

	public ArrayList<Mission> getActiveMissions() {
		ArrayList<Mission> active = new ArrayList<Mission>();
		for(Mission m : getMissions()) {
			if(m.isActive()) {
				active.add(m);
			}
		}
		return active;
	}
	
	/**
	 * @param id
	 *            the <code>int</code> id of the team
	 * @return a <code>SupportTeam</code> object
	 */
	public Mission getMission(int id) {
		return missionIds.get(new Integer(id));
	}
	
	public Scenario getScenario(int id) {
		return scenarioIds.get(new Integer(id));
	}
	
	public CurrentLocation getLocation() {
		return location;
	}

	private void addUnit(Unit u) {
		MekHQApp.logMessage("Adding unit: ("+u.getId()+"):"+u, 5);
		units.add(u);
		unitIds.put(new Integer(u.getId()), u);
		
		if (u.getId() > lastUnitId)
			lastUnitId = u.getId();
	}
	
	/**
	 * Add a unit to the campaign. This is only for new units
	 * 
	 * @param en
	 *            An <code>Entity</code> object that the new unit will be
	 *            wrapped around
	 */
	public void addUnit(Entity en, boolean allowNewPilots) {
		// TODO: check for duplicate display names

		//reset the game object
		en.setGame(game);
		
		int id = lastUnitId + 1;
		en.setId(id);
		en.setExternalId(id);
		Unit unit = new Unit(en, this);
		unit.setId(id);
		units.add(unit);
		unitIds.put(new Integer(id), unit);
		lastUnitId = id;
			
		// collect all the work items outstanding on this unit and add them
		// to the workitem vector
		unit.initializeParts();
		unit.resetPilotAndEntity();
		addReport(unit.getEntity().getDisplayName() + " has been added to the unit roster.");
	}

	public ArrayList<Unit> getUnits() {
		return units;
	}

	public ArrayList<Entity> getEntities() {
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for (Unit unit : getUnits()) {
			entities.add(unit.getEntity());
		}
		return entities;
	}

	public Unit getUnit(int id) {
		return unitIds.get(new Integer(id));
	}

	public void addPerson(Person p) {
		int id = lastPersonId + 1;
		p.setId(id);
		personnel.add(p);
		personnelIds.put(new Integer(id), p);
		lastPersonId = id;
		addReport(p.getName() + " has been added to the personnel roster.");
		if(p.getType() == Person.T_ASTECH) {
			astechPoolMinutes += 480;
			astechPoolOvertime += 240;
		}
	}
	
	private void addPersonWithoutId(Person p) {
		personnel.add(p);
		personnelIds.put(p.getId(), p);
		
		if (p.getId() > lastPersonId)
			lastPersonId = p.getId();
		
		//TODO: Should this have runDiagnostic on the person here?...
	}

	public ArrayList<Person> getPersonnel() {
		return personnel;
	}
	
	public ArrayList<Person> getPatients() {
		ArrayList<Person> patients = new ArrayList<Person>();
		for(Person p : getPersonnel()) {
			if(p.needsFixing()) {
				patients.add(p);
			}
		}
		return patients;
	}
	
	public ArrayList<Unit> getServiceableUnits() {
		ArrayList<Unit> service = new ArrayList<Unit>();
		for(Unit u : getUnits()) {
			if(u.isDeployed()) {
				continue;
			}		
			if(u.isSalvage() || !u.isRepairable()) {
				if(u.getSalvageableParts().size() > 0) {
					service.add(u);
				}
			} else {
				if(u.getPartsNeedingFixing().size() > 0) {
					service.add(u);
				}
			}
		}
		return service;
	}

	public Person getPerson(int id) {
		return personnelIds.get(new Integer(id));
	}

	public void addPart(Part p) {

		if (p instanceof GenericSparePart) {
			for (Part part : getParts()) {
				if (part instanceof GenericSparePart
						&& p.isSamePartTypeAndStatus(part)) {
					((GenericSparePart) part)
							.setAmount(((GenericSparePart) part).getAmount()
									+ ((GenericSparePart) p).getAmount());
					return;
				}
			}
		}

		int id = lastPartId + 1;
		p.setId(id);
		parts.add(p);
		partIds.put(new Integer(id), p);
		lastPartId = id;
		if(p instanceof Armor) {
			updateAllArmorForNewSpares();
		}
	}
	
	/**
	 * call this whenever armor spare parts are changed so that 
	 * armor knows whether it gets partial repairs or not
	 */
	public void updateAllArmorForNewSpares() {
		for(Part part : getParts()) {
			if(part instanceof Armor) {
				Armor a = (Armor)part;
				if(null != a.getUnit() && a.needsFixing()) {
					a.updateConditionFromEntity();
				}
			}
		}
	}
	
	private void addPartWithoutId(Part p) {
		parts.add(p);
		partIds.put(p.getId(), p);
		
		if (p.getId() > lastPartId)
			lastPartId = p.getId();
	}

	/**
	 * @return an <code>ArrayList</code> of SupportTeams in the campaign
	 */
	public ArrayList<Part> getParts() {
		return parts;
	}

	public Part getPart(int id) {
		return partIds.get(new Integer(id));
	}
	
	public Force getForce(int id) {
		return forceIds.get(new Integer(id));
	}

	public ArrayList<String> getCurrentReport() {
		return currentReport;
	}

	public String getCurrentReportHTML() {
		String toReturn = "";
		//lets do the report backwards
		for (String s : currentReport) {
			toReturn += s + "<br/>";
		}
		return toReturn;
	}

	public ArrayList<Person> getTechs() {
		ArrayList<Person> techs = new ArrayList<Person>();
		for (Person p: personnel) {
			if (p.isTech() && p.isActive()) {
				techs.add(p);
			}
		}
		return techs;
	}

	/**
	 * return an html report on this unit. This will go in MekInfo
	 * 
	 * @param unitId
	 * @return
	 */
	public String getUnitDesc(int unitId) {
		Unit unit = getUnit(unitId);
		String toReturn = "<html><font size='2'";
		if (unit.isDeployed()) {
			toReturn += " color='white'";
		}
		toReturn += ">";
		toReturn += unit.getDescHTML();
		int totalMin = 0;
		int total = 0;
		//int cost = unit.getRepairCost();

		if (total > 0) {
			toReturn += "Total tasks: " + total + " (" + totalMin
					+ " minutes)<br/>";
		}
		/*
		if (cost > 0) {
			NumberFormat numberFormat = DecimalFormat.getIntegerInstance();
			String text = numberFormat.format(cost) + " "
					+ (cost != 0 ? "CBills" : "CBill");
			toReturn += "Repair cost : " + text + "<br/>";
		}
		*/

		toReturn += "</font>";
		toReturn += "</html>";
		return toReturn;
	}
	
	public String healPerson(IMedicalWork medWork, MedicalTeam t) {
		String report = "";
		report += t.getName() + " attempts to heal " + medWork.getPatientName();   
		TargetRoll target = t.getTargetFor(medWork);
		int roll = Compute.d6(2);
		report = report + ",  needs " + target.getValueAsString() + " and rolls " + roll + ":";
		if(roll >= target.getValue()) {
			report = report + medWork.succeed();	
		} else {
			report = report + medWork.fail(0);
		}
		return report;
	}
	
	public void acquirePart(IAcquisitionWork acquisition, Person person) {
		String report = "";
		report += person.getName() + " attempts to find " + acquisition.getPartName();          
		TargetRoll target = getTargetForAcquisition(acquisition, person);     
		acquisition.setCheckedToday(true);
		int roll = Compute.d6(2);
		report += "  needs " + target.getValueAsString();
		report += " and rolls " + roll + ":";		
		if(roll >= target.getValue()) {
			report = report + acquisition.find();	
		} else {
			report = report + acquisition.failToFind();
		}
		addReport(report);
	}
	
	public void fixPart(IPartWork partWork, Person tech) {
		TargetRoll target = getTargetFor(partWork, tech);
		String report = "";
		String action = " fix ";
		if(partWork instanceof AmmoBin) {
			action = " reload ";
		}
		if(partWork.isSalvaging()) {
			action = " salvage ";
		}
		if(partWork instanceof MissingPart) {
			action = " replace ";
		}
		report += tech.getName() + " attempts to" + action + partWork.getPartName();   
		int minutes = partWork.getTimeLeft();
		int minutesUsed = minutes;
		boolean usedOvertime = false;
		if(minutes > tech.getMinutesLeft()) {
			minutes -= tech.getMinutesLeft();
			//check for overtime first
			if(isOvertimeAllowed() && minutes <= tech.getOvertimeLeft()) {
	               //we are working overtime
				   usedOvertime = true;
	               tech.setMinutesLeft(0);
	               tech.setOvertimeLeft(tech.getOvertimeLeft() - minutes);
			} else {
				//we need to finish the task tomorrow
				minutesUsed = tech.getMinutesLeft();
				if(isOvertimeAllowed()) {
					minutesUsed += tech.getOvertimeLeft();
					partWork.setWorkedOvertime(true);
					usedOvertime = false;
				}
				partWork.addTimeSpent(minutesUsed);
				tech.setMinutesLeft(0);
				tech.setOvertimeLeft(0);
				int helpMod = getShorthandedMod(getAvailableAstechs(minutesUsed,usedOvertime), false);
		        if(partWork.getShorthandedMod() < helpMod) {
		        	partWork.setShorthandedMod(helpMod);
		        }
				partWork.setTeamId(tech.getId());
				report += " - <b>Not enough time, the remainder of the task will be finished tomorrow.</b>";
				addReport(report);
	             return;
			}     
		} else {
			tech.setMinutesLeft(tech.getMinutesLeft() - minutes);
		}
		int astechMinutesUsed = minutesUsed * getAvailableAstechs(minutesUsed, usedOvertime);
		if(astechPoolMinutes < astechMinutesUsed) {
			astechMinutesUsed -= astechPoolMinutes;
			astechPoolMinutes = 0;
			astechPoolOvertime -= astechMinutesUsed;
		} else {
			astechPoolMinutes -= astechMinutesUsed;
		}
		//check for the type
		int roll;
		String wrongType = "";
		if(tech.isRightTechTypeFor(partWork.getUnit())) {	
			roll = Compute.d6(2);
		} else {
			roll = Utilities.roll3d6();
			wrongType = " <b>Warning: wrong tech type for this repair.</b>";
		}
		report = report + ",  needs " + target.getValueAsString() + " and rolls " + roll + ":";
		if(roll >= target.getValue()) {
			report = report + partWork.succeed();	
		} else {
			int modePenalty = Modes.getModeExperienceReduction(partWork.getMode());
			report = report + partWork.fail(tech.getSkillForWorkingOn(partWork.getUnit()).getExperienceLevel()-modePenalty);
		}
		report += wrongType;
		partWork.setTeamId(-1);
		addReport(report);
	}

	public void newDay() {
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		addReport("<p/><b>" + getDateAsString() + "</b>");

		location.newDay(this);
	
		for (Person p : getPersonnel()) {
			p.resetMinutesLeft();
			if(p.needsFixing()) {
				/*
				SupportTeam t = getTeam(p.getAssignedTeamId());
				if(null != t && t instanceof MedicalTeam) {
					addReport(healPerson(p, (MedicalTeam)t));
				} else if(p.checkNaturalHealing()) {
					addReport(p.getDesc() + " heals naturally!");
				}
				*/
			} 
		}
		resetAstechMinutes();
		//need to check for assigned tasks in two steps to avoid
		//concurrent mod problems
		ArrayList<Integer> assignedPartIds = new ArrayList<Integer>();
		for(Part part : getParts()) {
			if(null != part.getUnit() && part.getAssignedTeamId() != -1) {
				assignedPartIds.add(part.getId());
			}
			if(part instanceof IAcquisitionWork) {
				((IAcquisitionWork)part).setCheckedToday(false);
			}
		}
		for(int pid : assignedPartIds) {
			Part part = getPart(pid);
			if(null != part) {
				Person tech = getPerson(part.getAssignedTeamId());
				if(null != tech) {
					fixPart(part, tech);
				}
			}
		}
		DecimalFormat formatter = new DecimalFormat();
		//check for a new year
		if(calendar.get(Calendar.MONTH) == 0 && calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			//clear the ledger
			finances.newFiscalYear(calendar.getTime());
		}
		if(calendar.get(Calendar.DAY_OF_WEEK) == 0) {
			//maintenance costs
			if(campaignOptions.payForMaintain()) {
				finances.debit(getMaintenanceCosts(), Transaction.C_MAINTAIN, "Weekly Maintenance", calendar.getTime());
				addReport("Your account has been debited for " + formatter.format(getMaintenanceCosts()) + " C-bills in maintenance costs");
			}
		}
		if(calendar.get(Calendar.DAY_OF_MONTH) == 1) {
			//check for contract payments
			for(Contract contract : getActiveContracts()) {
				finances.credit(contract.getMonthlyPayOut(), Transaction.C_CONTRACT, "Monthly payment for " + contract.getName(), calendar.getTime());
				addReport("Your account has been credited for " + formatter.format(contract.getMonthlyPayOut()) + " C-bills for the monthly payout from contract " + contract.getName());
			}
			//Payday!
			if(campaignOptions.payForSalaries()) {
				finances.debit(getPayRoll(), Transaction.C_SALARY, "Monthly salaries", calendar.getTime());
				addReport("Payday! Your account has been debited for " + formatter.format(getPayRoll()) + " C-bills in personnel salaries");
			}
			if(campaignOptions.payForOverhead()) {
				finances.debit(getOverheadExpenses(), Transaction.C_OVERHEAD, "Monthly overhead", calendar.getTime());
				addReport("Your account has been debited for " + formatter.format(getOverheadExpenses()) + " C-bills in overhead expenses");
			}
		}
	}
	
	private ArrayList<Contract> getActiveContracts() {
		ArrayList<Contract> active = new ArrayList<Contract>();
		for(Mission m : getMissions()) {
			if(!(m instanceof Contract)) {
				continue;
			}
			Contract c = (Contract)m;
			if(c.isActive() 
					&& !getCalendar().getTime().after(c.getEndingDate())
					&& !getCalendar().getTime().before(c.getStartDate())) {
				active.add(c);
			}
		}
		return active;
	}

	public long getPayRoll() {
		long salaries = 0;
		for(Person p : personnel) {
			if(p.isActive()) {
				salaries += p.getSalary();
			}
		}
		//add in astechs from the astech pool
		//we will assume vee mechanic * able-bodied * enlisted
		//640 * 0.5 * 0.6 = 192
		salaries += (192 * astechPool);
		return salaries;
	}
	
	public long getSupportPayRoll() {
		long salaries = 0;
		for(Person p : personnel) {
			if(p.isActive() && p.isSupport()) {
				salaries += p.getSalary();
			}
		}
		return salaries;
	}
	
	public long getMaintenanceCosts() {
		long costs = 0;
		for(Unit u : units) {
			if(!u.isSalvage()) {
				costs += u.getMaintenanceCost();
			}
		}
		return costs;
	}
	
	public long getOverheadExpenses() {
		return (long)(getPayRoll() * 0.05);
	}

	public void clearAllUnits() {
		this.units = new ArrayList<Unit>();
		this.unitIds = new Hashtable<Integer, Unit>();
		this.lastUnitId = 0;
		//TODO: clear parts associated with unit

	}

	public void removeUnit(int id) {
		Unit unit = getUnit(id);

		//remove all parts for this unit as well
		for(Part p : unit.getParts()) {
			removePart(p);
		}

		// remove any personnel from this unit
		for(Person p : unit.getCrew()) {
			unit.remove(p);
		}

		// finally remove the unit
		units.remove(unit);
		unitIds.remove(new Integer(unit.getId()));
		addReport(unit.getEntity().getDisplayName()
				+ " has been removed from the unit roster.");
	}

	public void removePerson(int id) {
		Person person = getPerson(id);

		Unit u = getUnit(person.getUnitId());
		if(null != u) {
			u.remove(person);
		}

		addReport(person.getDesc()
				+ " has been removed from the personnel roster.");
		personnel.remove(person);
		personnelIds.remove(new Integer(id));
		if(person.getType() == Person.T_ASTECH) {
			astechPoolMinutes = Math.max(0, astechPoolMinutes - 480);
			astechPoolOvertime = Math.max(0, astechPoolOvertime - 240);
		}
	}
	
	public void removeScenario(int id) {
		Scenario scenario = getScenario(id);	
		scenario.clearAllForcesAndPersonnel(this);
		Mission mission = getMission(scenario.getMissionId());
		if(null != mission) {
			mission.removeScenario(scenario.getId());
		}
		scenarioIds.remove(new Integer(id));
	}

	public void removePart(Part part) {
		parts.remove(part);
		partIds.remove(new Integer(part.getId()));
	}

	public void removeForce(Force force) {
		int fid = force.getId();
		forceIds.remove(new Integer(fid));
		//clear forceIds of all personnel with this force
		for(Unit u : units) {
			if(u.getForceId() == fid) {
				u.setForceId(-1);
				if(force.isDeployed()) {
					//p.setScenarioId(-1);
				}
			}
		}
		//also remove this force's id from any scenarios
		if(force.isDeployed()) {
			Scenario s = getScenario(force.getScenarioId());
			s.removeForce(fid);
		}
		if(null != force.getParentForce()) {
			force.getParentForce().removeSubForce(fid);
		}	
	}
	
	public void removeUnitFromForce(Unit u) {
		Force force = getForce(u.getForceId());
		if(null != force) {
			force.removeUnit(u.getId());
			u.setForceId(-1);
			//p.setScenarioId(-1);
		}
	}
	 
	public Force getForceFor(Unit u) {
		return getForce(u.getForceId());
	}
	
	public Force getForceFor(Person p) {
		Unit u = getUnit(p.getUnitId());
		if(null == u) {
			return null;
		} else {
			return getForceFor(u);
		}
	}

	/**
	 * return a string (HTML formatted) of tasks for this doctor
	 * 
	 * @param unit
	 * @return
	 */
	public String getToolTipFor(MedicalTeam doctor) {
		String toReturn = "<html><b>Tasks:</b><br/>";
		toReturn += "</html>";
		return toReturn;
	}

	public String getDateAsString() {
		return dateFormat.format(calendar.getTime());
	}

	public String getShortDateAsString() {
		return shortDateFormat.format(calendar.getTime());
	}

	public ArrayList<Person> getEligiblePilotsFor(Unit unit) {
		ArrayList<Person> pilots = new ArrayList<Person>();
		for (Person p : getPersonnel()) {
			/*
			if (!(p instanceof PilotPerson)) {
				continue;
			}
			PilotPerson pp = (PilotPerson) p;
			if (pp.canPilot(unit.getEntity())) {
				pilots.add(pp);
			}
			*/
		}
		return pilots;
	}
	
	public ArrayList<Unit> getEligibleUnitsFor2(Person person) {
		ArrayList<Unit> units = new ArrayList<Unit>();
		for (Unit u : this.getUnits()) {
			//if (person.canPilot(u.getEntity())) {
				units.add(u);
			//}
		}
		return units;
	}

	/*
	public void changePilot(Unit unit, PilotPerson pilot) {
		if (null != pilot.getAssignedUnit()) {
			pilot.getAssignedUnit().removePilot();
		}
		unit.setPilot(pilot);
	}
	*/

	public void restore() {
		for (Part part : getParts()) {
			if (part instanceof EquipmentPart) {
				((EquipmentPart) part).restore();
			}
			if (part instanceof MissingEquipmentPart) {
				((MissingEquipmentPart) part).restore();
			}
		}

		for (Unit unit : getUnits()) {
			if (null != unit.getEntity()) {
				unit.getEntity().setGame(game);
				unit.getEntity().restore();
			}
		}
	}

	public boolean isOvertimeAllowed() {
		return overtime;
	}

	public void setOvertime(boolean b) {
		this.overtime = b;
	}

	public boolean isGM() {
		return gmMode;
	}

	public void setGMMode(boolean b) {
		this.gmMode = b;
	}

	public int getFaction() {
		return faction;
	}

	public void setFaction(int i) {
		this.faction = i;
	}

	public String getFactionName() {
		return Faction.getFactionName(faction);
	}

	public void addReport(String r) {
		int maxLine = 150;
		while (currentReport.size() > maxLine) {
			currentReport.remove(currentReport.size()-1);
		}
		currentReport.add(0,r);
	}
	
	public void addReports(ArrayList<String> reports) {
		for(String r : reports) {
			addReport(r);
		}
	}

	public void setCamoCategory(String name) {
		camoCategory = name;
	}

	public String getCamoCategory() {
		return camoCategory;
	}

	public void setCamoFileName(String name) {
		camoFileName = name;
	}

	public String getCamoFileName() {
		return camoFileName;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public void setColorIndex(int index) {
		colorIndex = index;
	}

	public ArrayList<Part> getSpareParts() {
		ArrayList<Part> spares = new ArrayList<Part>();
		for(Part part : getParts()) {
			if(null == part.getUnit()) {
				spares.add(part);
			}
		}
		return spares;
	}
	
	/**
	 * Creates an {@link ArrayList} containing a {@link PartInventory} for each
	 * part owned ({@link parts})
	 * 
	 */
	// TODO : Add some kind of caching method to speed things up when lots of
	// parts
	public ArrayList<PartInventory> getPartsInventory() {
		ArrayList<PartInventory> partsInventory = new ArrayList<PartInventory>();

		Iterator<Part> itParts = getSpareParts().iterator();
		while (itParts.hasNext()) {
			Part part = itParts.next();
			if (!partsInventory.contains(new PartInventory(part, 0))) {
				partsInventory.add(new PartInventory(part, 1));
			} else {
				partsInventory.get(
						partsInventory.indexOf(new PartInventory(part, 0)))
						.addOnePart();
			}
		}

		return partsInventory;
	}

	public void addFunds(long quantity) {
		finances.credit(quantity, Transaction.C_MISC, "Rich Uncle", calendar.getTime());
		NumberFormat numberFormat = DecimalFormat.getIntegerInstance();
		String quantityString = numberFormat.format(quantity);
		addReport("Funds added : " + quantityString);
	}

	public boolean hasEnoughFunds(long cost) {
		return getFunds() >= cost;
	}
	
	public boolean buyUnit(Entity en, boolean allowNewPilots) {
		int cost = new Unit(en, this).getBuyCost();

		if (hasEnoughFunds(cost) || !campaignOptions.payForUnits()) {
			addUnit(en, allowNewPilots);
			if(campaignOptions.payForUnits()) {
				finances.debit(cost, Transaction.C_UNIT, "Purchased " + en.getDisplayName(), calendar.getTime());
			}
			return true;
		} else
			return false;
	}

	public void sellUnit(int id) {
		Unit unit = getUnit(id);
		int sellValue = unit.getSellValue();
		finances.credit(sellValue, Transaction.C_UNIT_SALE, "Sale of " + unit.getEntity().getDisplayName(), calendar.getTime());
		removeUnit(id);
	}

	public void sellPart(Part part) {
		long cost = part.getCurrentValue();
		finances.credit(cost, Transaction.C_EQUIP_SALE, "Sale of " + part.getName(), calendar.getTime());
		removePart(part);
	}

	public void buyPart(Part part, long cost) {
		addPart(part);
		if(getCampaignOptions().payForParts()) {
			finances.debit(cost, Transaction.C_EQUIP, "Purchase of " + part.getName(), calendar.getTime());		
		}
	}

	public static Entity getBrandNewUndamagedEntity(String entityShortName) {
		MechSummary mechSummary = MechSummaryCache.getInstance().getMech(
				entityShortName);
		if (mechSummary == null)
			return null;

		MechFileParser mechFileParser = null;
		try {
			mechFileParser = new MechFileParser(mechSummary.getSourceFile());
		} catch (EntityLoadingException ex) {
			Logger.getLogger(Campaign.class.getName()).log(Level.SEVERE,
					"MechFileParse exception : " + entityShortName, ex);
		}
		if (mechFileParser == null)
			return null;

		return mechFileParser.getEntity();
	}

	public CampaignOptions getCampaignOptions() {
		return campaignOptions;
	}

	public void writeToXml(PrintWriter pw1) {
		
		// File header
		pw1.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

		// Start the XML root.
		pw1.println("<campaign>");

		// Basic Campaign Info
		pw1.println("\t<info>");

		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "name", name);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "faction", faction);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "ranks", ranks.getRankSystem());
		if(ranks.getRankSystem() == Ranks.RS_CUSTOM) {
			MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "officerCut", ranks.getOfficerCut());
			MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "rankNames", ranks.getRankNameList());
		}
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "nameGen", rng.getChosenFaction());
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "percentFemale", rng.getPercentFemale());
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "overtime", overtime);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "gmMode", gmMode);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "astechPool", astechPool);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "astechPoolMinutes", astechPoolMinutes);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "astechPoolOvertime", astechPoolOvertime);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "camoCategory", camoCategory);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "camoFileName", camoFileName);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "colorIndex", colorIndex);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "lastTeamId", lastTeamId);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "lastUnitId", lastUnitId);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "lastPersonId", lastPersonId);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "lastPartId", lastPartId);
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "lastForceId", lastForceId);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		MekHqXmlUtil.writeSimpleXmlTag(pw1, 2, "calendar",
				df.format(calendar.getTime()));
		{
			pw1.println("\t\t<nameGen>");
			pw1.print("\t\t\t<faction>");
			pw1.print(rng.getChosenFaction());
			pw1.println("</faction>");
			pw1.print("\t\t\t<percentFemale>");
			pw1.print(rng.getPercentFemale());
			pw1.println("</percentFemale>");
			pw1.println("\t\t</nameGen>");
		}
		{
			pw1.println("\t\t<currentReport>");

			for (int x = 0; x < currentReport.size(); x++) {
				pw1.print("\t\t\t<reportLine><![CDATA[");
				pw1.print(currentReport.get(x));
				pw1.println("]]></reportLine>");
			}

			pw1.println("\t\t</currentReport>");
		}

		pw1.println("\t</info>");

		// Campaign Options
		// private CampaignOptions campaignOptions = new CampaignOptions();
		if (getCampaignOptions() != null)
			getCampaignOptions().writeToXml(pw1, 1);

		// Lists of objects:
		writeArrayAndHashToXml(pw1, 1, "teams", teams, teamIds); // Teams
		writeArrayAndHashToXml(pw1, 1, "units", units, unitIds); // Units
		writeArrayAndHashToXml(pw1, 1, "personnel", personnel, personnelIds); // Personnel
		writeArrayAndHashToXml(pw1, 1, "missions", missions, missionIds); // Missions
		//the forces structure is hierarchical, but that should be handled internally
		//from with writeToXML function for Force
		pw1.println("\t<forces>");
		forces.writeToXml(pw1, 2);
		pw1.println("\t</forces>");
		finances.writeToXml(pw1,1);
		location.writeToXml(pw1, 1);
		pw1.println("\t<skillCosts>");
		skillCosts.writeToXml(pw1, 2);
		pw1.println("\t</skillCosts>");
		//parts is the biggest so it goes last
		writeArrayAndHashToXml(pw1, 1, "parts", parts, partIds); // Parts
		
		// Okay, we're done.
		// Close everything out and be done with it.
		pw1.println("</campaign>");
	}

	/**
	 * A helper function to encapsulate writing the array/hash pairs out to XML.
	 * Each of the types requires a different XML structure, but is in an
	 * identical holding structure. Thus, genericized function and interface to
	 * cleanly wrap it up. God, I love 3rd-generation programming languages.
	 * 
	 * @param <arrType>
	 *            The object type in the list. Must implement
	 *            MekHqXmlSerializable.
	 * @param pw1
	 *            The PrintWriter to output XML to.
	 * @param indent
	 *            The indentation level to use for writing XML (purely for
	 *            neatness).
	 * @param tag
	 *            The name of the tag to use to encapsulate it.
	 * @param array
	 *            The list of objects to write out.
	 * @param hashtab
	 *            The lookup hashtable for the associated array.
	 */
	private <arrType> void writeArrayAndHashToXml(PrintWriter pw1, int indent,
			String tag, ArrayList<arrType> array,
			Hashtable<Integer, arrType> hashtab) {
		// Hooray for implicitly-type-detected genericized functions!
		// However, I still ended up making an interface to handle this.
		// That way, I can cast it and call "writeToXml" to make it cleaner.
		pw1.println(MekHqXmlUtil.indentStr(indent) + "<" + tag + ">");

		// Enumeration<Integer> = hashtab.keys
		for (int x : hashtab.keySet()) {
			((MekHqXmlSerializable) (hashtab.get(x))).writeToXml(pw1,
					indent + 1, x);
		}

		pw1.println(MekHqXmlUtil.indentStr(indent) + "</" + tag + ">");
	}
	
	 

	/**
	 * Designed to create a campaign object from a file containing an XML
	 * structure. Instead of actually manually parsing it all, lets pull it into
	 * a DOM and parse that.
	 * 
	 * @param fis
	 *            The file holding the XML, in FileInputStream form.
	 * @return The created Campaign object, or null if there was a problem.
	 * @throws ParseException
	 * @throws DOMException
	 */
	public static Campaign createCampaignFromXMLFileInputStream(FileInputStream fis)
			throws DOMException, ParseException {
		MekHQApp.logMessage("Starting load of campaign file from XML...");
		// Initialize variables.
		Campaign retVal = new Campaign();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document xmlDoc = null;

		try {
			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Parse using builder to get DOM representation of the XML file
			xmlDoc = db.parse(fis);
		} catch (Exception ex) {
			MekHQApp.logError(ex);
		}

		Element campaignEle = xmlDoc.getDocumentElement();
		NodeList nl = campaignEle.getChildNodes();

		// Get rid of empty text nodes and adjacent text nodes...
		// Stupid weird parsing of XML.  At least this cleans it up.
		campaignEle.normalize(); 

		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < nl.getLength(); x++) {
			Node wn = nl.item(x);

			if (wn.getParentNode() != campaignEle)
				continue;

			int xc = wn.getNodeType();

			if (xc == Node.ELEMENT_NODE) {
				// This is what we really care about.
				// All the meat of our document is in this node type, at this
				// level.
				// Okay, so what element is it?
				String xn = wn.getNodeName();

				if (xn.equalsIgnoreCase("campaignOptions")) {
					retVal.campaignOptions = CampaignOptions
							.generateCampaignOptionsFromXml(wn);
				} else if (xn.equalsIgnoreCase("info")) {
					processInfoNode(retVal, wn);
				} else if (xn.equalsIgnoreCase("parts")) {
					processPartNodes(retVal, wn);
				} else if (xn.equalsIgnoreCase("personnel")) {
					processPersonnelNodes(retVal, wn);
				} else if (xn.equalsIgnoreCase("teams")) {
					processTeamNodes(retVal, wn);
				} else if (xn.equalsIgnoreCase("units")) {
					processUnitNodes(retVal, wn);
				} else if (xn.equalsIgnoreCase("missions")) {
					processMissionNodes(retVal, wn);
				} else if (xn.equalsIgnoreCase("forces")) {
					processForces(retVal, wn);
				} else if (xn.equalsIgnoreCase("finances")) {
					processFinances(retVal, wn);
				} else if(xn.equalsIgnoreCase("location")) {
					retVal.location = CurrentLocation.generateInstanceFromXML(wn, retVal);
				} else if(xn.equalsIgnoreCase("skillCosts")) {
					retVal.skillCosts = SkillCosts.generateInstanceFromXML(wn);
				}
				
			} else {
				// If it's a text node or attribute or whatever at this level,
				// it's probably white-space.
				// We can safely ignore it even if it isn't, for now.
				continue;
			}
		}
		
		// Okay, after we've gone through all the nodes and constructed the Campaign object...
		// We need to do a post-process pass to restore a number of references.

		// First, iterate through Support Teams;
		// they have a reference to the Campaign object.
		for (int x=0; x<retVal.teams.size(); x++) {
			SupportTeam st = retVal.teams.get(x);
			
			// Okay, last trigger a reCalc.
			// This should fix some holes in the data.
			st.reCalc();
		}
		
		//loop through forces to set force id
		for(int fid : retVal.forceIds.keySet()) {
			Force f = retVal.forceIds.get(fid);
			Scenario s = retVal.getScenario(f.getScenarioId());
			if(null != s) {
				s.addForces(fid);
			}
			//some units may need force id set for backwards compatability
			//some may also need scenario id set
			for(int uid : f.getUnits()) {
				Unit u = retVal.getUnit(uid);
				if(null != u) {
					u.setForceId(f.getId());
					if(f.isDeployed()) {
						u.setScenarioId(f.getScenarioId());
					}
				}
			}
		}
		
		// Process parts...
		for (int x=0; x<retVal.parts.size(); x++) {
			Part prt = retVal.parts.get(x);
			Unit u = retVal.getUnit(prt.getUnitId());
			prt.setUnit(u);
			if(null != u) {
				u.addPart(prt);
			}
		}
		
		// All personnel need the rank reference fixed
		for (int x=0; x<retVal.personnel.size(); x++) {
			Person psn = retVal.personnel.get(x);
			
			psn.setRankSystem(retVal.ranks);
			
			//reverse compatability check for assigning support personnel
			//characteristics from their support team
			if (psn.getOldSupportTeamId() >= 0) {
				SupportTeam t = retVal.teamIds.get(psn.getOldSupportTeamId());
				psn.setName(t.getName());
				int lvl = 0;
				switch(t.getRating()) {
				case 0:
					lvl = 1;
					break;
				case 1:
					lvl = 3;
					break;
				case 2:
					lvl = 4;
					break;
				case 3:
					lvl = 5;
					break;
				}
				if(t instanceof TechTeam) {
					switch(((TechTeam) t).getType()) {
					case TechTeam.T_MECH:
						psn.setType(Person.T_MECH_TECH);
						psn.addSkill(SkillType.S_TECH_MECH, lvl, 0);
						break;
					case TechTeam.T_MECHANIC:
						psn.setType(Person.T_MECHANIC);
						psn.addSkill(SkillType.S_TECH_MECHANIC, lvl, 0);
						break;
					case TechTeam.T_AERO:
						psn.setType(Person.T_AERO_TECH);
						psn.addSkill(SkillType.S_TECH_AERO, lvl, 0);
						break;
					case TechTeam.T_BA:
						psn.setType(Person.T_BA_TECH);
						psn.addSkill(SkillType.S_TECH_BA, lvl, 0);
						break;
					}
				} else {
					psn.setType(Person.T_DOCTOR);
					psn.addSkill(SkillType.S_DOCTOR, lvl, 0);
				}
			}
		}
		
		// Okay, Units, need their pilot references fixed.
		for (int x=0; x<retVal.units.size(); x++) {
			Unit unit = retVal.units.get(x);
			
			// Also, the unit should have its campaign set.
			unit.campaign = retVal;
			
			//for reverse compatability check pilotId
			unit.reassignPilotReverseCompatabilityCheck();
	
			//reset the pilot and entity, to reflect newly assigned personnel
			unit.resetPilotAndEntity();
			
			//just in case parts are missing (i.e. because they weren't tracked in previous versions)
			unit.initializeParts();
			
			//some units might need to be assigned to scenarios
			Scenario s = retVal.getScenario(unit.getScenarioId());
			if(null != s) {
				//most units will be properly assigned through their
				//force, so check to make sure they aren't already here
				if(!s.isAssigned(unit, retVal)) {
					s.addUnit(unit.getId());
				}
			}
		}
		
		MekHQApp.logMessage("Load of campaign file complete!");

		return retVal;
	}

	private static void processFinances(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Finances from XML...", 4);
		retVal.finances = Finances.generateInstanceFromXML(wn);
		MekHQApp.logMessage("Load of Finances complete!");
	}

	
	private static void processForces(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Force Organization from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		boolean foundForceAlready = false;
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("force")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Forces nodes: "+wn2.getNodeName());

				continue;
			}
			
			if(!foundForceAlready)  {
				Force f = Force.generateInstanceFromXML(wn2, retVal);
				if(null != f) {
					retVal.forces = f;
					foundForceAlready = true;
				}
			} else {
				MekHQApp.logMessage("More than one type-level force found", 5);
			}
		}
		
		MekHQApp.logMessage("Load of Force Organization complete!");
	}
	
	private static void processPersonnelNodes(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Personnel Nodes from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("person")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Personnel nodes: "+wn2.getNodeName());

				continue;
			}

			Person p = Person.generateInstanceFromXML(wn2);
			
			if (p != null) {
				retVal.addPersonWithoutId(p);
			}
		}

		MekHQApp.logMessage("Load Personnel Nodes Complete!", 4);
	}
	
	private static void processMissionNodes(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Mission Nodes from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("mission")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Mission nodes: "+wn2.getNodeName());

				continue;
			}

			Mission m = Mission.generateInstanceFromXML(wn2);
			
			if (m != null) {
				//add scenarios to the scenarioId hash
				for(Scenario s : m.getScenarios()) {
					retVal.addScenarioToHash(s);
				}
				retVal.addMissionWithoutId(m);
			}
		}

		MekHQApp.logMessage("Load Mission Nodes Complete!", 4);
	}

	private static void processTeamNodes(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Team Nodes from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("supportTeam")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Team nodes: "+wn2.getNodeName());

				continue;
			}

			SupportTeam t = SupportTeam.generateInstanceFromXML(wn2);
			
			if (t != null) {
				retVal.addTeamWithoutId(t);
			}
		}

		MekHQApp.logMessage("Load Team Nodes Complete!", 4);
	}

	private static void processUnitNodes(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Unit Nodes from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("unit")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Unit nodes: "+wn2.getNodeName());

				continue;
			}

			Unit u = Unit.generateInstanceFromXML(wn2);
			
			if (u != null) {
				retVal.addUnit(u);
			}
		}

		MekHQApp.logMessage("Load Unit Nodes Complete!", 4);
	}

	private static void processPartNodes(Campaign retVal, Node wn) {
		MekHQApp.logMessage("Loading Part Nodes from XML...", 4);

		NodeList wList = wn.getChildNodes();
		
		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < wList.getLength(); x++) {
			Node wn2 = wList.item(x);

			// If it's not an element node, we ignore it.
			if (wn2.getNodeType() != Node.ELEMENT_NODE)
				continue;
			
			if (!wn2.getNodeName().equalsIgnoreCase("part")) {
				// Error condition of sorts!
				// Errr, what should we do here?
				MekHQApp.logMessage("Unknown node type not loaded in Part nodes: "+wn2.getNodeName());

				continue;
			}

			Part p = Part.generateInstanceFromXML(wn2);
			
			if (p != null)
				retVal.addPartWithoutId(p);
		}

		MekHQApp.logMessage("Load Part Nodes Complete!", 4);
	}

	/**
	 * Pulled out purely for encapsulation. Makes the code neater and easier to
	 * read.
	 * 
	 * @param retVal
	 *            The Campaign object that is being populated.
	 * @param wn
	 *            The XML node we're working from.
	 * @throws ParseException
	 * @throws DOMException
	 */
	private static void processInfoNode(Campaign retVal, Node wni)
			throws DOMException, ParseException {
		NodeList nl = wni.getChildNodes();

		// Okay, lets iterate through the children, eh?
		for (int x = 0; x < nl.getLength(); x++) {
			Node wn = nl.item(x);
			int xc = wn.getNodeType();

			// If it's not an element, again, we're ignoring it.
			if (xc == Node.ELEMENT_NODE) {
				String xn = wn.getNodeName();

				// Yeah, long if/then clauses suck.
				// I really couldn't think of a significantly better way to
				// handle it.
				// They're all primitives anyway...
				if (xn.equalsIgnoreCase("calendar")) {
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					retVal.calendar = (GregorianCalendar) GregorianCalendar
							.getInstance();
					retVal.calendar.setTime(df
							.parse(wn.getTextContent().trim()));
				} else if (xn.equalsIgnoreCase("camoCategory")) {
					String val = wn.getTextContent().trim();

					if (val.equals("null"))
						retVal.camoCategory = null;
					else
						retVal.camoCategory = val;
				} else if (xn.equalsIgnoreCase("camoFileName")) {
					String val = wn.getTextContent().trim();

					if (val.equals("null"))
						retVal.camoFileName = null;
					else
						retVal.camoFileName = val;
				} else if (xn.equalsIgnoreCase("colorIndex")) {
					retVal.colorIndex = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("nameGen")) {					
					// First, get all the child nodes;
					NodeList nl2 = wn.getChildNodes();	
					for (int x2 = 0; x2 < nl2.getLength(); x2++) {
						Node wn2 = nl2.item(x2);
						if (wn2.getParentNode() != wn)
							continue;
						if (wn2.getNodeName().equalsIgnoreCase("faction")) {
							retVal.getRNG().setChosenFaction(wn2.getTextContent().trim());
						} else if (wn2.getNodeName().equalsIgnoreCase("percentFemale")) {
							retVal.getRNG().setPerentFemale(Integer.parseInt(wn2.getTextContent().trim()));
						}
					}
				} else if (xn.equalsIgnoreCase("currentReport")) {
					// First, get all the child nodes;
					NodeList nl2 = wn.getChildNodes();
					
					// Then, make sure the report is empty.  *just* in case.
					// ...That is, creating a new campaign throws in a date line for us...
					// So make sure it's cleared out.
					retVal.currentReport.clear(); 

					for (int x2 = 0; x2 < nl2.getLength(); x2++) {
						Node wn2 = nl2.item(x2);

						if (wn2.getParentNode() != wn)
							continue;

						if (wn2.getNodeName().equalsIgnoreCase("reportLine"))
							retVal.currentReport.add(wn2.getTextContent());
					}
				} else if (xn.equalsIgnoreCase("faction")) {
					retVal.faction = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("ranks")) {
					int rankSystem = Integer.parseInt(wn.getTextContent().trim());
					if(rankSystem != Ranks.RS_CUSTOM) {
						retVal.ranks = new Ranks(rankSystem);
					}
				} else if (xn.equalsIgnoreCase("officerCut")) {
					retVal.ranks.setOfficerCut(Integer.parseInt(wn.getTextContent().trim()));
				} else if (xn.equalsIgnoreCase("rankNames")) {
					retVal.ranks.setRanksFromList(wn.getTextContent().trim());
				} else if (xn.equalsIgnoreCase("gmMode")) {
					if (wn.getTextContent().trim().equals("true"))
						retVal.gmMode = true;
					else
						retVal.gmMode = false;
				} else if (xn.equalsIgnoreCase("lastPartId")) {
					retVal.lastPartId = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("lastForceId")) {
					retVal.lastForceId = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("lastPersonId")) {
					retVal.lastPersonId = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("lastTeamId")) {
					retVal.lastTeamId = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("lastUnitId")) {
					retVal.lastUnitId = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("name")) {
					String val = wn.getTextContent().trim();

					if (val.equals("null"))
						retVal.name = null;
					else
						retVal.name = val;
				} else if (xn.equalsIgnoreCase("overtime")) {
					if (wn.getTextContent().trim().equals("true"))
						retVal.overtime = true;
					else
						retVal.overtime = false;
				} else if (xn.equalsIgnoreCase("astechPool")) {
					retVal.astechPool = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("astechPoolMinutes")) {
					retVal.astechPoolMinutes = Integer.parseInt(wn.getTextContent()
							.trim());
				} else if (xn.equalsIgnoreCase("astechPoolOvertime")) {
					retVal.astechPoolOvertime = Integer.parseInt(wn.getTextContent()
							.trim());
				}
			}
		}
	}
	
	public ArrayList<Planet> getPlanets() {
		ArrayList<Planet> plnts = new ArrayList<Planet>();
		for(String key : Planets.getInstance().getPlanets().keySet()) {
			plnts.add(Planets.getInstance().getPlanets().get(key));
		}
		return plnts;
	}
	
	public Vector<String> getPlanetNames() {
		Vector<String> plntNames = new Vector<String>();
		for(String key : Planets.getInstance().getPlanets().keySet()) {
			plntNames.add(key);
		}
		return plntNames;
	}
	
	public Planet getPlanet(String name) {
		return Planets.getInstance().getPlanets().get(name);
	}
	
	/**
	 * Generate a new pilotPerson of the given type
	 * using whatever randomization options have been
	 * given in the CampaignOptions
	 * @param type
	 * @return
	 */
	public Person newPerson(int type) {
		boolean isFemale = getRNG().isFemale();
		Person person = new Person();
		if(isFemale) {
			person.setGender(Person.G_FEMALE);
		}
		person.setName(getRNG().generate(isFemale));
		//now lets get a random birthdate, such that the person
		//is age 13+4d6 by default
		//TODO: let user specify age distribution
		GregorianCalendar birthdate = (GregorianCalendar)getCalendar().clone();	
		birthdate.set(Calendar.YEAR, birthdate.get(Calendar.YEAR) - (13 + Compute.d6(4)));
		//choose a random day and month
		int randomDay = Compute.randomInt(365)+1;
		if(birthdate.isLeapYear(birthdate.get(Calendar.YEAR))) {
			randomDay = Compute.randomInt(366)+1;
		}
		birthdate.set(Calendar.DAY_OF_YEAR, randomDay);
		person.setBirthday(birthdate);
		person.setType(type);
		//set default skills
		switch(type) {
		case(Person.T_MECHWARRIOR):
	        person.addSkill(SkillType.S_PILOT_MECH);
        	person.addSkill(SkillType.S_GUN_MECH);
        	break;
	    case(Person.T_VEE_CREW):
	    	person.addSkill(SkillType.S_PILOT_GVEE);
    		person.addSkill(SkillType.S_GUN_VEE);
    		break;
	    case(Person.T_AERO_PILOT):
	    	person.addSkill(SkillType.S_PILOT_AERO);
    		person.addSkill(SkillType.S_GUN_AERO);
    		break;
	    case(Person.T_PROTO_PILOT):
	    	break;
	    case(Person.T_BA):
	    	person.addSkill(SkillType.S_GUN_BA);
			person.addSkill(SkillType.S_ANTI_MECH);
			person.addSkill(SkillType.S_SMALL_ARMS);
			break;
	    case(Person.T_INFANTRY):
			person.addSkill(SkillType.S_ANTI_MECH);
			person.addSkill(SkillType.S_SMALL_ARMS);
			break;
	    case(Person.T_MECH_TECH):
	    	person.addSkill(SkillType.S_TECH_MECH);
			break;
	    case(Person.T_MECHANIC):
	    	person.addSkill(SkillType.S_TECH_MECHANIC);
			break;
	    case(Person.T_AERO_TECH):
	    	person.addSkill(SkillType.S_TECH_AERO);
			break;
	    case(Person.T_BA_TECH):
	    	person.addSkill(SkillType.S_TECH_BA);
			break;
	    case(Person.T_ASTECH):
	    	person.addSkill(SkillType.S_ASTECH,0,0);
			break;
	    case(Person.T_DOCTOR):
	    	person.addSkill(SkillType.S_DOCTOR);
			break;
	    case(Person.T_MEDIC):
	    	person.addSkill(SkillType.S_MEDTECH,0,0);
			break;
	    case(Person.T_ADMIN_COM):
	    case(Person.T_ADMIN_LOG):
	    case(Person.T_ADMIN_TRA):
	    case(Person.T_ADMIN_HR):
	    	person.addSkill(SkillType.S_ADMIN);
			break;
		}
		person.setRankSystem(ranks);
		return person;
	}
	
	public Ranks getRanks() {
		return ranks;
	}
	
	public void setRankSystem(int system) {
		getRanks().setRankSystem(system);
		for(Person p : getPersonnel()) {
			p.setRank(0);
		}
		//resetAllPilotNames();
	}
	
	public ArrayList<Force> getAllForces() {
		ArrayList<Force> allForces = new ArrayList<Force>();
		for(int x : forceIds.keySet()) {
			allForces.add(forceIds.get(x));
		}
		return allForces;
	}
	
	public Finances getFinances() {
		return finances;
	}
	
	public ArrayList<Part> getPartsNeedingServiceFor(int uid) {
		Unit u = getUnit(uid);
		if(u != null) {
			if(u.isSalvage() || !u.isRepairable()) {
				return u.getSalvageableParts();
			} else {
				return u.getPartsNeedingFixing();
			}
		}
		return new ArrayList<Part>();
	}
	
	public ArrayList<IAcquisitionWork> getAcquisitionsForUnit(int uid) {
		Unit u = getUnit(uid);
		if(u != null) {
			return u.getPartsNeeded();
		}
		return new ArrayList<IAcquisitionWork>();
	}
	
	/**
	 * Use an A* algorithm to find the best path between two planets
	 * For right now, we are just going to minimze the number of jumps
	 * but we could extend this to take advantage of recharge information 
	 * or other variables as well
	 * Based on
	 * http://www.policyalmanac.org/games/aStarTutorial.htm
	 * @param startKey
	 * @param endKey
	 * @return
	 */
	public JumpPath calculateJumpPath(String startKey, String endKey) {
		
		if(startKey.equals(endKey)) {
			JumpPath jpath = new JumpPath();
			jpath.addPlanet(getPlanet(startKey));
			return jpath;
		}
		
		String current = startKey;
		ArrayList<String> closed = new ArrayList<String>();
		ArrayList<String> open = new ArrayList<String>();
		boolean found = false;
		int jumps = 0;
		
		Planet end = Planets.getInstance().getPlanets().get(endKey);
		
		//we are going to through and set up some hashes that will make our work easier
		//hash of parent key
		Hashtable<String,String> parent = new Hashtable<String,String>();
		//hash of H for each planet which will not change
		Hashtable<String,Double> scoreH = new Hashtable<String,Double>();
		//hash of G for each planet which might change
		Hashtable<String,Integer> scoreG = new Hashtable<String,Integer>();

		for(String key : Planets.getInstance().getPlanets().keySet()) {
			scoreH.put(key, end.getDistanceTo(Planets.getInstance().getPlanets().get(key)));
		}
		scoreG.put(current, 0);
		closed.add(current);
		
		while(!found && jumps < 10000) {
			jumps++;
			int currentG = scoreG.get(current) + 1;
			ArrayList<String> neighborKeys = getAllReachablePlanetsFrom(Planets.getInstance().getPlanets().get(current));
			for(String neighborKey : neighborKeys) {
				if(closed.contains(neighborKey)) {
					continue;
				}
				else if (open.contains(neighborKey)) {
					//is the current G better than the existing G
					if(currentG < scoreG.get(neighborKey)) {
						//then change G and parent
						scoreG.put(neighborKey, currentG);
						parent.put(neighborKey, current);
					}
				} else {
					//put the current G for this one in memory
					scoreG.put(neighborKey, currentG);
					//put the parent in memory
					parent.put(neighborKey, current);
					open.add(neighborKey);
				}
			}
			String bestMatch = null;
			double bestF = Integer.MAX_VALUE;
			for(String possible : open) {
				//calculate F
				double currentF = scoreG.get(possible) + scoreH.get(possible);
				if(currentF < bestF) {
					bestMatch = possible;
					bestF = currentF;
				}
			}
			current = bestMatch;
			closed.add(current);
			open.remove(current);
			if(current.equals(endKey)) {
				found = true;
			}
		}
		//now we just need to back up from the last current by parents until we hit null
		ArrayList<Planet> path = new ArrayList<Planet>();
		String nextKey = current;
		while(null != nextKey) {
			path.add(Planets.getInstance().getPlanets().get(nextKey));
			//MekHQApp.logMessage(nextKey);
			nextKey = parent.get(nextKey);
			
		}
		//now reverse the direaction
		JumpPath finalPath = new JumpPath();
		for(int i = (path.size() -1);  i >= 0; i--) {
			finalPath.addPlanet(path.get(i));
		}
		return finalPath;
	}
	
	public ArrayList<String> getAllReachablePlanetsFrom(Planet planet) {
		ArrayList<String> neighbors = new ArrayList<String>();
		for(String key : Planets.getInstance().getPlanets().keySet()) {
			Planet p = Planets.getInstance().getPlanets().get(key);
			if(planet.getDistanceTo(p) <= 30.0) {
				neighbors.add(key);
			}
		}
		return neighbors;
	}
	
	/**
	 * Right now this is going to be a total hack because the rules from FM Merc
	 * would be a nightmare to calculate and I want to get something up and running
	 * so we can do contracts. There are two components to figure - the costs of leasing
	 * dropships for excess units and the cost of leasing jumpships based on the number
	 * of dropships. Right now, we are just going to calculate average costs per unit
	 * and then make some guesses about total dropship collar needs.
	 * 
	 * Hopefully, StellarOps will clarify all of this.
	 */
	public long calculateCostPerJump(boolean excludeOwnTransports) {
		//first we need to get the total number of units by type
		int nMech = 0;
		int nVee = 0;
		int nAero = 0;
		int nBA = 0;
		int nMechInf = 0;
		int nMotorInf = 0;
		int nFootInf = 0;
		
		double cargoSpace = 0.0;
		
		int nDropship = 0;
		int nCollars = 0;
		
		for(Unit u : getUnits()) {
			Entity en = u.getEntity();
			if(en instanceof Dropship && excludeOwnTransports) {
				nDropship++;
				//decrement total needs by what this dropship can offer
				for(Bay bay : en.getTransportBays()) {
					if(bay instanceof MechBay) {
						nMech -= bay.getCapacity();
					}
					else if(bay instanceof LightVehicleBay) {
						nVee -= bay.getCapacity();
					}
					else if(bay instanceof HeavyVehicleBay) {
						nVee -= bay.getCapacity();
					}
					else if(bay instanceof ASFBay || bay instanceof SmallCraftBay) {
						nAero -= bay.getCapacity();
					}
					else if(bay instanceof BattleArmorBay) {
						nBA -= bay.getCapacity() * 4;
					}
					else if(bay instanceof InfantryBay) {
						nMechInf -= bay.getCapacity() * 28;
					}
					else if(bay instanceof CargoBay) {
						cargoSpace += bay.getCapacity();
					}
				}
			}
			else if(en instanceof Jumpship && excludeOwnTransports) {
				nCollars += ((Jumpship)en).getDocks();
			}
			else if(en instanceof Mech) {
				nMech++;
			}
			else if(en instanceof Tank) {
				nVee++;
			}
			else if(en instanceof Aero && !(en instanceof Dropship) && !(en instanceof Jumpship)) {
				nAero++;
			}
			else if(en instanceof BattleArmor) {
				nBA += 4;
			}
			else if(en instanceof Infantry) {
				if(en.getMovementMode() == EntityMovementMode.INF_LEG || en.getMovementMode() == EntityMovementMode.INF_LEG) {
					nFootInf += ((Infantry)en).getSquadN() * ((Infantry)en).getSquadSize();
				}
				else if(en.getMovementMode() == EntityMovementMode.INF_MOTORIZED) {
					nMotorInf += ((Infantry)en).getSquadN() * ((Infantry)en).getSquadSize();
				}
				else {
					nMechInf += ((Infantry)en).getSquadN() * ((Infantry)en).getSquadSize();
				}
			}
			//if we havent got you yet then you fly free (yay!)
		}
		
		if(nMech < 0) {
			nMech = 0;
		}
		if(nVee < 0) {
			nVee = 0;
		}
		if(nAero < 0) {
			nAero = 0;
		}
		if(nBA < 0) {
			nBA = 0;
		}
		//now lets resort the infantry a bit
		if(nMechInf < 0) {
			nMotorInf += nMechInf;
			nMechInf = 0;
		}
		if(nMotorInf < 0) {
			nFootInf += nMotorInf;
		}
		if(nFootInf < 0) {
			nFootInf = 0;
		}
		
		//Ok, now the costs per unit - this is the dropship fee. I am loosely
		//basing this on Field Manual Mercs, although I think the costs are f'ed up
		long dropshipCost = 0;
		dropshipCost += nMech  * 10000;
		dropshipCost += nAero  * 15000;
		dropshipCost += nVee   *  3000;
		dropshipCost += nBA    *   250;
		dropshipCost += nMechInf * 100;
		dropshipCost += nMotorInf * 50;
		dropshipCost += nFootInf  * 10;
		
		//ok, now how many dropship collars do we need for these units? base this on 
		//some of the canonical designs
		int collarsNeeded = 0;
		//for mechs assume a union or smaller
		collarsNeeded += (int)Math.ceil(nMech / 12.0);
		//for aeros, they may ride for free on the union, if not assume a leopard cv
		collarsNeeded += (int)Math.ceil(Math.max(0,nAero-collarsNeeded*2) / 6.0);
		//for vees, assume a Triumph
		collarsNeeded += (int)Math.ceil(nVee / 53.0);
		//for now I am going to let infantry and BA tag along because of cargo space rules
		
		//add the existing dropships 
		collarsNeeded += nDropship;
		
		//now factor in owned jumpships
		collarsNeeded = Math.max(0, collarsNeeded - nCollars);
		
		return dropshipCost + collarsNeeded*50000;
	}
	
	/*
	public void resetAllPilotNames() {
		for(Person p : getPersonnel()) {
			if(p instanceof PilotPerson) {
				((PilotPerson)p).resetPilotName();
			}
		}
	}
	*/
	
	public void personUpdated(Person p) {
		Unit u = getUnit(p.getUnitId());
		if(null != u) {
			u.resetPilotAndEntity();
			
		}
	}
	
	public TargetRoll getTargetFor(IPartWork partWork, Person tech) {		
		Skill skill = tech.getSkillForWorkingOn(partWork.getUnit());
		int modePenalty = Modes.getModeExperienceReduction(partWork.getMode());
        if(null != partWork.getUnit() && partWork.getUnit().isDeployed()) {
            return new TargetRoll(TargetRoll.IMPOSSIBLE, "This unit is currently deployed!");
        } 
        if(null == skill) {
        	return new TargetRoll(TargetRoll.IMPOSSIBLE, "Assigned tech does not have the right skills");
        }
        if(partWork.getSkillMin() > (skill.getExperienceLevel()-modePenalty)) {
            return new TargetRoll(TargetRoll.IMPOSSIBLE, "Task is beyond this tech's skill level");
        }
        if(!partWork.needsFixing() && !partWork.isSalvaging()) {
            return new TargetRoll(TargetRoll.IMPOSSIBLE, "Task is not needed.");
        }
        if(partWork instanceof MissingPart && null == ((MissingPart)partWork).findReplacement()) {
            return new TargetRoll(TargetRoll.IMPOSSIBLE, "Part not available.");
        }
        if(tech.getMinutesLeft() <= 0 && (!isOvertimeAllowed() || tech.getOvertimeLeft() <= 0)) {
     	   return new TargetRoll(TargetRoll.IMPOSSIBLE, "No time left.");
        }
        String notFixable = partWork.checkFixable();
        if(null != notFixable) {
     	   return new TargetRoll(TargetRoll.IMPOSSIBLE, notFixable);
        }
        //this is ugly, if the mode penalty drops you to green, you drop two levels instead of two
        int value = skill.getFinalSkillValue() + modePenalty;
        if(SkillType.EXP_GREEN == (skill.getExperienceLevel()-modePenalty)) {
        	value++;
        }
        TargetRoll target = new TargetRoll(value, SkillType.getExperienceLevelName(skill.getExperienceLevel()-modePenalty));
        if(target.getValue() == TargetRoll.IMPOSSIBLE) {
            return target;
        }

        target.append(partWork.getAllMods());
       
        boolean isOvertime = false;
        if(isOvertimeAllowed() && (tech.isTaskOvertime(partWork) || partWork.hasWorkedOvertime())) {
            target.addModifier(3, "overtime");
            isOvertime = true;
        }
             
        int minutes = partWork.getTimeLeft();
        if(minutes > tech.getMinutesLeft()) {
        	if(isOvertimeAllowed()) {
        		if(minutes > (tech.getMinutesLeft() + tech.getOvertimeLeft())) {
        			minutes = tech.getMinutesLeft() + tech.getOvertimeLeft();
        		} 
        	} else {
        		minutes = tech.getMinutesLeft();
        	}
        }      
        int helpers = getAvailableAstechs(minutes, isOvertime);
        int helpMod = getShorthandedMod(helpers, false);
        if(partWork.getShorthandedMod() > helpMod) {
        	helpMod = partWork.getShorthandedMod();
        }
        if(helpMod > 0) {
        	target.addModifier(helpMod, "shorthanded");
        }
        //we may have just gone overtime with our helpers
        if(!isOvertime && astechPoolMinutes < (minutes * helpers)) {
        	target.addModifier(3, "overtime astechs");
        }
        return target;
    }
	
	public TargetRoll getTargetForAcquisition(IAcquisitionWork acquisition, Person person) {
		Skill skill = person.getSkillForWorkingOn(acquisition.getUnit());	
		if(acquisition.hasCheckedToday()) {
			return new TargetRoll(TargetRoll.IMPOSSIBLE, "Already checked for this part in this cycle");
		}	
		TargetRoll target = new TargetRoll(skill.getFinalSkillValue(), SkillType.getExperienceLevelName(skill.getExperienceLevel()));//person.getTarget(Modes.MODE_NORMAL);
		target.append(acquisition.getAllAcquisitionMods());
		return target;
	}
	
	public void resetAstechMinutes() {
		astechPoolMinutes = 480 * getNumberAstechs();
		astechPoolOvertime = 240 * getNumberAstechs();
	}
	
	public int getAstechPoolMinutes() {
		return astechPoolMinutes;
	}
	
	public int getAstechPoolOvertime() {
		return astechPoolOvertime;
	}
	
	public int getAstechPool() {
		return astechPool;
	}
	
	public void increaseAstechPool(int i) {
		astechPool += i;
		astechPoolMinutes += (480 * i);
		astechPoolOvertime += (240 * i);
	}
	
	public void decreaseAstechPool(int i) {
		astechPool = Math.max(0, astechPool - i);
		//always assume that we fire the ones who have not yet worked
		astechPoolMinutes = Math.max(0, astechPoolMinutes - 480*i);
		astechPoolOvertime = Math.max(0, astechPoolOvertime - 240*i);
	}
	
	public int getNumberAstechs() {
		int astechs = astechPool;
		for(Person p : personnel) {
			if(p.getType() == Person.T_ASTECH && p.isActive() && !p.isDeployed(this)) {
				astechs++;
			}
		}
		return astechs;
	}
	
	public int getAvailableAstechs(int minutes, boolean alreadyOvertime) {
        int availableHelp = (int)Math.floor(((double)astechPoolMinutes) / minutes);   
	        if(isOvertimeAllowed() && availableHelp < 6) {
	        //if we are less than fully staffed, then determine whether 
	        //we should dip into overtime or just continue as short-staffed
	        int shortMod = getShorthandedMod(availableHelp, false);
	        int remainingMinutes = astechPoolMinutes - availableHelp * minutes;
	        int extraHelp = (remainingMinutes + astechPoolOvertime)/minutes;
	        int helpNeeded = 6 - availableHelp;
	        if(alreadyOvertime && shortMod > 0) {
	        	//then add whatever we can
	        	availableHelp += extraHelp;
	        }
	        else if(shortMod > 3) {
	        	//only dip in if we can bring ourselves up to full
	        	if(extraHelp >= helpNeeded) {
	        		availableHelp = 6;
	        	}
	        }
        }
        if(availableHelp > 6) {
        	availableHelp = 6;
        }  
        if(availableHelp > getNumberAstechs()) {
        	return getNumberAstechs();
        }
        return availableHelp;
	}
	
	public int getShorthandedMod(int availableHelp, boolean medicalStaff) {
		if(medicalStaff) {
			availableHelp += 2;
		}
        int helpMod = 0;
        if(availableHelp == 0) {
        	helpMod = 4;
        }
        else if(availableHelp == 1) {
        	helpMod = 3;
        }
        else if(availableHelp < 4) {
        	helpMod = 2;
        }
        else if(availableHelp < 6) {
        	helpMod = 1;
        }
        return helpMod;
	}
}
