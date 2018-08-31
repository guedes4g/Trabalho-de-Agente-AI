package com.company.models;

public class Chest {
    private Positon positon;
    private int capacity;


    public Chest(Positon positon, int capacity) {
        this.positon = positon;
        this.capacity = capacity;
    }

    public Positon getPositon() {
        return positon;
    }

    public void setPositon(Positon positon) {
        this.positon = positon;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
