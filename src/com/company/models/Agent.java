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
                     if(map.outOfBounds(positon.getX() + 1, positon.getY()) )
                         break;

                     if(map.getElementAt(positon.getX() + 1, positon.getY()).getType() == ElementType.floor) {
                         positon.setX(positon.getX() + 1);
                         return;
                     } else if( map.getElementAt(positon.getX() + 1, positon.getY()).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() + 2, positon.getY()) &&
                                 map.getElementAt(positon.getX() + 2, positon.getY()).isWalkable())
                             positon.setX(positon.getX() + 2);
                     }
                     break;
                 case 1:
                     if(map.outOfBounds(positon.getX() - 1, positon.getY()) )
                         break;

                     if(map.getElementAt(positon.getX() - 1, positon.getY()).getType() == ElementType.floor) {
                         positon.setX(positon.getX() + 1);
                         return;
                     } else if( map.getElementAt(positon.getX() - 1, positon.getY()).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() - 2, positon.getY()) &&
                                 map.getElementAt(positon.getX() +-2, positon.getY()).isWalkable())
                             positon.setX(positon.getX() - 2);
                     }
                     break;
                 case 2:
                     if(map.outOfBounds(positon.getX(), positon.getY()+1) )
                         break;

                     if(map.getElementAt(positon.getX(), positon.getY() + 1).getType() == ElementType.floor) {
                         positon.setY(positon.getY() + 1);
                         return;
                     }  else if( map.getElementAt(positon.getX() , positon.getY() + 1).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() , positon.getY()+2) &&
                                 map.getElementAt(positon.getX() , positon.getY()+2).isWalkable())
                             positon.setY(positon.getY() + 2);
                     }
                     break;
                 case 3:
                     if(map.outOfBounds(positon.getX() + 1, positon.getY()-1) )
                         break;

                     if(map.getElementAt(positon.getX(), positon.getY() - 1).getType() == ElementType.floor) {
                         positon.setY(positon.getY() - 1);
                         return;
                     } else if( map.getElementAt(positon.getX() , positon.getY() - 1).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() , positon.getY()-2) &&
                                 map.getElementAt(positon.getX() , positon.getY()-2).isWalkable())
                             positon.setY(positon.getY() - 2);
                     }
                     break;
             }
        }
    }

    /**
     * Filtra por tipo
     * @param all
     * @param type
     * @return
     */
    public ArrayList<Element> filterElementByType(ArrayList<Element> all, ElementType type){
        ArrayList<Element> result = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            if(all.get(i).getType() == type)
                result.add(all.get(i));
        }
        return result;
    }

    /**
     * OLHA AO REDOR
     */
    public ArrayList<Element> lookAround(){
        ArrayList<Element> result = new ArrayList<>();
        for (int x = this.positon.getX()-2; x < this.positon.getX()+2; x++) {
            for (int y = this.positon.getY()-2; y < this.positon.getY()+2; y++) {
                if(this.map.outOfBounds(x,y))
                    continue;
                result.add(this.map.getElementAt(x,y));
            }
        }
        return result;
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
