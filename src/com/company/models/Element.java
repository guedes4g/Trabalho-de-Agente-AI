package com.company.models;

import com.company.helpers.ElementType;

public class Element extends Positon {
    private ElementType type;

    public Element(int x, int y, ElementType type ){
        super(x,y);
        this.type = type;
    }

    public ElementType getType() {
        return type;
    }

    public void setType(ElementType type) {
        this.type = type;
    }

    public boolean isWalkable(){return (this.type == ElementType.floor || this.type == ElementType.bag || this.type == ElementType.chest ); }
    public boolean isWalkableOrHole(){return (this.type == ElementType.floor || this.type == ElementType.bag || this.type == ElementType.chest || this.type == ElementType.hole); }

}


