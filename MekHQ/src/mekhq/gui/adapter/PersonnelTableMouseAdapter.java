/*
 * Copyright (c) 2019-2021 - The MegaMek Team. All Rights Reserved.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.gui.adapter;

import megamek.client.generator.RandomCallsignGenerator;
import megamek.client.generator.RandomNameGenerator;
import megamek.client.ui.dialogs.PortraitChooserDialog;
import megamek.common.*;
import megamek.common.options.IOption;
import megamek.common.options.OptionsConstants;
import megamek.common.options.PilotOptions;
import megamek.common.util.EncodeControl;
import megamek.common.util.sorter.NaturalOrderComparator;
import mekhq.MekHQ;
import mekhq.Utilities;
import mekhq.campaign.Kill;
import mekhq.campaign.event.PersonChangedEvent;
import mekhq.campaign.event.PersonLogEvent;
import mekhq.campaign.finances.Money;
import mekhq.campaign.finances.enums.TransactionType;
import mekhq.campaign.log.LogEntry;
import mekhq.campaign.log.PersonalLogger;
import mekhq.campaign.personnel.*;
import mekhq.campaign.personnel.enums.*;
import mekhq.campaign.personnel.generator.SingleSpecialAbilityGenerator;
import mekhq.campaign.personnel.ranks.Rank;
import mekhq.campaign.personnel.ranks.RankSystem;
import mekhq.campaign.personnel.ranks.RankValidator;
import mekhq.campaign.personnel.ranks.Ranks;
import mekhq.campaign.unit.HangarSorter;
import mekhq.campaign.unit.Unit;
import mekhq.campaign.universe.Faction;
import mekhq.campaign.universe.Planet;
import mekhq.gui.CampaignGUI;
import mekhq.gui.PersonnelTab;
import mekhq.gui.dialog.*;
import mekhq.gui.displayWrappers.RankDisplay;
import mekhq.gui.model.PersonnelTableModel;
import mekhq.gui.utilities.JMenuHelpers;
import mekhq.gui.utilities.MultiLineTooltip;
import mekhq.gui.utilities.StaticChecks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonnelTableMouseAdapter extends JPopupMenuAdapter {
    //region Variable Declarations
    private static final String CMD_RANKSYSTEM = "RANKSYSTEM";
    private static final String CMD_RANK = "RANK";
    private static final String CMD_MANEI_DOMINI_RANK = "MD_RANK";
    private static final String CMD_MANEI_DOMINI_CLASS = "MD_CLASS";
    private static final String CMD_PRIMARY_ROLE = "PROLE";
    private static final String CMD_SECONDARY_ROLE = "SROLE";
    private static final String CMD_PRIMARY_DESIGNATOR = "DESIG_PRI";
    private static final String CMD_SECONDARY_DESIGNATOR = "DESIG_SEC";
    private static final String CMD_REMOVE_UNIT = "REMOVE_UNIT";
    private static final String CMD_ADD_PILOT = "ADD_PILOT";
    private static final String CMD_ADD_SOLDIER = "ADD_SOLDIER";
    private static final String CMD_ADD_DRIVER = "ADD_DRIVER";
    private static final String CMD_ADD_VESSEL_PILOT = "ADD_VESSEL_PILOT";
    private static final String CMD_ADD_GUNNER = "ADD_GUNNER";
    private static final String CMD_ADD_CREW = "ADD_CREW";
    private static final String CMD_ADD_NAVIGATOR = "ADD_NAV";
    private static final String CMD_ADD_TECH_OFFICER = "ADD_TECH_OFFICER";
    private static final String CMD_ADD_AWARD = "ADD_AWARD";
    private static final String CMD_RMV_AWARD = "RMV_AWARD";

    private static final String CMD_EDIT_SALARY = "SALARY";
    private static final String CMD_EDIT_INJURIES = "EDIT_INJURIES";
    private static final String CMD_REMOVE_INJURY = "REMOVE_INJURY";
    private static final String CMD_CLEAR_INJURIES = "CLEAR_INJURIES";
    private static final String CMD_CALLSIGN = "CALLSIGN";
    private static final String CMD_COMMANDER = "COMMANDER";
    private static final String CMD_TRYING_TO_CONCEIVE = "TRYING_TO_CONCEIVE";
    private static final String CMD_TRYING_TO_MARRY = "TRYING_TO_MARRY";
    private static final String CMD_FOUNDER = "FOUNDER";
    private static final String CMD_EDIT_PERSONNEL_LOG = "LOG";
    private static final String CMD_ADD_LOG_ENTRY = "ADD_PERSONNEL_LOG_SINGLE";
    private static final String CMD_EDIT_MISSIONS_LOG = "MISSIONS_LOG";
    private static final String CMD_ADD_MISSION_ENTRY = "ADD_MISSION_ENTRY";
    private static final String CMD_EDIT_KILL_LOG = "KILL_LOG";
    private static final String CMD_ADD_KILL = "ADD_KILL";
    private static final String CMD_BUY_EDGE = "EDGE_BUY";
    private static final String CMD_SET_EDGE = "EDGE_SET";
    private static final String CMD_SET_XP = "XP_SET";
    private static final String CMD_ADD_1_XP = "XP_ADD_1";
    private static final String CMD_ADD_XP = "XP_ADD";
    private static final String CMD_EDIT_BIOGRAPHY = "BIOGRAPHY";
    private static final String CMD_EDIT_PORTRAIT = "PORTRAIT";
    private static final String CMD_EDIT_HITS = "EDIT_HITS";
    private static final String CMD_EDIT = "EDIT";
    private static final String CMD_SACK = "SACK";
    private static final String CMD_REMOVE = "REMOVE";
    private static final String CMD_EDGE_TRIGGER = "EDGE";
    private static final String CMD_CHANGE_PRISONER_STATUS = "PRISONER_STATUS";
    private static final String CMD_CHANGE_STATUS = "STATUS";
    private static final String CMD_ACQUIRE_SPECIALIST = "SPECIALIST";
    private static final String CMD_ACQUIRE_WEAPON_SPECIALIST = "WSPECIALIST";
    private static final String CMD_ACQUIRE_RANGEMASTER = "RANGEMASTER";
    private static final String CMD_ACQUIRE_HUMANTRO = "HUMANTRO";
    private static final String CMD_ACQUIRE_ABILITY = "ABILITY";
    private static final String CMD_ACQUIRE_CUSTOM_CHOICE = "CUSTOM_CHOICE";
    private static final String CMD_IMPROVE = "IMPROVE";
    private static final String CMD_ADD_SPOUSE = "SPOUSE";
    private static final String CMD_REMOVE_SPOUSE = "REMOVE_SPOUSE";
    private static final String CMD_ADD_PREGNANCY = "ADD_PREGNANCY";
    private static final String CMD_REMOVE_PREGNANCY = "PREGNANCY_SPOUSE";
    private static final String CMD_ADD_TECH = "ADD_TECH";

    private static final String CMD_IMPRISON = "IMPRISON";
    private static final String CMD_FREE = "FREE";
    private static final String CMD_RECRUIT = "RECRUIT";
    private static final String CMD_RANSOM = "RANSOM";

    // MechWarrior Edge Options
    private static final String OPT_EDGE_MASC_FAILURE = "edge_when_masc_fails";
    private static final String OPT_EDGE_EXPLOSION = "edge_when_explosion";
    private static final String OPT_EDGE_KO = "edge_when_ko";
    private static final String OPT_EDGE_TAC = "edge_when_tac";
    private static final String OPT_EDGE_HEADHIT = "edge_when_headhit";

    // Aero Edge Options
    private static final String OPT_EDGE_WHEN_AERO_ALT_LOSS= "edge_when_aero_alt_loss";
    private static final String OPT_EDGE_WHEN_AERO_EXPLOSION= "edge_when_aero_explosion";
    private static final String OPT_EDGE_WHEN_AERO_KO= "edge_when_aero_ko";
    private static final String OPT_EDGE_WHEN_AERO_LUCKY_CRIT= "edge_when_aero_lucky_crit";
    private static final String OPT_EDGE_WHEN_AERO_NUKE_CRIT= "edge_when_aero_nuke_crit";
    private static final String OPT_EDGE_WHEN_AERO_UNIT_CARGO_LOST= "edge_when_aero_unit_cargo_lost";

    //region Randomization Menu
    private static final String CMD_RANDOM_NAME = "RANDOM_NAME";
    private static final String CMD_RANDOM_BLOODNAME = "RANDOM_BLOODNAME";
    private static final String CMD_RANDOM_CALLSIGN = "RANDOM_CALLSIGN";
    private static final String CMD_RANDOM_PORTRAIT = "RANDOM_PORTRAIT";
    private static final String CMD_RANDOM_ORIGIN = "RANDOM_ORIGIN";
    private static final String CMD_RANDOM_ORIGIN_FACTION = "RANDOM_ORIGIN_FACTION";
    private static final String CMD_RANDOM_ORIGIN_PLANET = "RANDOM_ORIGIN_PLANET";
    //endregion Randomization Menu

    private static final String SEPARATOR = "@";
    private static final String TRUE = String.valueOf(true);
    private static final String FALSE = String.valueOf(false);

    private final CampaignGUI gui;
    private final JTable personnelTable;
    private final PersonnelTableModel personnelModel;

    private final ResourceBundle resourceMap = ResourceBundle.getBundle("mekhq.resources.PersonnelTableMouseAdapter", new EncodeControl());
    //endregion Variable Declarations

    protected PersonnelTableMouseAdapter(CampaignGUI gui, JTable personnelTable,
                                         PersonnelTableModel personnelModel) {
        this.gui = gui;
        this.personnelTable = personnelTable;
        this.personnelModel = personnelModel;
    }

    public static void connect(CampaignGUI gui, JTable personnelTable,
            PersonnelTableModel personnelModel, JSplitPane splitPersonnel) {
        new PersonnelTableMouseAdapter(gui, personnelTable, personnelModel) {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getButton() == MouseEvent.BUTTON1) && (e.getClickCount() == 2)) {
                    int width = splitPersonnel.getSize().width;
                    int location = splitPersonnel.getDividerLocation();
                    int size = splitPersonnel.getDividerSize();
                    if ((width - location + size) < PersonnelTab.PERSONNEL_VIEW_WIDTH) {
                        // expand
                        splitPersonnel.resetToPreferredSizes();
                    } else {
                        // collapse
                        splitPersonnel.setDividerLocation(1.0);
                    }
                }
            }
        }.connect(personnelTable);
    }

    private String makeCommand(String ... parts) {
        return Utilities.combineString(Arrays.asList(parts), SEPARATOR);
    }

    @Override
    public void actionPerformed(ActionEvent action) {
        int row = personnelTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Person selectedPerson = personnelModel.getPerson(personnelTable.convertRowIndexToModel(row));
        int[] rows = personnelTable.getSelectedRows();
        Person[] people = new Person[rows.length];
        for (int i = 0; i < rows.length; i++) {
            people[i] = personnelModel.getPerson(personnelTable.convertRowIndexToModel(rows[i]));
        }

        String[] data = action.getActionCommand().split(SEPARATOR, -1);

        switch (data[0]) {
            case CMD_RANKSYSTEM: {
                final RankSystem rankSystem = Ranks.getRankSystemFromCode(data[1]);
                final RankValidator rankValidator = new RankValidator();
                for (final Person person : people) {
                    person.setRankSystem(rankValidator, rankSystem);
                }
                break;
            }
            case CMD_RANK: {
                try {
                    final int rank = Integer.parseInt(data[1]);
                    final int level = (data.length > 2) ? Integer.parseInt(data[2]) : 0;
                    for (final Person person : people) {
                        person.changeRank(gui.getCampaign(), rank, level, true);
                    }
                } catch (Exception e) {
                    MekHQ.getLogger().error(e);
                }
                break;
            }
            case CMD_MANEI_DOMINI_CLASS: {
                try {
                    final ManeiDominiClass mdClass = ManeiDominiClass.valueOf(data[1]);
                    for (final Person person : people) {
                        person.setManeiDominiClass(mdClass);
                    }
                } catch (Exception e) {
                    MekHQ.getLogger().error("Failed to assign Manei Domini Class", e);
                }
                break;
            }
            case CMD_MANEI_DOMINI_RANK: {
                final ManeiDominiRank maneiDominiRank = ManeiDominiRank.valueOf(data[1]);
                for (final Person person : people) {
                    person.setManeiDominiRank(maneiDominiRank);
                }
                break;
            }
            case CMD_PRIMARY_DESIGNATOR: {
                try {
                    ROMDesignation romDesignation = ROMDesignation.valueOf(data[1]);
                    for (Person person : people) {
                        person.setPrimaryDesignator(romDesignation);
                    }
                } catch (Exception e) {
                    MekHQ.getLogger().error("Failed to assign ROM designator", e);
                }
                break;
            }
            case CMD_SECONDARY_DESIGNATOR: {
                try {
                    ROMDesignation romDesignation = ROMDesignation.valueOf(data[1]);
                    for (Person person : people) {
                        person.setSecondaryDesignator(romDesignation);
                    }
                } catch (Exception e) {
                    MekHQ.getLogger().error("Failed to assign ROM secondary designator", e);
                }
                break;
            }
            case CMD_PRIMARY_ROLE: {
                PersonnelRole role = PersonnelRole.valueOf(data[1]);
                for (final Person person : people) {
                    person.setPrimaryRole(role);
                    gui.getCampaign().personUpdated(person);
                    if (gui.getCampaign().getCampaignOptions().usePortraitForRole(role)
                            && gui.getCampaign().getCampaignOptions().getAssignPortraitOnRoleChange()
                            && person.getPortrait().hasDefaultFilename()) {
                        gui.getCampaign().assignRandomPortraitFor(person);
                    }
                }
                break;
            }
            case CMD_SECONDARY_ROLE: {
                PersonnelRole role = PersonnelRole.valueOf(data[1]);
                for (final Person person : people) {
                    person.setSecondaryRole(role);
                    gui.getCampaign().personUpdated(person);
                }
                break;
            }
            case CMD_REMOVE_UNIT: {
                for (Person person : people) {
                    Unit u = person.getUnit();
                    if (null != u) {
                        u.remove(person, true);
                        u.resetEngineer();
                        u.runDiagnostic(false);
                    }
                    // check for tech unit assignments
                    if (!person.getTechUnits().isEmpty()) {
                        for (Unit unitWeTech : new ArrayList<>(person.getTechUnits())) {
                            unitWeTech.remove(person, true);
                            unitWeTech.resetEngineer();
                            unitWeTech.runDiagnostic(false);
                        }
                        /*
                         * Incase there's still some assignments for this tech,
                         * clear them out. This can happen if the target unit
                         * above is null. The tech will still have the pointer
                         * but to a null unit and it will never go away
                         * otherwise.
                         */
                        person.clearTechUnits();
                    }
                }
                break;
            }
            case CMD_ADD_PILOT: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                Unit oldUnit = selectedPerson.getUnit();
                boolean useTransfers = false;
                boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                if (null != oldUnit) {
                    oldUnit.remove(selectedPerson, transferLog);
                    useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                }
                if (null != u) {
                    u.addPilotOrSoldier(selectedPerson, useTransfers);
                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_SOLDIER: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeMoreGunners()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.addPilotOrSoldier(p, useTransfers);
                        }
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_DRIVER: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                Unit oldUnit = selectedPerson.getUnit();
                boolean useTransfers = false;
                boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                if (null != oldUnit) {
                    oldUnit.remove(selectedPerson, transferLog);
                    useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                }
                if (null != u) {
                    u.addDriver(selectedPerson, useTransfers);
                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_VESSEL_PILOT: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeMoreDrivers()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.addDriver(p, useTransfers);
                        }
                    }
                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_GUNNER: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeMoreGunners()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.addGunner(p, useTransfers);
                        }
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_CREW: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeMoreVesselCrew()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.addVesselCrew(p, useTransfers);
                        }
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_NAVIGATOR: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeNavigator()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.setNavigator(p, useTransfers);
                        }
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_TECH_OFFICER: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    for (Person p : people) {
                        if (u.canTakeTechOfficer()) {
                            Unit oldUnit = p.getUnit();
                            boolean useTransfers = false;
                            boolean transferLog = !gui.getCampaign().getCampaignOptions().useTransfers();
                            if (null != oldUnit) {
                                oldUnit.remove(p, transferLog);
                                useTransfers = gui.getCampaign().getCampaignOptions().useTransfers();
                            }
                            u.setTechOfficer(p, useTransfers);
                        }
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_TECH: {
                UUID selected = UUID.fromString(data[1]);
                Unit u = gui.getCampaign().getUnit(selected);
                if (null != u) {
                    if (u.canTakeTech()) {
                        u.setTech(selectedPerson);
                    }

                    u.resetPilotAndEntity();
                    u.runDiagnostic(false);
                }
                break;
            }
            case CMD_ADD_PREGNANCY: {
                if (selectedPerson.getGender().isFemale()) {
                    selectedPerson.addPregnancy(gui.getCampaign());
                    MekHQ.triggerEvent(new PersonChangedEvent(selectedPerson));
                }
                break;
            }
            case CMD_REMOVE_PREGNANCY: {
                if (selectedPerson.isPregnant()) {
                    selectedPerson.removePregnancy();
                    MekHQ.triggerEvent(new PersonChangedEvent(selectedPerson));
                }
                break;
            }
            case CMD_REMOVE_SPOUSE: {
                for (Person person : people) {
                    if (person.getGenealogy().hasSpouse()) {
                        Divorce.valueOf(data[1]).divorce(person, gui.getCampaign());
                    }
                }
                break;
            }
            case CMD_ADD_SPOUSE: {
                Person spouse = gui.getCampaign().getPerson(UUID.fromString(data[1]));
                Marriage.valueOf(data[2]).marry(gui.getCampaign(), selectedPerson, spouse);
                break;
            }
            case CMD_ADD_AWARD: {
                for (Person person : people) {
                    person.getAwardController().addAndLogAward(gui.getCampaign(), data[1], data[2],
                            gui.getCampaign().getLocalDate());
                }
                break;
            }
            case CMD_RMV_AWARD: {
                for (Person person : people) {
                    try {
                        if (person.getAwardController().hasAward(data[1], data[2])) {
                            person.getAwardController().removeAward(data[1], data[2],
                                    (data.length > 3)
                                            ? MekHQ.getMekHQOptions().parseDisplayFormattedDate(data[3])
                                            : null,
                                    gui.getCampaign().getLocalDate());
                        }
                    } catch (Exception e) {
                        MekHQ.getLogger().error("Could not remove award.", e);
                    }
                }
                break;
            }
            case CMD_IMPROVE: {
                String type = data[1];
                int cost = Integer.parseInt(data[2]);
                int oldExpLevel = selectedPerson.getExperienceLevel(false);
                selectedPerson.improveSkill(type);
                selectedPerson.spendXP(cost);

                PersonalLogger.improvedSkill(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), selectedPerson.getSkill(type).getType().getName(),
                        selectedPerson.getSkill(type).toString());
                gui.getCampaign().addReport(String.format(resourceMap.getString("improved.format"),
                        selectedPerson.getHyperlinkedName(), type));
                if (gui.getCampaign().getCampaignOptions().getUseAtB()
                        && gui.getCampaign().getCampaignOptions().useAbilities()) {
                    if (selectedPerson.getPrimaryRole().isCombat()
                            && (selectedPerson.getExperienceLevel(false) > oldExpLevel)
                            && (oldExpLevel >= SkillType.EXP_REGULAR)) {
                        SingleSpecialAbilityGenerator spaGenerator = new SingleSpecialAbilityGenerator();
                        String spa = spaGenerator.rollSPA(selectedPerson);
                        if (spa == null) {
                            if (gui.getCampaign().getCampaignOptions().useEdge()) {
                                selectedPerson.changeEdge(1);
                                selectedPerson.changeCurrentEdge(1);
                                PersonalLogger.gainedEdge(gui.getCampaign(), selectedPerson,
                                        gui.getCampaign().getLocalDate());
                                gui.getCampaign().addReport(String.format(resourceMap.getString("gainedEdge.format"),
                                        selectedPerson.getHyperlinkedName()));
                            }
                        } else {
                            PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                                    gui.getCampaign().getLocalDate(), spa);
                            gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                                    selectedPerson.getHyperlinkedName(), spa));
                        }
                    }
                }
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_ABILITY: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        selected, true);
                selectedPerson.spendXP(cost);
                final String displayName = SpecialAbility.getDisplayName(selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_WEAPON_SPECIALIST: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        OptionsConstants.GUNNERY_WEAPON_SPECIALIST, selected);
                selectedPerson.spendXP(cost);
                final String displayName = String.format("%s %s",
                        SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_WEAPON_SPECIALIST), selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_SPECIALIST: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        OptionsConstants.GUNNERY_SPECIALIST, selected);
                selectedPerson.spendXP(cost);
                final String displayName = String.format("%s %s",
                        SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_SPECIALIST), selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_RANGEMASTER: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        OptionsConstants.GUNNERY_RANGE_MASTER, selected);
                selectedPerson.spendXP(cost);
                final String displayName = String.format("%s %s",
                        SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_RANGE_MASTER), selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_HUMANTRO: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        OptionsConstants.MISC_HUMAN_TRO, selected);
                selectedPerson.spendXP(cost);
                final String displayName = String.format("%s %s",
                        SpecialAbility.getDisplayName(OptionsConstants.MISC_HUMAN_TRO), selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("gained.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_ACQUIRE_CUSTOM_CHOICE: {
                String selected = data[1];
                int cost = Integer.parseInt(data[2]);
                String ability = data[3];
                selectedPerson.getOptions().acquireAbility(PilotOptions.LVL3_ADVANTAGES,
                        ability, selected);
                selectedPerson.spendXP(cost);
                final String displayName = String.format("%s %s",
                        SpecialAbility.getDisplayName(ability), selected);
                PersonalLogger.gainedSPA(gui.getCampaign(), selectedPerson,
                        gui.getCampaign().getLocalDate(), displayName);
                gui.getCampaign().addReport(String.format(resourceMap.getString("spaGainedChoices.format"),
                        selectedPerson.getHyperlinkedName(), displayName));
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_CHANGE_STATUS: {
                PersonnelStatus status = PersonnelStatus.valueOf(data[1]);
                for (Person person : people) {
                    if (status.isActive() || (JOptionPane.showConfirmDialog(null,
                            String.format(resourceMap.getString("confirmRetireQ.format"), person.getFullTitle()),
                            status.toString(), JOptionPane.YES_NO_OPTION) == 0)) {
                        person.changeStatus(gui.getCampaign(), status);
                    }
                }
                break;
            }
            case CMD_CHANGE_PRISONER_STATUS: {
                try {
                    PrisonerStatus status = PrisonerStatus.valueOf(data[1]);
                    for (Person person : people) {
                        if (person.getPrisonerStatus() != status) {
                            person.setPrisonerStatus(status);
                        }
                    }
                } catch (Exception e) {
                    MekHQ.getLogger().error("Unknown PrisonerStatus Option. No changes will be made.", e);
                }
                break;
            }
            case CMD_IMPRISON: {
                for (Person person : people) {
                    if (!person.getPrisonerStatus().isPrisoner()) {
                        person.setPrisonerStatus(PrisonerStatus.PRISONER);
                    }
                }
                break;
            }
            case CMD_FREE: {
                // TODO: Warn in particular for "freeing" in deep space, leading to Geneva Conventions violation (#1400 adding Crime to MekHQ)
                // TODO: Record the people into some NPC pool, if still alive
                String title = (people.length == 1) ? people[0].getFullTitle()
                        : String.format(resourceMap.getString("numPrisoners.text"), people.length);
                if (0 == JOptionPane.showConfirmDialog(null,
                        String.format(resourceMap.getString("confirmFree.format"), title),
                        resourceMap.getString("freeQ.text"),
                        JOptionPane.YES_NO_OPTION)) {
                    for (Person person : people) {
                        gui.getCampaign().removePerson(person);
                    }
                }
                break;
            }
            case CMD_RECRUIT: {
                for (Person person : people) {
                    if (person.getPrisonerStatus().isWillingToDefect()) {
                        person.setPrisonerStatus(PrisonerStatus.FREE);
                    }
                }
                break;
            }
            case CMD_RANSOM: {
                // ask the user if they want to sell off their prisoners. If yes, then add a daily report entry, add the money and remove them all.
                Money total = Money.zero();
                total = total.plus(Arrays.stream(people).map(Person::getRansomValue).collect(Collectors.toList()));

                if (0 == JOptionPane.showConfirmDialog(
                        null,
                        String.format(resourceMap.getString("ransomQ.format"), people.length, total.toAmountAndSymbolString()),
                        resourceMap.getString("ransom.text"),
                        JOptionPane.YES_NO_OPTION)) {

                    gui.getCampaign().addReport(String.format(resourceMap.getString("ransomReport.format"), people.length, total.toAmountAndSymbolString()));
                    gui.getCampaign().addFunds(TransactionType.RANSOM, total, resourceMap.getString("ransom.text"));
                    for (Person person : people) {
                        gui.getCampaign().removePerson(person, false);
                    }
                }
                break;
            }
            case CMD_EDGE_TRIGGER: {
                String trigger = data[1];
                if (people.length > 1) {
                    boolean status = Boolean.parseBoolean(data[2]);
                    for (Person person : people) {
                        person.setEdgeTrigger(trigger, status);
                        gui.getCampaign().personUpdated(person);
                    }
                } else {
                    selectedPerson.changeEdgeTrigger(trigger);
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_REMOVE: {
                String title = (people.length == 1) ? people[0].getFullTitle()
                        : String.format(resourceMap.getString("numPersonnel.text"), people.length);
                if (0 == JOptionPane.showConfirmDialog(null,
                        String.format(resourceMap.getString("confirmRemove.format"), title),
                        resourceMap.getString("removeQ.text"),
                        JOptionPane.YES_NO_OPTION)) {
                    for (Person person : people) {
                        gui.getCampaign().removePerson(person);
                    }
                }
                break;
            }
            case CMD_SACK: {
                boolean showDialog = false;
                List<Person> toRemove = new ArrayList<>();
                for (Person person : people) {
                    if (gui.getCampaign().getRetirementDefectionTracker().removeFromCampaign(
                            person, false, gui.getCampaign(), null)) {
                        showDialog = true;
                    } else {
                        toRemove.add(person);
                    }
                }
                if (showDialog) {
                    RetirementDefectionDialog rdd = new RetirementDefectionDialog(
                            gui, null, false);
                    rdd.setVisible(true);
                    if (rdd.wasAborted()
                            || !gui.getCampaign().applyRetirement(rdd.totalPayout(),
                            rdd.getUnitAssignments())) {
                        for (Person person : people) {
                            gui.getCampaign().getRetirementDefectionTracker()
                                    .removePayout(person);
                        }
                    } else {
                        for (final Person person : toRemove) {
                            gui.getCampaign().removePerson(person);
                        }
                    }
                } else {
                    String question;
                    if (people.length > 1) {
                        question = resourceMap.getString("confirmRemoveMultiple.text");
                    } else {
                        question = String.format(resourceMap.getString("confirmRemove.format"), people[0].getFullTitle());
                    }
                    if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                            null, question, resourceMap.getString("removeQ.text"),
                            JOptionPane.YES_NO_OPTION)) {
                        for (Person person : people) {
                            gui.getCampaign().removePerson(person);
                        }
                    }
                }
                break;
            }
            case CMD_EDIT: {
                CustomizePersonDialog npd = new CustomizePersonDialog(
                        gui.getFrame(), true, selectedPerson, gui.getCampaign());
                npd.setVisible(true);
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_EDIT_HITS: {
                EditPersonnelHitsDialog ephd = new EditPersonnelHitsDialog(gui.getFrame(), true, selectedPerson);
                ephd.setVisible(true);
                if (0 == selectedPerson.getHits()) {
                    selectedPerson.setDoctorId(null, gui.getCampaign().getCampaignOptions()
                            .getNaturalHealingWaitingPeriod());
                }
                gui.getCampaign().personUpdated(selectedPerson);
                break;
            }
            case CMD_EDIT_PORTRAIT: {
                final PortraitChooserDialog portraitDialog = new PortraitChooserDialog(
                        gui.getFrame(), selectedPerson.getPortrait());
                if (portraitDialog.showDialog().isConfirmed()) {
                    for (Person person : people) {
                        if (!person.getPortrait().equals(portraitDialog.getSelectedItem())) {
                            person.setPortrait(portraitDialog.getSelectedItem());
                            gui.getCampaign().personUpdated(person);
                        }
                    }
                }
                break;
            }
            case CMD_EDIT_BIOGRAPHY: {
                MarkdownEditorDialog tad = new MarkdownEditorDialog(gui.getFrame(), true,
                        resourceMap.getString("editBiography.text"), selectedPerson.getBiography());
                tad.setVisible(true);
                if (tad.wasChanged()) {
                    selectedPerson.setBiography(tad.getText());
                    MekHQ.triggerEvent(new PersonChangedEvent(selectedPerson));
                }
                break;
            }
            case CMD_ADD_1_XP: {
                for (Person person : people) {
                    person.awardXP(gui.getCampaign(), 1);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_ADD_XP: {
                PopupValueChoiceDialog pvcda = new PopupValueChoiceDialog(
                        gui.getFrame(), true, resourceMap.getString("xp.text"), 1, 0);
                pvcda.setVisible(true);

                int ia = pvcda.getValue();
                if (ia <= 0) {
                    // <0 indicates Cancellation
                    // =0 is a No-Op
                    return;
                }

                for (Person person : people) {
                    person.awardXP(gui.getCampaign(), ia);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_SET_XP: {
                PopupValueChoiceDialog pvcd = new PopupValueChoiceDialog(
                        gui.getFrame(), true, resourceMap.getString("xp.text"), selectedPerson.getXP(), 0);
                pvcd.setVisible(true);
                if (pvcd.getValue() < 0) {
                    return;
                }
                int i = pvcd.getValue();
                for (Person person : people) {
                    person.setXP(gui.getCampaign(), i);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_BUY_EDGE: {
                final int cost = gui.getCampaign().getCampaignOptions().getEdgeCost();
                for (Person person : people) {
                    selectedPerson.spendXP(cost);
                    person.changeEdge(1);
                    // Make the new edge point available to support personnel, but don't reset until
                    // the week ends
                    person.changeCurrentEdge(1);
                    PersonalLogger.gainedEdge(gui.getCampaign(), person, gui.getCampaign().getLocalDate());
                    gui.getCampaign().addReport(String.format(resourceMap.getString("gainedEdge.format"), selectedPerson.getHyperlinkedName()));
                    gui.getCampaign().personUpdated(person);
                }
                break;
            }
            case CMD_SET_EDGE: {
                PopupValueChoiceDialog pvcd = new PopupValueChoiceDialog(
                        gui.getFrame(), true, resourceMap.getString("edge.text"), selectedPerson.getEdge(), 0,
                        10);
                pvcd.setVisible(true);
                if (pvcd.getValue() < 0) {
                    return;
                }
                int i = pvcd.getValue();
                for (Person person : people) {
                    person.setEdge(i);
                    //Reset currentEdge for support people
                    person.resetCurrentEdge();
                    PersonalLogger.changedEdge(gui.getCampaign(), person, gui.getCampaign().getLocalDate());
                    gui.getCampaign().personUpdated(person);
                }
                break;
            }
            case CMD_ADD_KILL: {
                AddOrEditKillEntryDialog nkd;
                Unit unit = selectedPerson.getUnit();
                if (people.length > 1) {
                    nkd = new AddOrEditKillEntryDialog(gui.getFrame(), true, null,
                            (unit != null) ? unit.getName() : resourceMap.getString("bareHands.text"),
                            gui.getCampaign().getLocalDate());
                } else {
                    nkd = new AddOrEditKillEntryDialog(gui.getFrame(), true, selectedPerson.getId(),
                            (unit != null) ? unit.getName() : resourceMap.getString("bareHands.text"),
                            gui.getCampaign().getLocalDate());
                }
                nkd.setVisible(true);
                if (nkd.getKill().isPresent()) {
                    Kill kill = nkd.getKill().get();
                    if (people.length > 1) {
                        for (Person person : people) {
                            Kill k = kill.clone();
                            k.setPilotId(person.getId());
                            gui.getCampaign().addKill(k);
                            MekHQ.triggerEvent(new PersonLogEvent(person));
                        }
                    } else {
                        gui.getCampaign().addKill(kill);
                        MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                    }
                }
                break;
            }
            case CMD_EDIT_KILL_LOG: {
                EditKillLogDialog ekld = new EditKillLogDialog(gui.getFrame(), true, gui.getCampaign(), selectedPerson);
                ekld.setVisible(true);
                MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                break;
            }
            case CMD_EDIT_PERSONNEL_LOG: {
                EditPersonnelLogDialog epld = new EditPersonnelLogDialog(gui.getFrame(), true, gui.getCampaign(), selectedPerson);
                epld.setVisible(true);
                MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                break;
            }
            case CMD_ADD_LOG_ENTRY: {
                final AddOrEditPersonnelEntryDialog addPersonnelLogDialog = new AddOrEditPersonnelEntryDialog(
                        gui.getFrame(), null, gui.getCampaign().getLocalDate());
                if (addPersonnelLogDialog.showDialog().isConfirmed()) {
                    for (Person person : people) {
                        person.addLogEntry(addPersonnelLogDialog.getEntry().clone());
                        MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                    }
                }
                break;
            }
            case CMD_EDIT_MISSIONS_LOG: {
                EditMissionLogDialog emld = new EditMissionLogDialog(gui.getFrame(), true, gui.getCampaign(), selectedPerson);
                emld.setVisible(true);
                MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                break;
            }
            case CMD_ADD_MISSION_ENTRY: {
                AddOrEditMissionEntryDialog addMissionDialog = new AddOrEditMissionEntryDialog(
                        gui.getFrame(), true, gui.getCampaign().getLocalDate());
                addMissionDialog.setVisible(true);
                Optional<LogEntry> missionEntry = addMissionDialog.getEntry();
                if (missionEntry.isPresent()) {
                    for (Person person : people) {
                        person.addMissionLogEntry(missionEntry.get().clone());
                        MekHQ.triggerEvent(new PersonLogEvent(selectedPerson));
                    }
                }
                break;
            }
            case CMD_COMMANDER: {
                selectedPerson.setCommander(!selectedPerson.isCommander());
                if (selectedPerson.isCommander()) {
                    for (Person p : gui.getCampaign().getPersonnel()) {
                        if (p.isCommander() && !p.getId().equals(selectedPerson.getId())) {
                            p.setCommander(false);
                            gui.getCampaign().addReport(String.format(resourceMap.getString("removedCommander.format"), p.getHyperlinkedFullTitle()));
                            gui.getCampaign().personUpdated(p);
                        }
                    }
                    gui.getCampaign().addReport(String.format(resourceMap.getString("setAsCommander.format"), selectedPerson.getHyperlinkedFullTitle()));
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_TRYING_TO_MARRY: {
                if (people.length > 1) {
                    boolean status = !people[0].isTryingToMarry();
                    for (Person person : people) {
                        person.setTryingToMarry(status);
                        gui.getCampaign().personUpdated(person);
                    }
                } else {
                    selectedPerson.setTryingToMarry(!selectedPerson.isTryingToMarry());
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_TRYING_TO_CONCEIVE: {
                if (people.length > 1) {
                    boolean status = !people[0].isTryingToConceive();
                    for (Person person : people) {
                        person.setTryingToConceive(status);
                        gui.getCampaign().personUpdated(person);
                    }
                } else {
                    selectedPerson.setTryingToConceive(!selectedPerson.isTryingToConceive());
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_FOUNDER: {
                if (people.length > 1) {
                    boolean status = !people[0].isFounder();
                    for (Person person : people) {
                        person.setFounder(status);
                        gui.getCampaign().personUpdated(person);
                    }
                } else {
                    selectedPerson.setFounder(!selectedPerson.isFounder());
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_CALLSIGN: {
                String s = (String) JOptionPane.showInputDialog(gui.getFrame(),
                        resourceMap.getString("enterNewCallsign.text"), resourceMap.getString("editCallsign.text"),
                        JOptionPane.PLAIN_MESSAGE, null, null,
                        selectedPerson.getCallsign());
                if (null != s) {
                    selectedPerson.setCallsign(s);
                    gui.getCampaign().personUpdated(selectedPerson);
                }
                break;
            }
            case CMD_CLEAR_INJURIES: {
                for (Person person : people) {
                    person.clearInjuries();
                    Unit u = person.getUnit();
                    if (null != u) {
                        u.resetPilotAndEntity();
                    }
                }
                break;
            }
            case CMD_REMOVE_INJURY: {
                String sel = data[1];
                Injury toRemove = null;
                for (Injury i : selectedPerson.getInjuries()) {
                    if (i.getUUID().toString().equals(sel)) {
                        toRemove = i;
                        break;
                    }
                }
                if (toRemove != null) {
                    selectedPerson.removeInjury(toRemove);
                }
                Unit u = selectedPerson.getUnit();
                if (null != u) {
                    u.resetPilotAndEntity();
                }
                break;
            }
            case CMD_EDIT_INJURIES: {
                EditPersonnelInjuriesDialog epid = new EditPersonnelInjuriesDialog(
                        gui.getFrame(), true, gui.getCampaign(), selectedPerson);
                epid.setVisible(true);
                MekHQ.triggerEvent(new PersonChangedEvent(selectedPerson));
                break;
            }
            case CMD_EDIT_SALARY: {
                PopupValueChoiceDialog pcvd = new PopupValueChoiceDialog(
                        gui.getFrame(),
                        true,
                        resourceMap.getString("changeSalary.text"),
                        selectedPerson.getSalary().getAmount().intValue(),
                        -1,
                        100000);
                pcvd.setVisible(true);
                int salary = pcvd.getValue();
                if (salary < -1) {
                    return;
                }
                for (Person person : people) {
                    person.setSalary(Money.of(salary));
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }

            //region Randomization Menu
            case CMD_RANDOM_NAME: {
                for (final Person person : people) {
                    final String[] name = RandomNameGenerator.getInstance().generateGivenNameSurnameSplit(
                            person.getGender(), person.isClanner(), person.getOriginFaction().getShortName());
                    person.setGivenName(name[0]);
                    person.setSurname(name[1]);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_RANDOM_BLOODNAME: {
                final boolean ignoreDice = (data.length > 1) && Boolean.parseBoolean(data[1]);
                for (final Person person : people) {
                    gui.getCampaign().checkBloodnameAdd(person, ignoreDice);
                }
                break;
            }
            case CMD_RANDOM_CALLSIGN: {
                for (final Person person : people) {
                    person.setCallsign(RandomCallsignGenerator.getInstance().generate());
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_RANDOM_PORTRAIT: {
                for (final Person person : people) {
                    gui.getCampaign().assignRandomPortraitFor(person);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_RANDOM_ORIGIN: {
                for (final Person person : people) {
                    gui.getCampaign().assignRandomOriginFor(person);
                    MekHQ.triggerEvent(new PersonChangedEvent(person));
                }
                break;
            }
            case CMD_RANDOM_ORIGIN_FACTION: {
                for (final Person person : people) {
                    final Faction faction = gui.getCampaign().getFactionSelector().selectFaction(gui.getCampaign());
                    if (faction != null) {
                        person.setOriginFaction(faction);
                        MekHQ.triggerEvent(new PersonChangedEvent(person));
                    }
                }
                break;
            }
            case CMD_RANDOM_ORIGIN_PLANET: {
                for (final Person person : people) {
                    final Planet planet = gui.getCampaign().getPlanetSelector().selectPlanet(
                            gui.getCampaign(), person.getOriginFaction());
                    if (planet != null) {
                        person.setOriginPlanet(planet);
                        MekHQ.triggerEvent(new PersonChangedEvent(person));
                    }
                }
                break;
            }
            //endregion Randomization Menu

            default: {
                break;
            }
        }
    }

    private void loadGMToolsForPerson(Person person) {
        GMToolsDialog gmToolsDialog = new GMToolsDialog(gui.getFrame(), gui, person);
        gmToolsDialog.setVisible(true);
        gui.getCampaign().personUpdated(person);
    }

    private Person[] getSelectedPeople() {
        Person[] selected = new Person[personnelTable.getSelectedRowCount()];
        int[] rows = personnelTable.getSelectedRows();
        for (int i = 0; i < rows.length; i++) {
            Person person = personnelModel.getPerson(personnelTable.convertRowIndexToModel(rows[i]));
            selected[i] = person;
        }
        return selected;
    }

    @Override
    protected Optional<JPopupMenu> createPopupMenu() {
        if (personnelTable.getSelectedRowCount() == 0) {
            return Optional.empty();
        }

        JPopupMenu popup = new JPopupMenu();

        int row = personnelTable.getSelectedRow();
        boolean oneSelected = personnelTable.getSelectedRowCount() == 1;
        Person person = personnelModel.getPerson(personnelTable.convertRowIndexToModel(row));
        JMenuItem menuItem;
        JMenu menu;
        JMenu submenu;
        JCheckBoxMenuItem cbMenuItem;
        Person[] selected = getSelectedPeople();

        // lets fill the pop up menu
        if (StaticChecks.areAllEligible(true, selected)) {
            menu = new JMenu(resourceMap.getString("changeRank.text"));
            final Profession initialProfession = Profession.getProfessionFromPersonnelRole(person.getPrimaryRole());
            for (final RankDisplay rankDisplay : RankDisplay.getRankDisplaysForSystem(
                    person.getRankSystem(), initialProfession)) {
                final Rank rank = person.getRankSystem().getRank(rankDisplay.getRankNumeric());
                final Profession profession = initialProfession.getProfession(person.getRankSystem(), rank);
                final int rankLevels = rank.getRankLevels().get(profession);

                if (rankLevels > 1) {
                    submenu = new JMenu(rankDisplay.toString());
                    for (int level = 0; level <= rankLevels; level++) {
                        cbMenuItem = new JCheckBoxMenuItem(rank.getName(profession)
                                + Utilities.getRomanNumeralsFromArabicNumber(level, true));
                        cbMenuItem.setSelected((person.getRankNumeric() == rankDisplay.getRankNumeric())
                                && (person.getRankLevel() == level));
                        cbMenuItem.setActionCommand(makeCommand(CMD_RANK,
                                String.valueOf(rankDisplay.getRankNumeric()), String.valueOf(level)));
                        cbMenuItem.addActionListener(this);
                        submenu.add(cbMenuItem);
                    }
                    JMenuHelpers.addMenuIfNonEmpty(menu, submenu);
                } else {
                    cbMenuItem = new JCheckBoxMenuItem(rankDisplay.toString());
                    cbMenuItem.setSelected(person.getRankNumeric() == rankDisplay.getRankNumeric());
                    cbMenuItem.setActionCommand(makeCommand(CMD_RANK, String.valueOf(rankDisplay.getRankNumeric())));
                    cbMenuItem.addActionListener(this);
                    menu.add(cbMenuItem);
                }
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);
        }

        menu = new JMenu(resourceMap.getString("changeRankSystem.text"));
        final RankSystem campaignRankSystem = gui.getCampaign().getRankSystem();
        // First allow them to revert to the campaign system
        cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("useCampaignRankSystem.text"));
        cbMenuItem.setSelected(campaignRankSystem.equals(person.getRankSystem()));
        cbMenuItem.setActionCommand(makeCommand(CMD_RANKSYSTEM, campaignRankSystem.getCode()));
        cbMenuItem.addActionListener(this);
        menu.add(cbMenuItem);

        final List<RankSystem> rankSystems = new ArrayList<>(Ranks.getRankSystems().values());
        final NaturalOrderComparator naturalOrderComparator = new NaturalOrderComparator();
        rankSystems.sort((r1, r2) -> naturalOrderComparator.compare(r1.toString(), r2.toString()));
        for (final RankSystem rankSystem : rankSystems) {
            if (rankSystem.equals(campaignRankSystem)) {
                continue;
            }
            cbMenuItem = new JCheckBoxMenuItem(rankSystem.toString());
            cbMenuItem.setSelected(rankSystem.equals(person.getRankSystem()));
            cbMenuItem.setActionCommand(makeCommand(CMD_RANKSYSTEM, rankSystem.getCode()));
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);
        }
        JMenuHelpers.addMenuIfNonEmpty(popup, menu);

        if (Stream.of(selected).allMatch(p -> p.getRankSystem().isUseManeiDomini())) {
            // MD Classes
            menu = new JMenu(resourceMap.getString("changeMDClass.text"));
            for (ManeiDominiClass maneiDominiClass : ManeiDominiClass.values()) {
                cbMenuItem = new JCheckBoxMenuItem(maneiDominiClass.toString());
                cbMenuItem.setActionCommand(makeCommand(CMD_MANEI_DOMINI_CLASS, maneiDominiClass.name()));
                cbMenuItem.addActionListener(this);
                if (maneiDominiClass == person.getManeiDominiClass()) {
                    cbMenuItem.setSelected(true);
                }
                menu.add(cbMenuItem);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);

            // MD Ranks
            menu = new JMenu(resourceMap.getString("changeMDRank.text"));
            for (ManeiDominiRank maneiDominiRank : ManeiDominiRank.values()) {
                cbMenuItem = new JCheckBoxMenuItem(maneiDominiRank.toString());
                cbMenuItem.setActionCommand(makeCommand(CMD_MANEI_DOMINI_RANK, maneiDominiRank.name()));
                cbMenuItem.addActionListener(this);
                if (person.getManeiDominiRank() == maneiDominiRank) {
                    cbMenuItem.setSelected(true);
                }
                menu.add(cbMenuItem);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);
        }

        if (Stream.of(selected).allMatch(p -> p.getRankSystem().isUseROMDesignation())) {
            menu = new JMenu(resourceMap.getString("changePrimaryDesignation.text"));
            for (ROMDesignation romDesignation : ROMDesignation.values()) {
                cbMenuItem = new JCheckBoxMenuItem(romDesignation.toString());
                cbMenuItem.setActionCommand(makeCommand(CMD_PRIMARY_DESIGNATOR, romDesignation.name()));
                cbMenuItem.addActionListener(this);
                if (romDesignation == person.getPrimaryDesignator()) {
                    cbMenuItem.setSelected(true);
                }
                menu.add(cbMenuItem);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);

            menu = new JMenu(resourceMap.getString("changeSecondaryDesignation.text"));
            for (ROMDesignation romDesignation : ROMDesignation.values()) {
                cbMenuItem = new JCheckBoxMenuItem(romDesignation.toString());
                cbMenuItem.setActionCommand(makeCommand(CMD_SECONDARY_DESIGNATOR, romDesignation.name()));
                cbMenuItem.addActionListener(this);
                if (romDesignation == person.getSecondaryDesignator()) {
                    cbMenuItem.setSelected(true);
                }
                menu.add(cbMenuItem);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);
        }
        menu = new JMenu(resourceMap.getString("changeStatus.text"));
        for (PersonnelStatus status : PersonnelStatus.values()) {
            cbMenuItem = new JCheckBoxMenuItem(status.toString());
            if (person.getStatus() == status) {
                cbMenuItem.setSelected(true);
            }
            cbMenuItem.setActionCommand(makeCommand(CMD_CHANGE_STATUS, status.name()));
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);
        }
        popup.add(menu);

        if (StaticChecks.areAnyFree(selected)) {
            popup.add(newMenuItem(resourceMap.getString("imprison.text"), CMD_IMPRISON));
        } else {
            // If none are free, then we can put the Free option
            popup.add(newMenuItem(resourceMap.getString("free.text"), CMD_FREE));
        }

        if (gui.getCampaign().getCampaignOptions().useAtBPrisonerRansom()
                && StaticChecks.areAllPrisoners(selected)) {
            popup.add(newMenuItem(resourceMap.getString("ransom.text"), CMD_RANSOM));
        }

        if (StaticChecks.areAnyWillingToDefect(selected)) {
            popup.add(newMenuItem(resourceMap.getString("recruit.text"), CMD_RECRUIT));
        }

        final PersonnelRole[] roles = PersonnelRole.values();
        menu = new JMenu(resourceMap.getString("changePrimaryRole.text"));
        for (final PersonnelRole role : roles) {
            if (person.canPerformRole(role, true)) {
                cbMenuItem = new JCheckBoxMenuItem(role.getName(person.isClanner()));
                cbMenuItem.setActionCommand(makeCommand(CMD_PRIMARY_ROLE, role.name()));
                cbMenuItem.setSelected(person.getPrimaryRole() == role);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }
        }
        JMenuHelpers.addMenuIfNonEmpty(popup, menu);

        menu = new JMenu(resourceMap.getString("changeSecondaryRole.text"));
        for (final PersonnelRole role : roles) {
            if (person.canPerformRole(role, false)) {
                cbMenuItem = new JCheckBoxMenuItem(role.getName(person.isClanner()));
                cbMenuItem.setActionCommand(makeCommand(CMD_SECONDARY_ROLE, role.name()));
                cbMenuItem.setSelected(person.getSecondaryRole() == role);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }
        }
        JMenuHelpers.addMenuIfNonEmpty(popup, menu);

        // change salary
        if (gui.getCampaign().getCampaignOptions().payForSalaries() && StaticChecks.areAllActive(selected)) {
            menuItem = new JMenuItem(resourceMap.getString("setSalary.text"));
            menuItem.setActionCommand(CMD_EDIT_SALARY);
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        if (!person.isDeployed()) {
            // Assign pilot to unit/none
            menu = new JMenu(resourceMap.getString("assignToUnit.text"));
            JMenu pilotMenu = new JMenu(resourceMap.getString("assignAsPilot.text"));
            JMenu pilotUnitTypeMenu = new JMenu();
            JMenu pilotEntityWeightMenu = new JMenu();
            JMenu driverMenu = new JMenu(resourceMap.getString("assignAsDriver.text"));
            JMenu driverUnitTypeMenu = new JMenu();
            JMenu driverEntityWeightMenu = new JMenu();
            JMenu crewMenu = new JMenu(resourceMap.getString("assignAsCrewmember.text"));
            JMenu crewUnitTypeMenu = new JMenu();
            JMenu crewEntityWeightMenu = new JMenu();
            JMenu gunnerMenu = new JMenu(resourceMap.getString("assignAsGunner.text"));
            JMenu gunnerUnitTypeMenu = new JMenu();
            JMenu gunnerEntityWeightMenu = new JMenu();
            JMenu navMenu = new JMenu(resourceMap.getString("assignAsNavigator.text"));
            JMenu navUnitTypeMenu = new JMenu();
            JMenu navEntityWeightMenu = new JMenu();
            JMenu soldierMenu = new JMenu(resourceMap.getString("assignAsSoldier.text"));
            JMenu soldierUnitTypeMenu = new JMenu();
            JMenu soldierEntityWeightMenu = new JMenu();
            JMenu techOfficerMenu = new JMenu(resourceMap.getString("assignAsTechOfficer.text"));
            JMenu techOfficerUnitTypeMenu = new JMenu();
            JMenu techOfficerEntityWeightMenu = new JMenu();
            JMenu consoleCmdrMenu = new JMenu(resourceMap.getString("assignAsConsoleCmdr.text"));
            JMenu consoleCmdrUnitTypeMenu = new JMenu();
            JMenu consoleCmdrEntityWeightMenu = new JMenu();
            JMenu techMenu = new JMenu(resourceMap.getString("assignAsTech.text"));
            JMenu techUnitTypeMenu = new JMenu();
            JMenu techEntityWeightMenu = new JMenu();

            int unitType = -1;
            int weightClass = -1;

            if (oneSelected && person.getStatus().isActive() && person.getPrisonerStatus().isFree()) {
                for (Unit unit : HangarSorter.defaultSorting().getUnits(gui.getCampaign().getHangar())) {
                    if (!unit.isAvailable()) {
                        continue;
                    } else if (unit.getEntity().getUnitType() != unitType) {
                        unitType = unit.getEntity().getUnitType();
                        String unitTypeName = UnitType.getTypeName(unitType);
                        weightClass = unit.getEntity().getWeightClass();
                        String weightClassName = unit.getEntity().getWeightClassName();

                        // Add Weight Menus to Unit Type Menus
                        JMenuHelpers.addMenuIfNonEmpty(pilotUnitTypeMenu, pilotEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverUnitTypeMenu, driverEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewUnitTypeMenu, crewEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerUnitTypeMenu, gunnerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navUnitTypeMenu, navEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierUnitTypeMenu, soldierEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techOfficerUnitTypeMenu, techOfficerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(consoleCmdrUnitTypeMenu, consoleCmdrEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techUnitTypeMenu, techEntityWeightMenu);

                        // Then add the Unit Type Menus to the Role Menus
                        JMenuHelpers.addMenuIfNonEmpty(pilotMenu, pilotUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverMenu, driverUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewMenu, crewUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerMenu, gunnerUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navMenu, navUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierMenu, soldierUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techOfficerMenu, techOfficerUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(consoleCmdrMenu, consoleCmdrUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techMenu, techUnitTypeMenu);

                        // Create new UnitType and EntityWeight Menus
                        pilotUnitTypeMenu = new JMenu(unitTypeName);
                        pilotEntityWeightMenu = new JMenu(weightClassName);
                        driverUnitTypeMenu = new JMenu(unitTypeName);
                        driverEntityWeightMenu = new JMenu(weightClassName);
                        crewUnitTypeMenu = new JMenu(unitTypeName);
                        crewEntityWeightMenu = new JMenu(weightClassName);
                        gunnerUnitTypeMenu = new JMenu(unitTypeName);
                        gunnerEntityWeightMenu = new JMenu(weightClassName);
                        navUnitTypeMenu = new JMenu(unitTypeName);
                        navEntityWeightMenu = new JMenu(weightClassName);
                        soldierUnitTypeMenu = new JMenu(unitTypeName);
                        soldierEntityWeightMenu = new JMenu(weightClassName);
                        techOfficerUnitTypeMenu = new JMenu(unitTypeName);
                        techOfficerEntityWeightMenu = new JMenu(weightClassName);
                        consoleCmdrUnitTypeMenu = new JMenu(unitTypeName);
                        consoleCmdrEntityWeightMenu = new JMenu(weightClassName);
                        techUnitTypeMenu = new JMenu(unitTypeName);
                        techEntityWeightMenu = new JMenu(weightClassName);
                    } else if (unit.getEntity().getWeightClass() != weightClass) {
                        weightClass = unit.getEntity().getWeightClass();
                        String weightClassName = unit.getEntity().getWeightClassName();

                        JMenuHelpers.addMenuIfNonEmpty(pilotUnitTypeMenu, pilotEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverUnitTypeMenu, driverEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewUnitTypeMenu, crewEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerUnitTypeMenu, gunnerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navUnitTypeMenu, navEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierUnitTypeMenu, soldierEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techOfficerUnitTypeMenu, techOfficerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(consoleCmdrUnitTypeMenu, consoleCmdrEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(techUnitTypeMenu, techEntityWeightMenu);

                        pilotEntityWeightMenu = new JMenu(weightClassName);
                        driverEntityWeightMenu = new JMenu(weightClassName);
                        crewEntityWeightMenu = new JMenu(weightClassName);
                        gunnerEntityWeightMenu = new JMenu(weightClassName);
                        navEntityWeightMenu = new JMenu(weightClassName);
                        soldierEntityWeightMenu = new JMenu(weightClassName);
                        techOfficerEntityWeightMenu = new JMenu(weightClassName);
                        consoleCmdrEntityWeightMenu = new JMenu(weightClassName);
                        techEntityWeightMenu = new JMenu(weightClassName);
                    }

                    if (unit.usesSoloPilot()) {
                        if (unit.canTakeMoreDrivers() && person.canDrive(unit.getEntity())
                                && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_PILOT, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            pilotEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (unit.usesSoldiers()) {
                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_SOLDIER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            soldierEntityWeightMenu.add(cbMenuItem);
                        }
                    } else {
                        if (unit.canTakeMoreDrivers() && person.canDrive(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_DRIVER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            if (unit.getEntity() instanceof Aero || unit.getEntity() instanceof Mech) {
                                pilotEntityWeightMenu.add(cbMenuItem);
                            } else {
                                driverEntityWeightMenu.add(cbMenuItem);
                            }
                        }

                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_GUNNER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            gunnerEntityWeightMenu.add(cbMenuItem);
                        }

                        if (unit.canTakeMoreVesselCrew()
                                && ((unit.getEntity().isAero() && person.hasSkill(SkillType.S_TECH_VESSEL))
                                || ((unit.getEntity().isSupportVehicle() && person.hasSkill(SkillType.S_TECH_MECHANIC))))) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_CREW, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            crewEntityWeightMenu.add(cbMenuItem);
                        }

                        if (unit.canTakeNavigator() && person.hasSkill(SkillType.S_NAV)) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_NAVIGATOR, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            navEntityWeightMenu.add(cbMenuItem);
                        }

                        if (unit.canTakeTechOfficer()) {
                            //For a vehicle command console we will require the commander to be a driver or a gunner, but not necessarily both
                            if (unit.getEntity() instanceof Tank) {
                                if (person.canDrive(unit.getEntity()) || person.canGun(unit.getEntity())) {
                                    cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                                    cbMenuItem.setSelected(unit.equals(person.getUnit()));
                                    cbMenuItem.setActionCommand(makeCommand(CMD_ADD_TECH_OFFICER, unit.getId().toString()));
                                    cbMenuItem.addActionListener(this);
                                    consoleCmdrEntityWeightMenu.add(cbMenuItem);
                                }
                            } else if (person.canDrive(unit.getEntity()) && person.canGun(unit.getEntity())) {
                                cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                                cbMenuItem.setSelected(unit.equals(person.getUnit()));
                                cbMenuItem.setActionCommand(makeCommand(CMD_ADD_TECH_OFFICER, unit.getId().toString()));
                                cbMenuItem.addActionListener(this);
                                techOfficerEntityWeightMenu.add(cbMenuItem);
                            }
                        }
                    }
                    if (unit.canTakeTech() && person.canTech(unit.getEntity())
                            && (person.getMaintenanceTimeUsing() + unit.getMaintenanceTime() <= 480)) {
                        cbMenuItem = new JCheckBoxMenuItem(String.format(resourceMap.getString("maintenanceTimeDesc.format"),
                                unit.getName(), unit.getMaintenanceTime()));
                        cbMenuItem.setSelected(unit.equals(person.getUnit()));
                        cbMenuItem.setActionCommand(makeCommand(CMD_ADD_TECH, unit.getId().toString()));
                        cbMenuItem.addActionListener(this);
                        techEntityWeightMenu.add(cbMenuItem);
                    }
                }
            } else if (StaticChecks.areAllActive(selected) && StaticChecks.areAllEligible(selected)) {
                for (Unit unit : HangarSorter.defaultSorting().getUnits(gui.getCampaign().getHangar())) {
                    if (!unit.isAvailable()) {
                        continue;
                    } else if (unit.getEntity().getUnitType() != unitType) {
                        unitType = unit.getEntity().getUnitType();
                        String unitTypeName = UnitType.getTypeName(unitType);
                        weightClass = unit.getEntity().getWeightClass();
                        String weightClassName = unit.getEntity().getWeightClassName();

                        // Add Weight Menus to Unit Type Menus
                        JMenuHelpers.addMenuIfNonEmpty(pilotUnitTypeMenu, pilotEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverUnitTypeMenu, driverEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewUnitTypeMenu, crewEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerUnitTypeMenu, gunnerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navUnitTypeMenu, navEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierUnitTypeMenu, soldierEntityWeightMenu);

                        // Then add the Unit Type Menus to the Role Menus
                        JMenuHelpers.addMenuIfNonEmpty(pilotMenu, pilotUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverMenu, driverUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewMenu, crewUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerMenu, gunnerUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navMenu, navUnitTypeMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierMenu, soldierUnitTypeMenu);

                        // Create new UnitType and EntityWeight Menus
                        pilotUnitTypeMenu = new JMenu(unitTypeName);
                        pilotEntityWeightMenu = new JMenu(weightClassName);
                        driverUnitTypeMenu = new JMenu(unitTypeName);
                        driverEntityWeightMenu = new JMenu(weightClassName);
                        crewUnitTypeMenu = new JMenu(unitTypeName);
                        crewEntityWeightMenu = new JMenu(weightClassName);
                        gunnerUnitTypeMenu = new JMenu(unitTypeName);
                        gunnerEntityWeightMenu = new JMenu(weightClassName);
                        navUnitTypeMenu = new JMenu(unitTypeName);
                        navEntityWeightMenu = new JMenu(weightClassName);
                        soldierUnitTypeMenu = new JMenu(unitTypeName);
                        soldierEntityWeightMenu = new JMenu(weightClassName);
                    } else if (unit.getEntity().getWeightClass() != weightClass) {
                        weightClass = unit.getEntity().getWeightClass();
                        String weightClassName = unit.getEntity().getWeightClassName();

                        JMenuHelpers.addMenuIfNonEmpty(pilotUnitTypeMenu, pilotEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(driverUnitTypeMenu, driverEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(crewUnitTypeMenu, crewEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(gunnerUnitTypeMenu, gunnerEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(navUnitTypeMenu, navEntityWeightMenu);
                        JMenuHelpers.addMenuIfNonEmpty(soldierUnitTypeMenu, soldierEntityWeightMenu);

                        pilotEntityWeightMenu = new JMenu(weightClassName);
                        driverEntityWeightMenu = new JMenu(weightClassName);
                        crewEntityWeightMenu = new JMenu(weightClassName);
                        gunnerEntityWeightMenu = new JMenu(weightClassName);
                        navEntityWeightMenu = new JMenu(weightClassName);
                        soldierEntityWeightMenu = new JMenu(weightClassName);
                    }

                    if (StaticChecks.areAllSoldiers(selected)) {
                        if (!unit.isConventionalInfantry()) {
                            continue;
                        }

                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_SOLDIER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            soldierEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllBattleArmor(selected)) {
                        if (!(unit.getEntity() instanceof BattleArmor)) {
                            continue;
                        }
                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_SOLDIER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            soldierEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllVehicleGunners(selected)) {
                        if (!(unit.getEntity() instanceof Tank)) {
                            continue;
                        }
                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_GUNNER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            gunnerEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllVesselGunners(selected)) {
                        if (!(unit.getEntity() instanceof Aero)) {
                            continue;
                        }
                        if (unit.canTakeMoreGunners() && person.canGun(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_GUNNER, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            gunnerEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllVesselCrew(selected)) {
                        if (!(unit.getEntity() instanceof Aero)) {
                            continue;
                        }
                        if (unit.canTakeMoreVesselCrew()
                                && ((unit.getEntity().isAero() && person.hasSkill(SkillType.S_TECH_VESSEL))
                                || ((unit.getEntity().isSupportVehicle() && person.hasSkill(SkillType.S_TECH_MECHANIC))))) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_CREW, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            crewEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllVesselPilots(selected)) {
                        if (!(unit.getEntity() instanceof Aero)) {
                            continue;
                        }
                        if (unit.canTakeMoreDrivers() && person.canDrive(unit.getEntity())) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_VESSEL_PILOT, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            pilotEntityWeightMenu.add(cbMenuItem);
                        }
                    } else if (StaticChecks.areAllVesselNavigators(selected)) {
                        if (!(unit.getEntity() instanceof Aero)) {
                            continue;
                        }
                        if (unit.canTakeNavigator() && person.hasSkill(SkillType.S_NAV)) {
                            cbMenuItem = new JCheckBoxMenuItem(unit.getName());
                            cbMenuItem.setSelected(unit.equals(person.getUnit()));
                            cbMenuItem.setActionCommand(makeCommand(CMD_ADD_NAVIGATOR, unit.getId().toString()));
                            cbMenuItem.addActionListener(this);
                            navEntityWeightMenu.add(cbMenuItem);
                        }
                    }
                }
            }

            // Add the last grouping of entity weight menus to the last grouping of entity menus
            JMenuHelpers.addMenuIfNonEmpty(pilotUnitTypeMenu, pilotEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(driverUnitTypeMenu, driverEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(crewUnitTypeMenu, crewEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(gunnerUnitTypeMenu, gunnerEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(navUnitTypeMenu, navEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(soldierUnitTypeMenu, soldierEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(techOfficerUnitTypeMenu, techOfficerEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(consoleCmdrUnitTypeMenu, consoleCmdrEntityWeightMenu);
            JMenuHelpers.addMenuIfNonEmpty(techUnitTypeMenu, techEntityWeightMenu);

            // then add the last grouping of entity menus to the primary menus
            JMenuHelpers.addMenuIfNonEmpty(pilotMenu, pilotUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(driverMenu, driverUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(crewMenu, crewUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(gunnerMenu, gunnerUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(navMenu, navUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(soldierMenu, soldierUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(techOfficerMenu, techOfficerUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(consoleCmdrMenu, consoleCmdrUnitTypeMenu);
            JMenuHelpers.addMenuIfNonEmpty(techMenu, techUnitTypeMenu);

            // and finally add any non-empty menus to the primary menu
            JMenuHelpers.addMenuIfNonEmpty(menu, pilotMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, driverMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, crewMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, gunnerMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, navMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, soldierMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, techOfficerMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, consoleCmdrMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, techMenu);

            // and we always include the None checkbox
            cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("none.text"));
            cbMenuItem.setActionCommand(makeCommand(CMD_REMOVE_UNIT, "-1"));
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);

            if ((menu.getItemCount() > 1) || (person.getUnit() != null)
                    || !person.getTechUnits().isEmpty()) {
                JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            }
        }

        if (oneSelected && person.getStatus().isActive()) {
            if (gui.getCampaign().getCampaignOptions().useManualMarriages()
                    && person.oldEnoughToMarry(gui.getCampaign()) && !person.getGenealogy().hasSpouse()) {
                menu = new JMenu(resourceMap.getString("chooseSpouse.text"));
                JMenu maleMenu = new JMenu(resourceMap.getString("spouseMenuMale.text"));
                JMenu femaleMenu = new JMenu(resourceMap.getString("spouseMenuFemale.text"));
                JMenu spouseMenu;

                LocalDate today = gui.getCampaign().getLocalDate();

                List<Person> personnel = new ArrayList<>(gui.getCampaign().getPersonnel());
                personnel.sort(Comparator.comparing((Person p) -> p.getAge(today)).thenComparing(Person::getSurname));

                for (Person ps : personnel) {
                    if (person.safeSpouse(ps, gui.getCampaign())) {
                        String pStatus;

                        if (ps.getPrisonerStatus().isBondsman()) {
                            pStatus = String.format(resourceMap.getString("marriageBondsmanDesc.format"),
                                    ps.getFullName(), ps.getAge(today), ps.getRoleDesc());
                        } else if (ps.getPrisonerStatus().isPrisoner()) {
                            pStatus = String.format(resourceMap.getString("marriagePrisonerDesc.format"),
                                    ps.getFullName(), ps.getAge(today), ps.getRoleDesc());
                        } else {
                            pStatus = String.format(resourceMap.getString("marriagePartnerDesc.format"),
                                    ps.getFullName(), ps.getAge(today), ps.getRoleDesc());
                        }

                        spouseMenu = new JMenu(pStatus);

                        for (Marriage style : Marriage.values()) {
                            spouseMenu.add(newMenuItem(style.getDropDownText(),
                                    makeCommand(CMD_ADD_SPOUSE, ps.getId().toString(), style.name())));
                        }

                        if (ps.getGender().isMale()) {
                            maleMenu.add(spouseMenu);
                        } else {
                            femaleMenu.add(spouseMenu);
                        }
                    }
                }

                if (person.getGender().isMale()) {
                    JMenuHelpers.addMenuIfNonEmpty(menu, femaleMenu);
                    JMenuHelpers.addMenuIfNonEmpty(menu, maleMenu);
                } else {
                    JMenuHelpers.addMenuIfNonEmpty(menu, maleMenu);
                    JMenuHelpers.addMenuIfNonEmpty(menu, femaleMenu);
                }

                JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            }

            if (person.getGenealogy().hasSpouse()) {
                menu = new JMenu(resourceMap.getString("removeSpouse.text"));

                for (Divorce divorceType : Divorce.values()) {
                    JMenuItem divorceMenu = new JMenuItem(divorceType.toString());
                    divorceMenu.setActionCommand(makeCommand(CMD_REMOVE_SPOUSE, divorceType.name()));
                    divorceMenu.addActionListener(this);
                    menu.add(divorceMenu);
                }

                JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            }
        }

        //region Awards Menu
        JMenu awardMenu = new JMenu(resourceMap.getString("award.text"));
        List<String> setNames = AwardsFactory.getInstance().getAllSetNames();
        Collections.sort(setNames);
        for (String setName : setNames) {
            JMenu setAwardMenu = new JMenu(setName);

            List<Award> awardsOfSet = AwardsFactory.getInstance().getAllAwardsForSet(setName);
            Collections.sort(awardsOfSet);

            for (Award award : awardsOfSet) {
                if (!award.canBeAwarded(selected)) {
                    continue;
                }

                StringBuilder awardMenuItem = new StringBuilder();
                awardMenuItem.append(String.format("%s", award.getName()));

                if ((award.getXPReward() != 0) || (award.getEdgeReward() != 0)) {
                    awardMenuItem.append(" (");

                    if (award.getXPReward() != 0) {
                        awardMenuItem.append(award.getXPReward()).append(" XP");
                        if (award.getEdgeReward() != 0) {
                            awardMenuItem.append(" & ");
                        }
                    }

                    if (award.getEdgeReward() != 0) {
                        awardMenuItem.append(award.getEdgeReward()).append(" Edge");
                    }

                    awardMenuItem.append(")");
                }

                menuItem = new JMenuItem(awardMenuItem.toString());
                menuItem.setToolTipText(MultiLineTooltip.splitToolTip(award.getDescription()));
                menuItem.setActionCommand(makeCommand(CMD_ADD_AWARD, award.getSet(), award.getName()));
                menuItem.addActionListener(this);
                setAwardMenu.add(menuItem);
            }

            JMenuHelpers.addMenuIfNonEmpty(awardMenu, setAwardMenu);
        }

        if (StaticChecks.doAnyHaveAnAward(selected)) {
            if (awardMenu.getItemCount() > 0) {
                awardMenu.addSeparator();
            }

            JMenu removeAwardMenu = new JMenu(resourceMap.getString("removeAward.text"));

            if (oneSelected) {
                for (Award award : person.getAwardController().getAwards()) {
                    JMenu singleAwardMenu = new JMenu(award.getName());
                    for (String date : award.getFormattedDates()) {
                        JMenuItem specificAwardMenu = new JMenuItem(date);
                        specificAwardMenu.setActionCommand(makeCommand(CMD_RMV_AWARD, award.getSet(), award.getName(), date));
                        specificAwardMenu.addActionListener(this);
                        singleAwardMenu.add(specificAwardMenu);
                    }
                    JMenuHelpers.addMenuIfNonEmpty(removeAwardMenu, singleAwardMenu);
                }
            } else {
                Set<Award> awards = new TreeSet<>((a1, a2) -> {
                    if (a1.getSet().equalsIgnoreCase(a2.getSet())) {
                        return a1.getName().compareToIgnoreCase(a2.getName());
                    } else {
                        return a1.getSet().compareToIgnoreCase(a2.getSet());
                    }
                });
                for (Person p : selected) {
                    awards.addAll(p.getAwardController().getAwards());
                }

                for (Award award : awards) {
                    JMenuItem singleAwardMenu = new JMenuItem(award.getName());
                    singleAwardMenu.setActionCommand(makeCommand(CMD_RMV_AWARD, award.getSet(), award.getName()));
                    singleAwardMenu.addActionListener(this);
                    removeAwardMenu.add(singleAwardMenu);
                }
            }
            JMenuHelpers.addMenuIfNonEmpty(awardMenu, removeAwardMenu);
        }
        popup.add(awardMenu);
        //endregion Awards Menu

        //region Spend XP Menu
        if (oneSelected && person.getStatus().isActive()) {
            menu = new JMenu(resourceMap.getString("spendXP.text"));
            if (gui.getCampaign().getCampaignOptions().useAbilities()) {
                JMenu abMenu = new JMenu(resourceMap.getString("spendOnSpecialAbilities.text"));
                int cost;

                List<SpecialAbility> specialAbilities = new ArrayList<>(SpecialAbility.getAllSpecialAbilities().values());
                specialAbilities.sort(Comparator.comparing(SpecialAbility::getName));

                for (SpecialAbility spa : specialAbilities) {
                    if (null == spa) {
                        continue;
                    }
                    if (!spa.isEligible(person)) {
                        continue;
                    }
                    cost = spa.getCost();
                    String costDesc;
                    if (cost < 0) {
                        costDesc = resourceMap.getString("costNotPossible.text");
                    } else {
                        costDesc = String.format(resourceMap.getString("costValue.format"), cost);
                    }
                    boolean available = (cost >= 0) && (person.getXP() >= cost);
                    if (spa.getName().equals(OptionsConstants.GUNNERY_WEAPON_SPECIALIST)) {
                        Unit u = person.getUnit();
                        if (null != u) {
                            JMenu specialistMenu = new JMenu(SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_WEAPON_SPECIALIST));
                            TreeSet<String> uniqueWeapons = new TreeSet<>();
                            for (int j = 0; j < u.getEntity().getWeaponList().size(); j++) {
                                Mounted m = u.getEntity().getWeaponList().get(j);
                                uniqueWeapons.add(m.getName());
                            }
                            boolean isSpecialist = person.getOptions().booleanOption(spa.getName());
                            for (String name : uniqueWeapons) {
                                if (!(isSpecialist
                                        && person.getOptions().getOption(spa.getName()).stringValue().equals(name))) {
                                    menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), name, costDesc));
                                    menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_WEAPON_SPECIALIST, name, String.valueOf(cost)));
                                    menuItem.addActionListener(this);
                                    menuItem.setEnabled(available);
                                    specialistMenu.add(menuItem);
                                }
                            }
                            if (specialistMenu.getMenuComponentCount() > 0) {
                                abMenu.add(specialistMenu);
                            }
                        }
                    } else if (spa.getName().equals(OptionsConstants.MISC_HUMAN_TRO)) {
                        JMenu specialistMenu = new JMenu(SpecialAbility.getDisplayName(OptionsConstants.MISC_HUMAN_TRO));
                        List<Object> tros = new ArrayList<>();
                        if (person.getOptions().getOption(OptionsConstants.MISC_HUMAN_TRO).booleanValue()) {
                            Object val = person.getOptions().getOption(OptionsConstants.MISC_HUMAN_TRO).getValue();
                            if (val instanceof Collection<?>) {
                                tros.addAll((Collection<?>) val);
                            } else {
                                tros.add(val);
                            }
                        }
                        menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("humantro_mek.text"), costDesc));
                        if (!tros.contains(Crew.HUMANTRO_MECH)) {
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_HUMANTRO, Crew.HUMANTRO_MECH, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (!tros.contains(Crew.HUMANTRO_AERO)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("humantro_aero.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_HUMANTRO, Crew.HUMANTRO_AERO, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (!tros.contains(Crew.HUMANTRO_VEE)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("humantro_vee.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_HUMANTRO, Crew.HUMANTRO_VEE, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (!tros.contains(Crew.HUMANTRO_BA)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("humantro_ba.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_HUMANTRO, Crew.HUMANTRO_BA, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (specialistMenu.getMenuComponentCount() > 0) {
                            abMenu.add(specialistMenu);
                        }
                    } else if (spa.getName().equals(OptionsConstants.GUNNERY_SPECIALIST)
                            && !person.getOptions().booleanOption(OptionsConstants.GUNNERY_SPECIALIST)) {
                        JMenu specialistMenu = new JMenu(SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_SPECIALIST));
                        menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("laserSpecialist.text"), costDesc));
                        menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_SPECIALIST, Crew.SPECIAL_ENERGY, String.valueOf(cost)));
                        menuItem.addActionListener(this);
                        menuItem.setEnabled(available);
                        specialistMenu.add(menuItem);
                        menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("missileSpecialist.text"), costDesc));
                        menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_SPECIALIST, Crew.SPECIAL_MISSILE, String.valueOf(cost)));
                        menuItem.addActionListener(this);
                        menuItem.setEnabled(available);
                        specialistMenu.add(menuItem);
                        menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("ballisticSpecialist.text"), costDesc));
                        menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_SPECIALIST, Crew.SPECIAL_BALLISTIC, String.valueOf(cost)));
                        menuItem.addActionListener(this);
                        menuItem.setEnabled(available);
                        specialistMenu.add(menuItem);
                        abMenu.add(specialistMenu);
                    } else if (spa.getName().equals(OptionsConstants.GUNNERY_RANGE_MASTER)) {
                        JMenu specialistMenu = new JMenu(SpecialAbility.getDisplayName(OptionsConstants.GUNNERY_RANGE_MASTER));
                        List<Object> ranges = new ArrayList<>();
                        if (person.getOptions().getOption(OptionsConstants.GUNNERY_RANGE_MASTER).booleanValue()) {
                            Object val = person.getOptions().getOption(OptionsConstants.GUNNERY_RANGE_MASTER).getValue();
                            if (val instanceof Collection<?>) {
                                ranges.addAll((Collection<?>) val);
                            } else {
                                ranges.add(val);
                            }
                        }
                        if (!ranges.contains(Crew.RANGEMASTER_MEDIUM)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("rangemaster_med.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_RANGEMASTER, Crew.RANGEMASTER_MEDIUM, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (!ranges.contains(Crew.RANGEMASTER_LONG)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("rangemaster_lng.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_RANGEMASTER, Crew.RANGEMASTER_LONG, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (!ranges.contains(Crew.RANGEMASTER_EXTREME)) {
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), resourceMap.getString("rangemaster_xtm.text"), costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_RANGEMASTER, Crew.RANGEMASTER_EXTREME, String.valueOf(cost)));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (specialistMenu.getMenuComponentCount() > 0) {
                            abMenu.add(specialistMenu);
                        }
                    } else if ((person.getOptions().getOption(spa.getName()).getType() == IOption.CHOICE)
                            && !(person.getOptions().getOption(spa.getName()).booleanValue())) {
                        JMenu specialistMenu = new JMenu(spa.getDisplayName());
                        List<String> choices = spa.getChoiceValues();
                        for (String s : choices) {
                            if (s.equalsIgnoreCase("none")) {
                                continue;
                            }
                            menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"),
                                    s, costDesc));
                            menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_CUSTOM_CHOICE,
                                    s, String.valueOf(cost), spa.getName()));
                            menuItem.addActionListener(this);
                            menuItem.setEnabled(available);
                            specialistMenu.add(menuItem);
                        }
                        if (specialistMenu.getMenuComponentCount() > 0) {
                            abMenu.add(specialistMenu);
                        }
                    } else if (!person.getOptions().booleanOption(spa.getName())) {
                        menuItem = new JMenuItem(String.format(resourceMap.getString("abilityDesc.format"), spa.getDisplayName(), costDesc));
                        menuItem.setActionCommand(makeCommand(CMD_ACQUIRE_ABILITY, spa.getName(), String.valueOf(cost)));
                        menuItem.addActionListener(this);
                        menuItem.setEnabled(available);
                        abMenu.add(menuItem);
                    }
                }
                JMenuHelpers.addMenuIfNonEmpty(menu, abMenu);
            }

            JMenu currentMenu = new JMenu(resourceMap.getString("spendOnCurrentSkills.text"));
            JMenu newMenu = new JMenu(resourceMap.getString("spendOnNewSkills.text"));
            for (int i = 0; i < SkillType.getSkillList().length; i++) {
                String type = SkillType.getSkillList()[i];
                int cost = person.hasSkill(type) ? person.getSkill(type).getCostToImprove() : SkillType.getType(type).getCost(0);
                if (cost >= 0) {
                    String desc = String.format(resourceMap.getString("skillDesc.format"), type, cost);
                    menuItem = new JMenuItem(desc);
                    menuItem.setActionCommand(makeCommand(CMD_IMPROVE, type, String.valueOf(cost)));
                    menuItem.addActionListener(this);
                    menuItem.setEnabled(person.getXP() >= cost);
                    if (person.hasSkill(type)) {
                        currentMenu.add(menuItem);
                    } else {
                        newMenu.add(menuItem);
                    }
                }
            }
            JMenuHelpers.addMenuIfNonEmpty(menu, currentMenu);
            JMenuHelpers.addMenuIfNonEmpty(menu, newMenu);

            // Edge Purchasing
            if (gui.getCampaign().getCampaignOptions().useEdge()) {
                JMenu edgeMenu = new JMenu(resourceMap.getString("edge.text"));
                int cost = gui.getCampaign().getCampaignOptions().getEdgeCost();

                if ((cost >= 0) && (person.getXP() >= cost)) {
                    menuItem = new JMenuItem(String.format(resourceMap.getString("spendOnEdge.text"), cost));
                    menuItem.setActionCommand(makeCommand(CMD_BUY_EDGE, String.valueOf(cost)));
                    menuItem.addActionListener(this);
                    edgeMenu.add(menuItem);
                }
                JMenuHelpers.addMenuIfNonEmpty(menu, edgeMenu);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            //endregion Spend XP Menu

            //region Edge Triggers
            if (gui.getCampaign().getCampaignOptions().useEdge()) {
                menu = new JMenu(resourceMap.getString("setEdgeTriggers.text"));

                //Start of Edge reroll options
                //MechWarriors
                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerHeadHits.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_HEADHIT));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_HEADHIT));
                if (!person.getPrimaryRole().isMechWarriorGrouping()) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerTAC.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_TAC));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_TAC));
                if (!person.getPrimaryRole().isMechWarriorGrouping()) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerKO.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_KO));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_KO));
                if (!person.getPrimaryRole().isMechWarriorGrouping()) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerExplosion.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_EXPLOSION));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_EXPLOSION));
                if (!person.getPrimaryRole().isMechWarriorGrouping()) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerMASCFailure.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_MASC_FAILURE));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_MASC_FAILURE));
                if (!person.getPrimaryRole().isMechWarriorGrouping()) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                // Aerospace pilots and gunners
                final boolean isNotAeroOrConventional = !(person.getPrimaryRole().isAerospacePilot()
                        || person.getPrimaryRole().isConventionalAircraftPilot()
                        || person.getPrimaryRole().isLAMPilot());
                final boolean isNotVessel = !person.getPrimaryRole().isVesselCrewmember();
                final boolean isNotAeroConvOrVessel = isNotAeroOrConventional || isNotVessel;

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroAltLoss.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_ALT_LOSS));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_ALT_LOSS));
                if (isNotAeroConvOrVessel) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroExplosion.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_EXPLOSION));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_EXPLOSION));
                if (isNotAeroConvOrVessel) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroKO.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_KO));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_KO));
                if (isNotAeroOrConventional) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroLuckyCrit.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_LUCKY_CRIT));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_LUCKY_CRIT));
                if (isNotAeroConvOrVessel) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroNukeCrit.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_NUKE_CRIT));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_NUKE_CRIT));
                if (isNotVessel) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAeroTrnBayCrit.text"));
                cbMenuItem.setSelected(person.getOptions().booleanOption(OPT_EDGE_WHEN_AERO_UNIT_CARGO_LOST));
                cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_UNIT_CARGO_LOST));
                if (isNotVessel) {
                    cbMenuItem.setForeground(new Color(150, 150, 150));
                }
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);

                // Support Edge
                if (gui.getCampaign().getCampaignOptions().useSupportEdge()) {
                    //Doctors
                    cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerHealCheck.text"));
                    cbMenuItem.setSelected(person.getOptions().booleanOption(PersonnelOptions.EDGE_MEDICAL));
                    cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_MEDICAL));
                    if (!person.getPrimaryRole().isDoctor()) {
                        cbMenuItem.setForeground(new Color(150, 150, 150));
                    }
                    cbMenuItem.addActionListener(this);
                    menu.add(cbMenuItem);

                    //Techs
                    cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerBreakPart.text"));
                    cbMenuItem.setSelected(person.getOptions().booleanOption(PersonnelOptions.EDGE_REPAIR_BREAK_PART));
                    cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_BREAK_PART));
                    if (!person.getPrimaryRole().isTech()) {
                        cbMenuItem.setForeground(new Color(150, 150, 150));
                    }
                    cbMenuItem.addActionListener(this);
                    menu.add(cbMenuItem);

                    cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerFailedRefit.text"));
                    cbMenuItem.setSelected(person.getOptions().booleanOption(PersonnelOptions.EDGE_REPAIR_FAILED_REFIT));
                    cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_FAILED_REFIT));
                    if (!person.getPrimaryRole().isTech()) {
                        cbMenuItem.setForeground(new Color(150, 150, 150));
                    }
                    cbMenuItem.addActionListener(this);
                    menu.add(cbMenuItem);

                    //Admins
                    cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("edgeTriggerAcquireCheck.text"));
                    cbMenuItem.setSelected(person.getOptions().booleanOption(PersonnelOptions.EDGE_ADMIN_ACQUIRE_FAIL));
                    cbMenuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_ADMIN_ACQUIRE_FAIL));
                    if (!person.getPrimaryRole().isAdministrator()) {
                        cbMenuItem.setForeground(new Color(150, 150, 150));
                    }
                    cbMenuItem.addActionListener(this);
                    menu.add(cbMenuItem);
                }
                JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            }
            //endregion Edge Triggers

            menu = new JMenu(resourceMap.getString("specialFlags.text"));
            cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("commander.text"));
            cbMenuItem.setSelected(person.isCommander());
            cbMenuItem.setActionCommand(CMD_COMMANDER);
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);

            cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("tryingToMarry.text"));
            cbMenuItem.setToolTipText(resourceMap.getString("tryingToMarry.toolTipText"));
            cbMenuItem.setSelected(person.isTryingToMarry());
            cbMenuItem.setActionCommand(CMD_TRYING_TO_MARRY);
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);

            if (gui.getCampaign().getCampaignOptions().useProcreation()
                    && person.getGender().isFemale()) {
                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("tryingToConceive.text"));
                cbMenuItem.setToolTipText(resourceMap.getString("tryingToConceive.toolTipText"));
                cbMenuItem.setSelected(person.isTryingToConceive());
                cbMenuItem.setActionCommand(CMD_TRYING_TO_CONCEIVE);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }

            cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("founder.text"));
            cbMenuItem.setSelected(person.isFounder());
            cbMenuItem.setActionCommand(CMD_FOUNDER);
            cbMenuItem.addActionListener(this);
            menu.add(cbMenuItem);
            popup.add(menu);
        } else if (StaticChecks.areAllActive(selected)) {
            if (gui.getCampaign().getCampaignOptions().useEdge()) {
                menu = new JMenu(resourceMap.getString("setEdgeTriggers.text"));
                submenu = new JMenu(resourceMap.getString("on.text"));

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerHeadHits.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_HEADHIT, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerTAC.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_TAC, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerKO.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_KO, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerExplosion.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_EXPLOSION, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerMASCFailure.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_MASC_FAILURE, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroAltLoss.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_ALT_LOSS, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroExplosion.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_EXPLOSION, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroKO.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_KO, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroLuckyCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_LUCKY_CRIT, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroNukeCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_NUKE_CRIT, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroTrnBayCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_UNIT_CARGO_LOST, TRUE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                if (gui.getCampaign().getCampaignOptions().useSupportEdge()) {
                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerHealCheck.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_MEDICAL, TRUE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerBreakPart.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_BREAK_PART, TRUE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerFailedRefit.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_FAILED_REFIT, TRUE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAcquireCheck.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_ADMIN_ACQUIRE_FAIL, TRUE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);
                }
                JMenuHelpers.addMenuIfNonEmpty(menu, submenu);

                submenu = new JMenu(resourceMap.getString("off.text"));

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerHeadHits.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_HEADHIT, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerTAC.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_TAC, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerKO.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_KO, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerExplosion.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_EXPLOSION, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerMASCFailure.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_MASC_FAILURE, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroAltLoss.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_ALT_LOSS, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroExplosion.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_EXPLOSION, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroKO.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_KO, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroLuckyCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_LUCKY_CRIT, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroNukeCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_NUKE_CRIT, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAeroTrnBayCrit.text"));
                menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, OPT_EDGE_WHEN_AERO_UNIT_CARGO_LOST, FALSE));
                menuItem.addActionListener(this);
                submenu.add(menuItem);

                if (gui.getCampaign().getCampaignOptions().useSupportEdge()) {
                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerHealCheck.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_MEDICAL, FALSE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerBreakPart.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_BREAK_PART, FALSE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerFailedRefit.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_REPAIR_FAILED_REFIT, FALSE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);

                    menuItem = new JMenuItem(resourceMap.getString("edgeTriggerAcquireCheck.text"));
                    menuItem.setActionCommand(makeCommand(CMD_EDGE_TRIGGER, PersonnelOptions.EDGE_ADMIN_ACQUIRE_FAIL, FALSE));
                    menuItem.addActionListener(this);
                    submenu.add(menuItem);
                }
                JMenuHelpers.addMenuIfNonEmpty(menu, submenu);
                JMenuHelpers.addMenuIfNonEmpty(popup, menu);
            }

            menu = new JMenu(resourceMap.getString("specialFlags.text"));
            if (StaticChecks.areEitherAllTryingToMarryOrNot(selected)) {
                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("tryingToMarry.text"));
                cbMenuItem.setToolTipText(resourceMap.getString("tryingToMarry.toolTipText"));
                cbMenuItem.setSelected(selected[0].isTryingToMarry());
                cbMenuItem.setActionCommand(CMD_TRYING_TO_MARRY);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }

            if (gui.getCampaign().getCampaignOptions().useProcreation()
                    && StaticChecks.areAllFemale(selected)
                    && StaticChecks.areEitherAllTryingToConceiveOrNot(selected)) {
                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("tryingToConceive.text"));
                cbMenuItem.setToolTipText(resourceMap.getString("tryingToConceive.toolTipText"));
                cbMenuItem.setSelected(selected[0].isTryingToConceive());
                cbMenuItem.setActionCommand(CMD_TRYING_TO_CONCEIVE);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }

            if (StaticChecks.areEitherAllFoundersOrNot(selected)) {
                cbMenuItem = new JCheckBoxMenuItem(resourceMap.getString("founder.text"));
                cbMenuItem.setSelected(person.isFounder());
                cbMenuItem.setActionCommand(CMD_FOUNDER);
                cbMenuItem.addActionListener(this);
                menu.add(cbMenuItem);
            }
            JMenuHelpers.addMenuIfNonEmpty(popup, menu);
        }

        // change portrait
        menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "changePortrait.text" : "bulkAssignSinglePortrait.text"));
        menuItem.setActionCommand(CMD_EDIT_PORTRAIT);
        menuItem.addActionListener(this);
        popup.add(menuItem);

        if (oneSelected) {
            // change Biography
            menuItem = new JMenuItem(resourceMap.getString("changeBiography.text"));
            menuItem.setActionCommand(CMD_EDIT_BIOGRAPHY);
            menuItem.addActionListener(this);
            popup.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString("changeCallsign.text"));
            menuItem.setActionCommand(CMD_CALLSIGN);
            menuItem.addActionListener(this);
            popup.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString("editPersonnelLog.text"));
            menuItem.setActionCommand(CMD_EDIT_PERSONNEL_LOG);
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        menuItem = new JMenuItem(resourceMap.getString("addSingleLogEntry.text"));
        menuItem.setActionCommand(CMD_ADD_LOG_ENTRY);
        menuItem.addActionListener(this);
        popup.add(menuItem);

        if (oneSelected) {
            // Edit mission log
            menuItem = new JMenuItem(resourceMap.getString("editMissionLog.text"));
            menuItem.setActionCommand(CMD_EDIT_MISSIONS_LOG);
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        // Add one item to all personnel mission logs
        menuItem = new JMenuItem(resourceMap.getString("addMissionEntry.text"));
        menuItem.setActionCommand(CMD_ADD_MISSION_ENTRY);
        menuItem.addActionListener(this);
        popup.add(menuItem);

        if (oneSelected) {
            menuItem = new JMenuItem(resourceMap.getString("editKillLog.text"));
            menuItem.setActionCommand(CMD_EDIT_KILL_LOG);
            menuItem.addActionListener(this);
            menuItem.setEnabled(true);
            popup.add(menuItem);
        }

        if (oneSelected || StaticChecks.allHaveSameUnit(selected)) {
            menuItem = new JMenuItem(resourceMap.getString("assignKill.text"));
            menuItem.setActionCommand(CMD_ADD_KILL);
            menuItem.addActionListener(this);
            menuItem.setEnabled(true);
            popup.add(menuItem);
        }

        menuItem = new JMenuItem(resourceMap.getString("exportPersonnel.text"));
        menuItem.addActionListener(gui::miExportPersonActionPerformed);
        menuItem.setEnabled(true);
        popup.add(menuItem);

        if (gui.getCampaign().getCampaignOptions().getUseAtB() && StaticChecks.areAllActive(selected)) {
            menuItem = new JMenuItem(resourceMap.getString("sack.text"));
            menuItem.setActionCommand(CMD_SACK);
            menuItem.addActionListener(this);
            popup.add(menuItem);
        }

        //region Randomization Menu
        // This Menu contains the following options, in the specified order:
        // 1) Random Name
        // 2) Random Bloodname Check
        // 3) Random Bloodname Assignment
        // 4) Random Callsign
        // 5) Random Portrait
        // 6) Random Origin
        // 7) Random Origin Faction
        // 8) Random Origin Planet
        menu = new JMenu(resourceMap.getString("randomizationMenu.text"));

        menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomName.single.text" : "miRandomName.bulk.text"));
        menuItem.setName("miRandomName");
        menuItem.setActionCommand(CMD_RANDOM_NAME);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        if (StaticChecks.areAllClanEligible(selected)) {
            menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomBloodnameCheck.single.text" : "miRandomBloodnameCheck.bulk.text"));
            menuItem.setName("miRandomBloodnameCheck");
            menuItem.setActionCommand(makeCommand(CMD_RANDOM_BLOODNAME, String.valueOf(false)));
            menuItem.addActionListener(this);
            menu.add(menuItem);

            if (gui.getCampaign().isGM()) {
                menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomBloodname.single.text" : "miRandomBloodname.bulk.text"));
                menuItem.setName("miRandomBloodname");
                menuItem.setActionCommand(makeCommand(CMD_RANDOM_BLOODNAME, String.valueOf(true)));
                menuItem.addActionListener(this);
                menu.add(menuItem);
            }
        }

        menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomCallsign.single.text" : "miRandomCallsign.bulk.text"));
        menuItem.setName("miRandomCallsign");
        menuItem.setActionCommand(CMD_RANDOM_CALLSIGN);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomPortrait.single.text" : "miRandomPortrait.bulk.text"));
        menuItem.setName("miRandomPortrait");
        menuItem.setActionCommand(CMD_RANDOM_PORTRAIT);
        menuItem.addActionListener(this);
        menu.add(menuItem);

        if (gui.getCampaign().getCampaignOptions().randomizeOrigin()) {
            menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomOrigin.single.text" : "miRandomOrigin.bulk.text"));
            menuItem.setName("miRandomOrigin");
            menuItem.setActionCommand(CMD_RANDOM_ORIGIN);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomOriginFaction.single.text" : "miRandomOriginFaction.bulk.text"));
            menuItem.setName("miRandomOriginFaction");
            menuItem.setActionCommand(CMD_RANDOM_ORIGIN_FACTION);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString(oneSelected ? "miRandomOriginPlanet.single.text" : "miRandomOriginPlanet.bulk.text"));
            menuItem.setName("miRandomOriginPlanet");
            menuItem.setActionCommand(CMD_RANDOM_ORIGIN_PLANET);
            menuItem.addActionListener(this);
            menu.add(menuItem);
        }

        JMenuHelpers.addMenuIfNonEmpty(popup, menu);
        //endregion Randomization Menu

        //region GM Menu
        if (gui.getCampaign().isGM()) {
            popup.addSeparator();

            menu = new JMenu(resourceMap.getString("gmMode.text"));

            menuItem = new JMenu(resourceMap.getString("changePrisonerStatus.text"));
            menuItem.add(newCheckboxMenu(
                    PrisonerStatus.FREE.toString(),
                    makeCommand(CMD_CHANGE_PRISONER_STATUS, PrisonerStatus.FREE.name()),
                    (person.getPrisonerStatus() == PrisonerStatus.FREE)));
            menuItem.add(newCheckboxMenu(
                    PrisonerStatus.PRISONER.toString(),
                    makeCommand(CMD_CHANGE_PRISONER_STATUS, PrisonerStatus.PRISONER.name()),
                    (person.getPrisonerStatus() == PrisonerStatus.PRISONER)));
            menuItem.add(newCheckboxMenu(
                    PrisonerStatus.PRISONER_DEFECTOR.toString(),
                    makeCommand(CMD_CHANGE_PRISONER_STATUS, PrisonerStatus.PRISONER_DEFECTOR.name()),
                    (person.getPrisonerStatus() == PrisonerStatus.PRISONER_DEFECTOR)));
            menuItem.add(newCheckboxMenu(
                    PrisonerStatus.BONDSMAN.toString(),
                    makeCommand(CMD_CHANGE_PRISONER_STATUS, PrisonerStatus.BONDSMAN.name()),
                    (person.getPrisonerStatus() == PrisonerStatus.BONDSMAN)));
            menu.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString("removePerson.text"));
            menuItem.setActionCommand(CMD_REMOVE);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            if (!gui.getCampaign().getCampaignOptions().useAdvancedMedical()) {
                menuItem = new JMenuItem(resourceMap.getString("editHits.text"));
                menuItem.setActionCommand(CMD_EDIT_HITS);
                menuItem.addActionListener(this);
                menu.add(menuItem);
            }

            menuItem = new JMenuItem(resourceMap.getString("add1XP.text"));
            menuItem.setActionCommand(CMD_ADD_1_XP);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString("addXP.text"));
            menuItem.setActionCommand(CMD_ADD_XP);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            menuItem = new JMenuItem(resourceMap.getString("setXP.text"));
            menuItem.setActionCommand(CMD_SET_XP);
            menuItem.addActionListener(this);
            menu.add(menuItem);

            if (gui.getCampaign().getCampaignOptions().useEdge()) {
                menuItem = new JMenuItem(resourceMap.getString("setEdge.text"));
                menuItem.setActionCommand(CMD_SET_EDGE);
                menuItem.addActionListener(this);
                menu.add(menuItem);
            }

            if (oneSelected) {
                menuItem = new JMenuItem(resourceMap.getString("edit.text"));
                menuItem.setActionCommand(CMD_EDIT);
                menuItem.addActionListener(this);
                menu.add(menuItem);

                menuItem = new JMenuItem(resourceMap.getString("loadGMTools.text"));
                menuItem.addActionListener(evt -> loadGMToolsForPerson(person));
                menu.add(menuItem);
            }
            if (gui.getCampaign().getCampaignOptions().useAdvancedMedical()) {
                menuItem = new JMenuItem(resourceMap.getString("removeAllInjuries.text"));
                menuItem.setActionCommand(CMD_CLEAR_INJURIES);
                menuItem.addActionListener(this);
                menu.add(menuItem);

                if (oneSelected) {
                    for (Injury i : person.getInjuries()) {
                        menuItem = new JMenuItem(String.format(resourceMap.getString("removeInjury.format"), i.getName()));
                        menuItem.setActionCommand(makeCommand(CMD_REMOVE_INJURY, i.getUUID().toString()));
                        menuItem.addActionListener(this);
                        menu.add(menuItem);
                    }

                    menuItem = new JMenuItem(resourceMap.getString("editInjuries.text"));
                    menuItem.setActionCommand(CMD_EDIT_INJURIES);
                    menuItem.addActionListener(this);
                    menu.add(menuItem);
                }
            }

            if (oneSelected) {
                if (person.canProcreate(gui.getCampaign())) {
                    menuItem = new JMenuItem(resourceMap.getString("addPregnancy.text"));
                    menuItem.setActionCommand(CMD_ADD_PREGNANCY);
                    menuItem.addActionListener(this);
                    menu.add(menuItem);
                } else if (person.isPregnant()) {
                    menuItem = new JMenuItem(resourceMap.getString("removePregnancy.text"));
                    menuItem.setActionCommand(CMD_REMOVE_PREGNANCY);
                    menuItem.addActionListener(this);
                    menu.add(menuItem);
                }
            }
            popup.add(menu);
        }
        //endregion GM Menu

        return Optional.of(popup);
    }

    private JMenuItem newMenuItem(String text, String command) {
        JMenuItem result = new JMenuItem(text);
        result.setActionCommand(command);
        result.addActionListener(this);
        return result;
    }

    private JCheckBoxMenuItem newCheckboxMenu(String text, String command, boolean selected) {
        JCheckBoxMenuItem result = new JCheckBoxMenuItem(text);
        result.setSelected(selected);
        result.setActionCommand(command);
        result.addActionListener(this);
        result.setEnabled(true);
        return result;
    }
}
