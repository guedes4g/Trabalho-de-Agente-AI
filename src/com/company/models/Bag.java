package com.company.models;

import com.company.helpers.ElementType;

public class Bag extends Element{
    private int value;

    public Bag(int x, int y, int value) {
        super(x,y, ElementType.bag);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
