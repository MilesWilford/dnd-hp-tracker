package com.admteal.dndhp;

public class Player {

    /*
     * It is a java idiom to make your actual class attributes private and
     * provide getters and setters to access them. Those are at the bottom of
     * the class. The idea is to hide implementation details from users of the
     * class. For example, if you wanted to allow the health attribute to vary
     * outside of the range of 0 and maxHealth, you wouldn't have to expose that
     * to the user. In the getter, you could use a conditional to simply return
     * maxHealth if currentHealth is greater than it or 0 if currentHealth is
     * negative.
     */
    private int currentHealth, maxHealth, currentSurges;

    public Player(int health, int surges) {
        currentHealth = health;
        maxHealth = health;
        currentSurges = surges;
    }

    public Player(int curHP, int maxHP, int surges) {
        currentHealth = curHP;
        maxHealth = maxHP;
        currentSurges = surges;
    }

    /*
     * Reduces a player's health by the passed in value. If the player's health
     * would be reduced below zero, only reduces it to zero.
     */
    public void injure(int health) {
        currentHealth -= health;
        if (currentHealth < 0)
            currentHealth = 0;
    }

    /*
     * Increases a player's health by the passed in value. If the player's
     * health would increase above his maximum health, increases it to his
     * maximum health.
     */
    public void heal(int health) {
        currentHealth += health;
        if (currentHealth > maxHealth)
            currentHealth = maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int hp) {
        currentHealth = hp;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHP) {
        maxHealth = maxHP;
    }

    public int getCurrentSurges() {
        return currentSurges;
    }

    public void setCurrentSurges(int surges) {
        currentSurges = surges;
    }
}
