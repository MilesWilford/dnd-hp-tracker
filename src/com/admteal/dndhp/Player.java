package com.admteal.dndhp;

import java.io.Serializable;
import java.util.ArrayList;

//TODO: MOVE THE MATH TO THIS CLASS RATHER THAN DNDHPActivity.java
public class Player implements Serializable {
	private static final long serialVersionUID = -736811401990731225L; //generated
	
	private boolean blinded, dazed, deafened, dominated, dying, grabbed, 
		helpless, immobile, marked, petrified, prone, restrained, 
		stunned, unconscious, weakened, usingDefaultPlayer;
		
    // The player's various stats and HPs are tracked in this class.

	private int maxHP, maxSurges, currentTHP, currentHS, currentOngo;
    private int currentHP, currentSurges, currentDeathSaves;
    /* 
     * Note: class uses int division because we round down in D&D.
     * 3�2 should equal 1.
     */
    private ArrayList<Integer> changeHistory = new ArrayList<Integer>();
    private ArrayList<Integer> HPHistory = new ArrayList<Integer>();
    
    //default constructor for if we aren't passing a real max hp
    public Player() {
    	maxHP = 999;
    	currentHP = 0;
    	currentHP = 0;
    	currentTHP = 0;
    	currentHS = 0;
     	currentOngo = 0;
     	maxSurges = 99;
     	currentSurges = 0;
     	currentDeathSaves = 3;
     	clearToggles();
     	usingDefaultPlayer = true;
    }
    
    //create a new player at full HP
    public Player(int newHP, int newSurges) {
    	currentHP = newHP;
        maxHP = newHP;
        maxSurges = newSurges;
        currentSurges = maxSurges;
        clearToggles();
     	usingDefaultPlayer = false;
    }
    
    //create a new player at less than full HP
    public Player(int newHP, int newMaxHP, int newSurges, int newMaxSurges) {
    	currentHP = newHP;
    	currentHP = newMaxHP;
        currentSurges = newSurges;
        maxSurges = newMaxSurges;
        clearToggles();
     	usingDefaultPlayer = false;
    }
    
    public void heal(int healBy) {
    	if (currentHP + healBy > maxHP) { //No going above max.
    		changeHistory.add(maxHP-currentHP);
    		currentHP = maxHP;
    	} else {
        	currentHP += healBy;
        	changeHistory.add(healBy);
    	}
    	dying = false;
    	unconscious = false;
    	HPHistory.add(currentHP);
    }
    
	public void injure(int injureBy) {
		int negBloodied = -maxHP / 2;
		// No going below negative bloodied (you're dead)
		if (currentHP + currentTHP - injureBy <= negBloodied) {
			currentTHP = 0;
			changeHistory.add(negBloodied - currentHP);
			currentHP = negBloodied;
		} else {
			// Negative to make it clear this was an injury in the history
			changeHistory.add(-injureBy);
			/*
			 * Consume THP first, before affecting actual HP. We do not want
			 * either currentTHP or injureBy to drop below 0 at any point
			 */
			if (injureBy > currentTHP) {
				injureBy -= currentTHP;
				currentTHP = 0;
			} else if (currentTHP > injureBy) {
				currentTHP -= injureBy;
				injureBy = 0;
			} else {
				currentTHP = 0;
				injureBy = 0;
			}

			currentHP -= injureBy;
		}
		HPHistory.add(currentHP);
	}

    //HP VALUE
    public int getHP() {
        return currentHP;
    }

    public void setHP(int newHP) {
        currentHP = newHP;
    }

    //MAX HP VALUE
    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int newMaxHP) {
        maxHP = newMaxHP;
    }
    	
    //BLOODIED VALUE
    public boolean isBloodied() {
    	if (currentHP <= maxHP/2) {
    		return true;
    	} else {
    		return false;
    	}
    }

    //TEMPORARY HP
    public int getTHP() {
    	return currentTHP;
    }
    
    //Only the largest THP number offered applies.
    public void addTHP(int newTHP) {
    	if (newTHP > currentTHP) {
    		currentTHP = newTHP;
    	}
    }
    
    //SURGES COUNT
    public int getSurges() {
        return currentSurges;
    }

    public void setSurges(int newSurges) {
    	if (newSurges >= 0) {
            currentSurges = newSurges;
    	}
    }
    
    public void addSurge() {
    	currentSurges++;
    }
    
    public void remSurge() {
    	if (currentSurges == 0) {
    		return;
    	}
    	currentSurges--;
    }
    
    //SURGE VALUE
    public void setHS(int newHS) {
    	currentHS = newHS;
    }
    
    public int getHS() {
    	return currentHS;
    }
    
    //ONGOING VALUE
    public void setOngo(int newOngo) {
    	currentOngo = newOngo;
    }
    
    public int getOngo() {
    	return currentOngo;
    }
    	
    //regen is the opposite of ongoing damage
    public int getRegen() { 
        return currentOngo * -1;
    }
	    
    public void addOngo() {
    	currentOngo++;
    }
    
    public void remOngo() {
    	currentOngo--;
    }
    
    //DEATH SAVES COUNT
    public void setDeathSaves(int newDeathSaves) {
    	currentDeathSaves = newDeathSaves;
    }
    
    public int getDeathSaves() {
    	return currentDeathSaves;
    }
    
    public void addDeathSave() {
    	currentDeathSaves++;
    }
    
    public void remDeathSave() {
    	currentDeathSaves--;
    }
    
    //STATUS EFFECTS    
	public boolean isBlinded() {return blinded;}
	public void blind() {
		blinded = true;
	}
	public void unblind() {
		blinded = false;
	}
	
	public boolean isDazed() {return dazed;}
	public void daze() {
		dazed = true;
	}
	
	public void undaze() {
		dazed = false;
	}
	
	public boolean isDeafened() {return deafened;}
	public void deafen() {
		deafened = true;
	}
	public void undeafen() {
		deafened = false;
	}
	
	public boolean isDominated() {return dominated;}
	public void dominate() {
		dominated = true;
	}
	public void undominate() {
		dominated = false;
	}
	
	public boolean isDying() {return dying;}
	public void kill() {
		dying = true;
	}
	public void unkill() {
		dying = false;
	}
	
	public boolean isGrabbed() {return grabbed;}
	public void grab() {
		grabbed = true;
	}
	public void ungrab() {
		grabbed = false;
	}
	
	public boolean isHelpless() {return helpless;}
	public void incapacitate() {
		helpless = true;
	}
	public void unincapacitate() {
		helpless = false;
	}
	
	public boolean isImmobile() {return immobile;}
	public void immobilize() {
		immobile = true;
	}
	public void unimmobilize() {
		immobile = false;
	}
	
	public boolean isMarked() {return marked;}
	public void mark() {
		marked = true;
	}
	public void unmark() {
		marked = false;
	}
	
	public boolean isPetrified() {return petrified;}
	public void petrify() {
		petrified = true;
	}
	public void unpetrify() {
		petrified = false;
	}
	
	public boolean isProne() {return prone;}
	public void knockProne() {
		prone = true;
	}
	public void getUp() {  //NOTE THIS BREAKS THE FORM BECAUSE unknockProne SOUNDS STUPID
		prone = false;
	}
	
	public boolean isRestrained() {return restrained;}
	public void restrain() {
		restrained = true;
	}
	public void unrestrain() {
		restrained = false;
	}
	
	public boolean isStunned() {return stunned;}
	public void stun() {
		stunned = true;
	}
	public void unstun() {
		stunned = false;
	}
	
	public boolean isUnconscious() {return unconscious;}
	public void knockOut() {
		unconscious = true;
	}
	public void wakeUp() {  //NOTE THIS BREAKS THE FORM BECAUSE unknockOut SOUNDS STUPID
		unconscious = false;
	}
	
	public boolean isWeakened() {return weakened;}
	public void weaken() {
		weakened = true;
	}
	public void unweaken() {
		weakened = false;
	}
	
	//Clears all the toggles.  Private because it should only be called by making a new Player
	private void clearToggles() {
		blinded = false;
		dazed = false;
		deafened = false;
		dominated = false;
		dying = false;
		grabbed = false;
		helpless = false;
		immobile = false;
		marked = false;
		petrified = false;
		prone = false;
		restrained = false;
		stunned = false;
		unconscious = false;
		weakened = false;	
	}
	
	public void extendedRest() {
		clearToggles();
		if (usingDefaultPlayer) {
			currentHP = 0;
			currentSurges = 0;
		} else {
			currentHP = maxHP;
			currentSurges = maxSurges;
		}
	}
    
	//History trackers
    public ArrayList<Integer> getChangeHistory() {
    	return changeHistory;
    }
    
    public ArrayList<Integer> getHPHistory() {
    	return HPHistory;
    }

}
