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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import megamek.common.Entity;

import megamek.common.Game;
import mekhq.campaign.work.WorkItem;

/**
 *
 * @author Taharqa
 * The main campaign class, keeps track of teams and units
 */
public class Campaign implements Serializable {

    //we have three things to track: (1) teams, (2) units, (3) repair tasks
    //we will use the same basic system (borrowed from MegaMek) for tracking all three
    
    private ArrayList<SupportTeam> teams = new ArrayList<SupportTeam>();
    private Hashtable<Integer, SupportTeam> teamIds = new Hashtable<Integer, SupportTeam>();
    private ArrayList<Unit> units = new ArrayList<Unit>();
    private Hashtable<Integer, Unit> unitIds = new Hashtable<Integer, Unit>();
    private ArrayList<WorkItem> tasks = new ArrayList<WorkItem>();
    private Hashtable<Integer, WorkItem> taskIds = new Hashtable<Integer, WorkItem>();
    
    private int lastTeamId;
    private int lastUnitId;
    private int lastTaskId;
    
    private ArrayList<String> currentReport = new ArrayList<String>();
    
    //I need to put a basic game object in campaign so that I can
    //asssign it to the entities, otherwise some entity methods may get NPE
    //if they try to call up game options
    Game game = new Game();
    
    
    public Campaign() {
        
    }
    
    public void addTeam(SupportTeam t) {
        int id = lastTeamId + 1;
        t.setId(id);
        teams.add(t);
        teamIds.put(new Integer(id), t);
        lastTeamId = id;
    }
    
    public ArrayList<SupportTeam> getTeams() {
        return teams;
    }
    
    public SupportTeam getTeam(int id) {
        return teamIds.get(new Integer(id));
    }
    
     
    public void addUnit(Entity en) {
        //TODO: check for duplicate display names
        int id = lastUnitId + 1;
        en.setId(id);
        en.setGame(game);
        Unit unit = new Unit(en);
        units.add(unit);
        unitIds.put(new Integer(id), unit);
        lastUnitId = id;
        //collect all the work items outstanding on this unit and add them to the workitem vector
        unit.runDiagnostic(this);
    }
    
    public ArrayList<Unit> getUnits() {
        return units;
    }
    
    public ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<Entity>();
        for(Unit unit : getUnits()) {
            entities.add(unit.getEntity());
        }
        return entities;
    }
    
    public Unit getUnit(int id) {
        return unitIds.get(new Integer(id));
    }
    
    public void addWork(WorkItem task) {
        //TODO: check for duplicate display names
        int id = lastTaskId + 1;
        task.setId(id);
        tasks.add(task);
        taskIds.put(new Integer(id), task);
        lastTaskId = id;
    }
    
    public ArrayList<WorkItem> getTasks() {
        return tasks;
    }
    
    public WorkItem getTask(int id) {
        return taskIds.get(new Integer(id));
    }
    
    public ArrayList<String> getCurrentReport() {
        return currentReport;
    }
    
    public ArrayList<WorkItem> getTasksForUnit(int unitId) {
        ArrayList<WorkItem> newTasks = new ArrayList<WorkItem>();
        for(WorkItem task : getTasks()) {
            if(task.getUnitId() == unitId) {
                newTasks.add(task);
            }
        }
        return newTasks;
    }
    
    public String getUnitTaskDesc(int unitId) {
        ArrayList<WorkItem> unitTasks = getTasksForUnit(unitId);
        int minutes = 0;
        int total = 0;
        int assigned = 0;
        for(WorkItem task : unitTasks) {
            total++;
            minutes += task.getTime();
            if(!task.isUnassigned()) {
                assigned++;
            } 
        }
        if(total == 0) {
            return "";
        }
        return " (" + assigned + "/" + total + "; " + minutes + " minutes)";
    }
    
     public ArrayList<WorkItem> getTasksForTeam(int teamId) {
        ArrayList<WorkItem> newTasks = new ArrayList<WorkItem>();
        for(WorkItem task : getTasks()) {
            if(task.getTeamId() == teamId) {
                newTasks.add(task);
            }
        }
        return newTasks;
    }
     
     public void assignTask(int teamId, int taskId) {
         taskIds.get(new Integer(taskId)).assignTeam(teamId);
     }
    
    public void processDay() {
        currentReport = new ArrayList<String>();
        //cycle through teams and tell them to get to work
        for(SupportTeam team : getTeams()) {
            currentReport.addAll(team.doAssignments());
        }
        
        //ok now cycle through all tasks and only keep the ones that 
        //have not been completed
        ArrayList<WorkItem> newTasks = new ArrayList<WorkItem>();
        for(WorkItem task : getTasks()) {
            if(!task.isCompleted()) {
                newTasks.add(task);
            }
        }
        this.tasks = newTasks;
    }
    
    public void clearUnits() {
        this.units = new ArrayList<Unit>();
        this.unitIds = new Hashtable<Integer, Unit>();
        this.lastUnitId = 0;
        //also clear tasks, because you can't have tasks without entities
        this.tasks = new ArrayList<WorkItem>();
        this.taskIds = new Hashtable<Integer, WorkItem>();
        this.lastTaskId = 0;
        
    }
    
}
