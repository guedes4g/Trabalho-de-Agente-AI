package com.company.models;

import com.company.helpers.ElementType;

public class Chest extends Element {
    private int capacity;

    public Chest(int x, int y) {
        super(x, y, ElementType.chest);
        this.capacity = 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
