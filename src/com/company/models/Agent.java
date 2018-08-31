package com.company.models;

import java.util.ArrayList;

public class Agent {
    private Agent INSTANCE;

    private Positon positon;
    //Memory
    private ArrayList<Chest> chests;
    private ArrayList<Bag> bags;

    private Agent(Positon positon) {
    }

    public Agent genereateAgent(Positon positon){
        this.INSTANCE = new Agent(positon);
        return INSTANCE;
    }

    public Agent getInstance(){
        return INSTANCE;
    }

    public Positon getPositon() {
        return positon;
    }

    public void setPositon(Positon positon) {
        this.positon = positon;
    }

    public ArrayList<Chest> getChests() {
        return chests;
    }

    public void setChests(ArrayList<Chest> chests) {
        this.chests = chests;
    }

    public ArrayList<Bag> getBags() {
        return bags;
    }

    public void setBags(ArrayList<Bag> bags) {
        this.bags = bags;
    }
}
