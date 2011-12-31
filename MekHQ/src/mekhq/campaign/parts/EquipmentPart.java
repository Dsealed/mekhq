/*
 * EquipmentPart.java
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
package mekhq.campaign.parts;

import java.io.PrintWriter;

import megamek.common.AmmoType;
import megamek.common.BipedMech;
import megamek.common.CriticalSlot;
import megamek.common.Engine;
import megamek.common.Entity;
import megamek.common.EquipmentType;
import megamek.common.Mech;
import megamek.common.MiscType;
import megamek.common.Mounted;
import megamek.common.Protomech;
import megamek.common.Tank;
import megamek.common.TechConstants;
import megamek.common.WeaponType;
import megamek.common.weapons.Weapon;
import mekhq.campaign.Campaign;
import mekhq.campaign.Era;
import mekhq.campaign.MekHqXmlUtil;
import mekhq.campaign.Unit;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jay Lawson <jaylawson39 at yahoo.com>
 */
public class EquipmentPart extends Part {
	private static final long serialVersionUID = 2892728320891712304L;

	//crap equipmenttype is not serialized!
    protected transient EquipmentType type;
    protected String typeName;
	protected int equipmentNum = -1;
	protected double equipTonnage;
	protected int engineRating;

    public EquipmentType getType() {
        return type;
    }
    
    public int getEquipmentNum() {
    	return equipmentNum;
    }
    
    public void setEquipmentNum(int n) {
    	this.equipmentNum = n;
    }
    
    public EquipmentPart() {
    	this(0, null, -1, null);
    }
    
    public EquipmentPart(int tonnage, EquipmentType et, int equipNum, Campaign c) {
        super(tonnage, c);
        this.type =et;
        if(null != type) {
        	this.name = type.getName();
        	this.typeName = type.getInternalName();
        }
        this.equipmentNum = equipNum;
        try {
        	equipTonnage = type.getTonnage(null);
        } catch(NullPointerException ex) {
        	System.out.println("Found a null entity while calculating tonnage for " + name);
        }
    }
    
    @Override
    public void setUnit(Unit u) {
    	super.setUnit(u);
    	if(null != unit) {
    		equipTonnage = type.getTonnage(unit.getEntity());
    		if(null != unit.getEntity().getEngine()) {
    			engineRating = unit.getEntity().getEngine().getRating();
    		}
    	}
    }
    
    public void setEquipTonnage(double ton) {
    	equipTonnage = ton;
    }
    
    public void setEngineRating(int rating) {
    	this.engineRating = rating;
    }

    public EquipmentPart clone() {
    	return new EquipmentPart(getUnitTonnage(), type, equipmentNum, campaign);
    }
    
    @Override
    public double getTonnage() {
        return equipTonnage;
    }
    
    /**
     * Restores the equipment from the name
     */
    public void restore() {
        if (typeName == null) {
        	typeName = type.getName();
        } else {
            type = EquipmentType.get(typeName);
        }

        if (type == null) {
            System.err
            .println("Mounted.restore: could not restore equipment type \""
                    + typeName + "\"");
        }
    }

    @Override
    public boolean isSamePartTypeAndStatus (Part part) {
    	if(needsFixing() || part.needsFixing()) {
    		return false;
    	}
        return part instanceof EquipmentPart
        		&& getType().equals(((EquipmentPart)part).getType())
        		&& getTonnage() == part.getTonnage();
    }

    @Override
    public int getPartType() {
        if (getType() instanceof Weapon)
            return PART_TYPE_WEAPON;
        else if (getType() instanceof AmmoType)
            return PART_TYPE_AMMO;
        else
            return PART_TYPE_EQUIPMENT_PART;
    }

    @Override
    public int getTechLevel() {
        if (getType().getTechLevel() < 0 || getType().getTechLevel() >= TechConstants.SIZE)
            return TechConstants.T_IS_TW_NON_BOX;
        else
            return getType().getTechLevel();
    }

	@Override
	public void writeToXml(PrintWriter pw1, int indent, int id) {
		writeToXmlBegin(pw1, indent, id);		
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<equipmentNum>"
				+equipmentNum
				+"</equipmentNum>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<typeName>"
				+type.getInternalName()
				+"</typeName>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<equipTonnage>"
				+equipTonnage
				+"</equipTonnage>");
		pw1.println(MekHqXmlUtil.indentStr(indent+1)
				+"<engineRating>"
				+engineRating
				+"</engineRating>");
		writeToXmlEnd(pw1, indent, id);
	}

	@Override
	protected void loadFieldsFromXmlNode(Node wn) {
		NodeList nl = wn.getChildNodes();
		
		for (int x=0; x<nl.getLength(); x++) {
			Node wn2 = nl.item(x);
			if (wn2.getNodeName().equalsIgnoreCase("equipmentNum")) {
				equipmentNum = Integer.parseInt(wn2.getTextContent());
			}
			else if (wn2.getNodeName().equalsIgnoreCase("typeName")) {
				typeName = wn2.getTextContent();
			}
			else if (wn2.getNodeName().equalsIgnoreCase("equipTonnage")) {
				equipTonnage = Integer.parseInt(wn2.getTextContent());
			}
			else if (wn2.getNodeName().equalsIgnoreCase("engineRating")) {
				engineRating = Integer.parseInt(wn2.getTextContent());
			}
		}
		restore();
	}

	@Override
	public int getAvailability(int era) {		
		return type.getAvailability(Era.convertEra(era));
	}

	@Override
	public int getTechRating() {
		return type.getTechRating();
	}

	@Override
	public void fix() {
		super.fix();
		if(null != unit) {
			Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted) {
				mounted.setHit(false);
		        mounted.setDestroyed(false);
		        unit.repairSystem(CriticalSlot.TYPE_EQUIPMENT, unit.getEntity().getEquipmentNum(mounted));
			}
		}
	}

	@Override
	public Part getMissingPart() {
		return new MissingEquipmentPart(getUnitTonnage(), type, equipmentNum, campaign, equipTonnage, engineRating);
	}

	@Override
	public void remove(boolean salvage) {
		if(null != unit) {
			Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted) {
				mounted.setHit(true);
		        mounted.setDestroyed(true);
		        unit.destroySystem(CriticalSlot.TYPE_EQUIPMENT, unit.getEntity().getEquipmentNum(mounted));	
			}
	        if(!salvage) {
				campaign.removePart(this);
			}
	        unit.removePart(this);
	        Part missing = getMissingPart();
			campaign.addPart(missing);
			unit.addPart(missing);
		}
		setUnit(null);
		equipmentNum = -1;
	}

	@Override
	public void updateConditionFromEntity() {
		if(null != unit) {
			Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted) {
				if(!mounted.isRepairable()) {
					remove(false);
					return;
				} else if(mounted.isDestroyed()) {
					//TODO: calculate actual hits
					hits = 1;
				} else {
					hits = 0;
				}
			}
			if(hits == 0) {
				time = 0;
				difficulty = 0;
			} else if(hits == 1) {
				time = 100;
				difficulty = -3;
			} else if(hits == 2) {
				time = 150;
				difficulty = -2;
			} else if(hits == 3) {
				time = 200;
				difficulty = 0;
			} else if(hits > 3) {
				time = 250;
				difficulty = 2;
			}
			if(isSalvaging()) {
				this.time = 120;
				this.difficulty = 0;
			}
		}
	}

	@Override
	public boolean needsFixing() {
		return hits > 0;
	}
	

    @Override
    public String getDetails() {
    	if(null != unit) {
			Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted && mounted.getLocation() != -1) {
				return unit.getEntity().getLocationName(mounted.getLocation()) + ", " + super.getDetails();
			}
    	}
    	return super.getDetails();
    }
    
    public int getLocation() {
    	if(null != unit) {
    		Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted) {
				return mounted.getLocation();
			}
    	}
    	return -1;
    }

	@Override
	public void updateConditionFromPart() {
		if(null != unit) {
			Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
			if(null != mounted) {
				if(hits >= 1) {
					mounted.setDestroyed(true);
					mounted.setHit(true);
					mounted.setRepairable(true);
			        unit.destroySystem(CriticalSlot.TYPE_EQUIPMENT, unit.getEntity().getEquipmentNum(mounted));	
				} else {
					mounted.setHit(false);
			        mounted.setDestroyed(false);
			        mounted.setRepairable(true);
			        unit.repairSystem(CriticalSlot.TYPE_EQUIPMENT, unit.getEntity().getEquipmentNum(mounted));
				}
			}
		}
	}
	
	@Override
    public String checkFixable() {
		if(isSalvaging()) {
			return null;
		}
        // The part is only fixable if the location is not destroyed.
        // We have to cycle through all locations because some equipment is spreadable.
        for(int loc = 0; loc < unit.getEntity().locations(); loc++) {
            for (int i = 0; i < unit.getEntity().getNumberOfCriticals(loc); i++) {
                CriticalSlot slot = unit.getEntity().getCritical(loc, i);
                
                // ignore empty & system slots
                if ((slot == null) || (slot.getType() != CriticalSlot.TYPE_EQUIPMENT)) {
                    continue;
                }
                
                if (equipmentNum == slot.getIndex()) {
                    if (unit.isLocationDestroyed(loc)) {
                        return unit.getEntity().getLocationName(loc) + " is destroyed.";
                    }
                }
            }
        }       
        return null;
    }

	/**
     * Copied from megamek.common.Entity.getWeaponsAndEquipmentCost(StringBuffer detail, boolean ignoreAmmo)
     *
     */
    @Override
    public long getStickerPrice() {
    	//OK, we cant use the resolveVariableCost methods from megamek, because they
    	//rely on entity which may be null if this is a spare part. So we use our 
    	//own resolveVariableCost method
    	//TODO: we need a static method that returns whether this equipment type depends upon
    	// - unit tonnage
    	// - item tonnage
    	// - engine
    	// use that to determine how to add things to the parts store and to 
    	// determine whether what can be used as a replacement
    	//why does all the proto ammo have no cost?
    	Entity en = null;
    	boolean isArmored = false;
    	if (unit != null) {
            en = unit.getEntity();
            Mounted mounted = unit.getEntity().getEquipment(equipmentNum);
            if(null != mounted) {
            	isArmored = mounted.isArmored();
            }
    	}

        int itemCost = 0;      
        try {
        	itemCost = (int) type.getCost(en, isArmored);
        	if (itemCost == EquipmentType.COST_VARIABLE) {
        		itemCost = resolveVariableCost(isArmored);
        	}
        } catch(NullPointerException ex) {
        	System.out.println("Found a null entity while calculating cost for " + name);
        }
        return itemCost;
    }
    
    private int resolveVariableCost(boolean isArmored) {
    	int varCost = 0;
        if (type instanceof MiscType) {
            if (type.hasFlag(MiscType.F_MASC)) {
            	//TODO: account for protomechs
               /* if (entity instanceof Protomech) {
                    varCost = Math.round(entity.getEngine().getRating() * 1000 * entity.getWeight() * 0.025f);
                } else */
            	if (type.hasSubType(MiscType.S_SUPERCHARGER)) {
            		varCost = engineRating * 10000;
                } else {
                    varCost = (int) (engineRating * getTonnage() * 1000);
                }
            } else if (type.hasFlag(MiscType.F_TARGCOMP)) {
                varCost = (int) Math.ceil(getTonnage() * 10000);
            } else if (type.hasFlag(MiscType.F_CLUB) && (type.hasSubType(MiscType.S_HATCHET) || type.hasSubType(MiscType.S_MACE_THB))) {
                varCost = (int) Math.ceil(getTonnage() * 5000);
            } else if (type.hasFlag(MiscType.F_CLUB) && type.hasSubType(MiscType.S_SWORD)) {
                varCost = (int) Math.ceil(getTonnage() * 10000);
            } else if (type.hasFlag(MiscType.F_CLUB) && type.hasSubType(MiscType.S_RETRACTABLE_BLADE)) {
                varCost = (int) Math.ceil((1+getTonnage()) * 10000);
            } else if (type.hasFlag(MiscType.F_TRACKS)) {
            	//TODO: need engine
                //varCost = (int) Math.ceil((500 * entity.getEngine().getRating() * entity.getWeight()) / 75);
            } else if (type.hasFlag(MiscType.F_TALON)) {
                varCost = (int) Math.ceil(getUnitTonnage() * 300);
            }

        } else {
            if (varCost == 0) {
                // if we don't know what it is...
                System.out.println("I don't know how much " + name + " costs.");
            }
        }

        if (isArmored) {
        	//need a getCriticals command - but how does this work?
            //varCost += 150000 * getCriticals(entity);
        }
        return varCost;
    }
    
    public static boolean hasVariableTonnage(EquipmentType type) {
    	return type.hasFlag(MiscType.F_TARGCOMP) ||
    			type.hasFlag(MiscType.F_MASC) ||
    			type.hasFlag(MiscType.F_CLUB) ||
    			type.hasFlag(MiscType.F_TALON);
    			
    }
    
    public static double getStartingTonnage(EquipmentType type) {
    	return 1;
    }
    
    public static double getMaxTonnage(EquipmentType type) {
    	if (type.hasFlag(MiscType.F_TALON)|| (type.hasFlag(MiscType.F_CLUB) && (type.hasSubType(MiscType.S_HATCHET) || type.hasSubType(MiscType.S_MACE_THB)))) {
            return 7;
        } else if (type.hasFlag(MiscType.F_CLUB) && (type.hasSubType(MiscType.S_LANCE) || type.hasSubType(MiscType.S_SWORD))) {
            return 5;
        } else if (type.hasFlag(MiscType.F_CLUB) && type.hasSubType(MiscType.S_MACE)) {
            return 10;
        } else if (type.hasFlag(MiscType.F_CLUB) && type.hasSubType(MiscType.S_RETRACTABLE_BLADE)) {
            return 5.5;
        } else if (type.hasFlag(MiscType.F_MASC)) {
        	if(type.hasSubType(MiscType.S_SUPERCHARGER)) {
        		return 10.5;
        	} else {
        		if(TechConstants.isClan(type.getTechLevel())) {
        			return 4;
        		} else {
        			return 5;
        		}
        	}
        } else if (type.hasFlag(MiscType.F_TARGCOMP)) {
        	//direct fire weapon weight divided by 4  - what is reasonably the highest - 15 tons?
        	return 15;
        }
    	return 1;
    }
    
    public static double getTonnageIncrement(EquipmentType type) {
    	if((type.hasFlag(MiscType.F_CLUB) && type.hasSubType(MiscType.S_RETRACTABLE_BLADE))
    			|| (type.hasFlag(MiscType.F_MASC) && type.hasSubType(MiscType.S_SUPERCHARGER))) {
    		return 0.5;
    	}
    	return 1;
    }
}
