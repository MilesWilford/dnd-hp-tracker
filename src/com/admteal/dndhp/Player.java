package com.admteal.dndhp;

import java.io.Serializable;
import java.util.ArrayList;

//TODO: MOVE THE MATH TO THIS CLASS RATHER THAN DNDHPActivity.java
public class Player implements Serializable {
	private static final long serialVersionUID = -736811401990731225L; //generated

    // The player's various stats and HPs are tracked in this class.

	private int maxHP;
    private int currentHP, currentHS, currentOngo, currentSurges, currentDeathSaves, bloodied;
    /* 
     * Note: class uses int division because we round down in D&D.
     * 3÷2 should equal 1.
     */
    private ArrayList<Integer> changeHistory = new ArrayList<Integer>();
    private ArrayList<Integer> HPHistory = new ArrayList<Integer>();
    
    //default constructor for if we aren't passing a real max hp
    public Player() {
    	maxHP = 999;
     	bloodied = 499;
    	currentHP = 0;
    	currentHP = 0;
    	currentHS = 0;
     	currentOngo = 0;
     	currentSurges = 0;
     	currentDeathSaves = 3;
    }
    
    //create a new player at full HP
    public Player(int newHP, int newSurges) {
    	currentHP = newHP;
        maxHP = newHP;
        bloodied = newHP / 2;
        currentSurges = newSurges;
    }
    
    //create a new player at less than full HP
    public Player(int newHP, int newMaxHP, int newSurges) {
    	currentHP = newHP;
    	currentHP = newMaxHP;
        bloodied = newMaxHP / 2;
        currentSurges = newSurges;
    }

    // TODO: TRACK HP ABOVE MAX HP AS TEMPORARY HP?  SOMETHING NEEDS TO BE DONE TO ACCOUNT FOR THP
    
    public void heal(int healBy) {
    	if (currentHP + healBy > maxHP) { //No going above max.
    		changeHistory.add(maxHP-currentHP);
    		currentHP = maxHP;
    	} else {
        	currentHP += healBy;
        	changeHistory.add(healBy);
    	}
    	HPHistory.add(currentHP);
    }
    
    public void injure(int injureBy) {
    	int negBloodied = bloodied * -1;
    	//injureBy was passed as a negative number.  Treat it using addition for subtraction.
    	if (currentHP - injureBy <= negBloodied) { //No going below negative bloodied (you're dead)
    		changeHistory.add(negBloodied - currentHP);
    		currentHP = negBloodied;
    	} else {
    		currentHP -= injureBy;
    		changeHistory.add(-injureBy); //We store injuries as a negative number in the history.
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
        bloodied = maxHP / 2; 
    }
    	
    	public void setMaxHP(int newMaxHP, int newBloodied) {
    		maxHP = newMaxHP;
    		bloodied = newBloodied;
    	}
    	
    //BLOODIED VALUE
    public int getBloodied() {
    	return bloodied;
    }
    
    public void setBloodied(int newBloodied) {
    	bloodied = newBloodied;
    }

    //SURGES COUNT
    public int getSurges() {
        return currentSurges;
    }

    public void setSurges(int newSurges) {
        currentSurges = newSurges;
    }
    
    public void addSurge() {
    	currentSurges++;
    }
    
    public void remSurge() {
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
    
    public ArrayList<Integer> getChangeHistory() {
    	return changeHistory;
    }
    
    public ArrayList<Integer> getHPHistory() {
    	return HPHistory;
    }
}
