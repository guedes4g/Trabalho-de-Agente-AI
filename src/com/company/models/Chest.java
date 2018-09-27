package com.company.models;

import com.company.helpers.ElementType;

import java.util.ArrayList;

public class Chest extends Element {
    private int capacity;

    private ArrayList<Bag> bags;
    public Chest(int x, int y) {
        super(x, y, ElementType.chest);
        this.capacity = 0;
        bags = new ArrayList<>();
    }

    public void addBag(Bag b){
        this.bags.add(b);
    }

    public ArrayList<Bag> getBags(){
        return this.bags;
    }
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
