package com.company.models;

import com.company.Config;
import com.company.helpers.ElementType;
import com.company.helpers.Map;
import java.util.ArrayList;
import java.util.Random;

public class Agent {
    private static Agent INSTANCE =  new Agent(new Positon(5,5));;
    private Map map;
    private Positon positon;
    //Memory
    private ArrayList<Chest> chests;
    private ArrayList<Bag> bags;
    private Random random;

    private Agent(Positon positon) {
        this.positon = positon;
        random = new Random();
        map = Map.getInstance();
    }

    
    public void explore() {
        while(true) {
             switch(random.nextInt(4)) {
                 case 0:
                     if(positon.getX() != Config.MapX - 1 &&
                             map.getElementAt(positon.getX() + 1, positon.getY()).getType() == ElementType.floor) {
                         positon.setX(positon.getX() + 1);
                         return;
                     } else if(positon.getX() != Config.MapX - 1 &&
                             map.getElementAt(positon.getX() + 1, positon.getY()).getType() == ElementType.hole) {
                         
                     }
                     break;
                 case 1:
                     if(positon.getX() != 0 &&
                             map.getElementAt(positon.getX() - 1, positon.getY()).getType() == ElementType.floor) {
                         positon.setX(positon.getX() + 1);
                         return;
                     }
                     break;
                 case 2:
                     if(positon.getY() != Config.MapY - 1 &&
                             map.getElementAt(positon.getX(), positon.getY() + 1).getType() == ElementType.floor) {
                         positon.setY(positon.getY() + 1);
                         return;
                     }
                     break;
                 case 3:
                     if(positon.getY() != 0 &&
                             map.getElementAt(positon.getX(), positon.getY() - 1).getType() == ElementType.floor) {
                         positon.setY(positon.getY() - 1);
                         return;
                     }
                     break;
             }
        }
    }

    public static Agent getInstance(){
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
