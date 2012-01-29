package com.admteal.dndhp;

//TODO: MOVE THE MATH TO THIS CLASS RATHER THAN DNDHPActivity.java
public class Player {

    // The players various stats and HPs are tracked in this class.
	
	private int maxHP;
    private int currentHP, currentHS, currentOngo, currentEntry, currentSurges, currentDeathSaves;
    
    //default constructor for if we aren't passing a real max hp
    public Player() {
    	maxHP = 999;
    	currentHP = 0;
    	currentHP = 0;
    	currentHS = 0;
     	currentOngo = 0;
     	currentEntry = 0;
     	currentSurges = 0;
     	currentDeathSaves = 3;
    }
    
    //create a new player at full HP
    public Player(int newHP, int newSurges) {
    	setHP(newHP);
        setMaxHP(newHP);
        setSurges(newSurges);
    }
    
    //create a new player at less than full HP
    public Player(int newHP, int newMaxHP, int newSurges) {
    	setHP(newHP);
    	setMaxHP(newMaxHP);
        setSurges(newSurges);
    }

   //modify current HP.  Do not allow current HP to go above max HP
    // TODO: TRACK HP ABOVE MAX HP AS TEMPORARY HP?  SOMETHING NEEDS TO BE DONE TO ACCOUNT FOR THP
    public int hpMod(int changeBy) {
    	if (currentHP + changeBy > maxHP) {
    		currentHP = maxHP;
    	} else {
        	currentHP += changeBy;
    	}
    	return currentHP;
    }

    public int getHP() {
        return currentHP;
    }

    public void setHP(int newHP) {
        currentHP = newHP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(int newMaxHP) {
        maxHP = newMaxHP;
    }

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
    
    public void setHS(int newHS) {
    	currentHS = newHS;
    }
    
    public int getHS() {
    	return currentHS;
    }
    
    public void setOngo(int newOngo) {
    	currentOngo = newOngo;
    }
    
    public int getOngo() {
    	return currentOngo;
    }
    
    public int getRegen() { //regen is the opposite of ongoing damage
    	return currentOngo * -1;
    }
    public void addOngo() {
    	currentOngo++;
    }
    
    public void remOngo() {
    	currentOngo--;
    }
    
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
}
