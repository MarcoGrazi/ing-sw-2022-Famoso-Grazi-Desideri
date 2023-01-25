package main.java.com.polimi.app.models;

import main.java.com.polimi.app.controllers.*;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Character class.
 * Each instance is represented by an absolute id, the amount of coins the player must spend to activate the effect
 * and the character unique effect, implemented using the strategy pattern.
 * @author Group 53
 */
public class Character implements Serializable {
    private boolean firstTime = true;
    //The absolute Id.
    private int id;
    //Amount of coins the player must spend to activate the effect
    private int cost;
    //The effect related to the character
    private EffectStrategy effect;

    /**
     * Class constructor.
     * Initialize a specific character depending on the selected id. Invalid if id is not between 1 and 12.
     * @param id specific to each character
     * @param context the game controller instance
     */
    public Character(int id, GameController context){
        this.id = id;
        switch (this.id) {
            case 1 -> {
                cost = 1;
                effect = new Effect1(context);
            }
            case 2 -> {
                cost = 2;
                effect = new Effect2();
            }
            case 3 -> {
                cost = 3;
                effect = new Effect3();
            }
            case 4 -> {
                cost = 1;
                effect = new Effect4();
            }
            case 5 -> {
                cost = 2;
                effect = new Effect5();
            }
            case 6 -> {
                cost = 3;
                effect = new Effect6();
            }
            case 7 -> {
                cost = 1;
                effect = new Effect7(context);
            }
            case 8 -> {
                cost = 2;
                effect = new Effect8();
            }
            case 9 -> {
                cost = 3;
                effect = new Effect9();
            }
            case 10 -> {
                cost = 1;
                effect = new Effect10();
            }
            case 11 -> {
                cost = 2;
                effect = new Effect11(context);
            }
            case 12 -> {
                cost = 3;
                effect = new Effect12();
            }
            default -> {
                cost = 0;
                System.out.println("Character ID: " + id + " is invalid");
            }
        }
    }

    public Character(int id){
        this.id = id;
        switch (this.id) {
            case 1, 4, 7, 10 -> {
                cost = 1;
            }
            case 2, 5, 8, 11 -> {
                cost = 2;
            }
            case 3, 6, 9, 12 -> {
                cost = 3;
            }
            default -> {
                cost = 0;
                System.out.println("Character ID: " + id + " is invalid");
            }
        }
    }

    /**
     * Activates the character effect.
     * @param context the game controller instance
     * @param packet contains client choices and infos
     * @return a list of packets with all the useful info to update the view
     */
    public ArrayList<Packet> Effect(GameController context, Packet packet) {
        return effect.ActivateEffect(context, packet);
    }

    /**
     * @return the character's absolute Id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the character's current cost in coins
     */
    public int getCost() {
        return cost;
    }

    /**
     * Adds 1 to the character current cost at its first usage.
     */
    public void incrementCost() {
        if(firstTime) {
            cost++;
            firstTime = false;
        }
    }

    public EffectStrategy getEffect() {
        return effect;
    }

    /**
     * Utilized to add prohibition to Character 5 card.
     * @return the Effect5 instance
     */
    public Effect5 getEffect5() {
        if(id == 5) {
            return (Effect5) effect;
        }
        return null;
    }

    public Effect1 getEffect1() {
        if(id == 1) {
            return (Effect1) effect;
        }
        return null;
    }
}

