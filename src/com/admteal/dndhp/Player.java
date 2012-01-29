package com.admteal.dndhp;

//TODO: MOVE THE MATH TO THIS CLASS RATHER THAN DNDHPActivity.java
public class Player {

    // The player's various stats and HPs are tracked in this class.
	
	private int maxHP;
    private int currentHP, currentHS, currentOngo, currentSurges, currentDeathSaves;
    
    //default constructor for if we aren't passing a real max hp
    public Player() {
    	maxHP = 999;
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
        currentSurges = newSurges;
    }
    
    //create a new player at less than full HP
    public Player(int newHP, int newMaxHP, int newSurges) {
    	currentHP = newHP;
    	currentHP = newMaxHP;
        currentSurges = newSurges;
    }

	//modify current HP.  Do not allow current HP to go above max HP
    // TODO: TRACK HP ABOVE MAX HP AS TEMPORARY HP?  SOMETHING NEEDS TO BE DONE TO ACCOUNT FOR THP

    public void hpMod(int changeBy) {
    	if (currentHP + changeBy > maxHP) {
    		currentHP = maxHP;
    	} else {
        	currentHP += changeBy;
    	}
    }
    
    public void injure(int changeBy) {

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

    public int getSurges() {
        return currentSurges;
    }

    //SURGES COUNT
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
}
