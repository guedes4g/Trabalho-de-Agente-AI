package com.company.models;

import com.company.Config;
import com.company.helpers.AStar;
import com.company.helpers.ElementType;
import com.company.helpers.Map;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Agent {
    private static Agent INSTANCE =  new Agent(new Positon(5,5));;
    private Map map;
    private Positon positon;
    private Estado estado;


    //Memory
    private ArrayList<Chest> chests;
    private ArrayList<Bag> bags;
    private Random random;

    private Agent(Positon positon) {
        this.positon = positon;
        //ESTADO INICIAL
        this.estado = Estado.PROCURA_MOEDAS;

        random = new Random();
        map = Map.getInstance();
        bags = new ArrayList<>();
        chests = new ArrayList<>();
    }

    public void start(){

        while (true){
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            System.out.println(map);
            if(Config.DEBUG) System.out.println("Estado " + estado.toString());

            switch (estado){
                case PROCURA_MOEDAS:
                    searchBags();
                    break;

                case PROCURA_BAU:
                    searchChests();
                    break;

                case SAINDO_SALA:
                    exit();
                    break;
            }
        }

    }

    /**
     * Estado de busca por sacos
     */
    public void searchBags(){
        ArrayList<Element> surrounding = lookAround();
        ArrayList<Element> bags = filterElementByType(surrounding, ElementType.bag);


        if(bags.size()>0){
            if(Config.DEBUG) System.out.println("Going to Bag" );
            goGetBag((Bag) bags.get(0));
        } else {
            if(Config.DEBUG) System.out.println("EXPLORING" );
            explore();
            if(Config.DEBUG) System.out.println("EXPLORED" );

        }
    }

    /**
     * Vai até bag e pega ela e armazena
     * @param element
     */
    private void goGetBag(Bag element) {
        Stack<Positon> path = AStar.run(map.getElementAt(positon), element);
        System.out.println(path);
        System.out.println(path.size());
        int size =  path.size();
        if(Config.DEBUG) map.printWithPath(path);
        for (int i = 0; i < size ; i++) {
            positon = path.pop();
            if(Config.DEBUG) System.out.println("A* to " + positon);
            System.out.println(map);
        }
        bags.add(
                map.catchBag(element.getPosition()));
    }

    /**
     * Estado de busca para guardar sacos
     */
    public void searchChests(){

    }

    /**
     * Estado de saída da sala
     */
    public void exit(){

    }

    public void explore() {
        while(true) {
             switch(random.nextInt(4)) {
                 case 0:
                     if(map.outOfBounds(positon.getX() + 1, positon.getY()) ) {
                         if(Config.DEBUG) System.out.println("Out of Bounds" + ( positon.getX() + 1) + " - " +  positon.getY());
                         break;
                     }
                     if(map.getElementAt(positon.getX() + 1, positon.getY()).isWalkable()) {
                         if(Config.DEBUG) System.out.println("walked" + ( positon.getX() + 1) + " - " +  positon.getY());
                         positon.setX(positon.getX() + 1);
                         return;
                     } else if( map.getElementAt(positon.getX() + 1, positon.getY()).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() + 2, positon.getY()) &&
                                 map.getElementAt(positon.getX() + 2, positon.getY()).isWalkable()) {
                             positon.setX(positon.getX() + 2);
                             if(Config.DEBUG) System.out.println("Jumped" + ( positon.getX() + 2) + " - " +  positon.getY());
                             return;
                         }
                     }
                     break;
                 case 1:
                     if(map.outOfBounds(positon.getX() - 1, positon.getY()) ){
                         if(Config.DEBUG) System.out.println("Out of Bounds" + (positon.getX() - 1 ) + " - " +  positon.getY());
                         break;
                     }

                     if(map.getElementAt(positon.getX() - 1, positon.getY()).isWalkable()) {
                         positon.setX(positon.getX() - 1);
                         if(Config.DEBUG) System.out.println("walked" + ( positon.getX() - 1) + " - " +  positon.getY());
                         return;
                     } else if( map.getElementAt(positon.getX() - 1, positon.getY()).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() - 2, positon.getY()) &&
                                 map.getElementAt(positon.getX() -2, positon.getY()).isWalkable()){
                             if(Config.DEBUG) System.out.println("Jumped" + ( positon.getX() -2) + " - " +  positon.getY());
                             positon.setX(positon.getX() - 2);
                             return;
                         }
                     }
                     break;
                 case 2:
                     if(map.outOfBounds(positon.getX(), positon.getY()+1) ){
                         if(Config.DEBUG) System.out.println("Out of Bounds" + positon.getX()  + " - " +  ( positon.getY() + 1) );
                         break;
                     }

                     if(map.getElementAt(positon.getX(), positon.getY() + 1).isWalkable()) {
                         positon.setY(positon.getY() + 1);
                         if(Config.DEBUG) System.out.println("walked" + ( positon.getX() ) + " - " +  ( positon.getY() + 1));
                         return;
                     }  else if( map.getElementAt(positon.getX() , positon.getY() + 1).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() , positon.getY()+2) &&
                                 map.getElementAt(positon.getX() , positon.getY()+2).isWalkable()){
                             if(Config.DEBUG) System.out.println("Jumped" + ( positon.getX() + 1) + " - " +  ( positon.getY() + 2));
                             positon.setY(positon.getY() + 2);
                             return;
                         }
                     }
                     break;
                 case 3:
                     if(map.outOfBounds(positon.getX(), positon.getY()-1) ){
                         if(Config.DEBUG) System.out.println("Out of Bounds" + positon.getX()  + " - " +  ( positon.getY() - 1) );
                         break;
                     }


                     if(map.getElementAt(positon.getX(), positon.getY() - 1).isWalkable()) {
                         if(Config.DEBUG) System.out.println("walked" + ( positon.getX() + 1) + " - " +  ( positon.getY() - 1));
                         positon.setY(positon.getY() - 1);
                         return;
                     } else if( map.getElementAt(positon.getX() , positon.getY() - 1).getType() == ElementType.hole) {

                         if(!map.outOfBounds(positon.getX() , positon.getY()-2) &&
                                 map.getElementAt(positon.getX() , positon.getY()-2).isWalkable()){
                             if(Config.DEBUG) System.out.println("Jumped" + ( positon.getX() + 1) + " - " +  ( positon.getY() - 2));
                             positon.setY(positon.getY() - 2);
                             return;
                         }
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
        for (int x = this.positon.getX()-2; x <= this.positon.getX()+2; x++) {
            for (int y = this.positon.getY()-2; y <=this.positon.getY()+2; y++) {
                if(this.map.outOfBounds(x,y)){
                    continue;
                }
                result.add(this.map.getElementAt(x,y));
            }
        }
        System.out.println();
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
