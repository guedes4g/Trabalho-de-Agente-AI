package com.company.models;

import com.company.Config;
import com.company.helpers.AStar;
import com.company.helpers.ElementType;
import com.company.helpers.Genetic;
import com.company.helpers.Map;

import java.util.*;

public class Agent {
    private static Agent INSTANCE =  new Agent(new Position(5,5));;
    private Map map;
    private Position position;
    private Estado estado;
    private int pontuacao;

    //Memory
    private Position doorPosition;
    private HashSet<Chest> chests;
    private ArrayList<Bag> bags;
    private Random random;

    private Agent(Position position) {
        this.position = position;
        //ESTADO INICIAL
        this.estado = Estado.PROCURA_MOEDAS;
        pontuacao = 0;
        random = new Random();
        map = Map.getInstance();
        bags = new ArrayList<>();
        chests = new HashSet<>();
    }

    public void slowExecution(){
        /*EXECUTAR DE FORMA MAIS LENTA*/
        if(Config.Should_Execute_Slower) {
            try {
                Thread.sleep(Config.timeout_ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addPoints(int p) {
        pontuacao += p;
    }

    public int getPontuacao(){
        return this.pontuacao;
    }

    public void start(){

        while (true){
            slowExecution();
            System.out.println("\n\n\n\n\n\n");
            System.out.println(map);

            if(Config.DEBUG) System.out.println("Estado " + estado.toString());
            ArrayList<Element> surrounding = lookAround();
            switch (estado){
                case PROCURA_MOEDAS:
                    lookForDoor(surrounding);
                    lookForChests(surrounding);
                    searchBags(surrounding);
                    break;

                case PROCURA_BAU:
                    searchChests(surrounding);
                    break;

                case DISTRIBUI_MOEDAS:
                    this.deliverCoins();
                    break;

                case PROCURA_PORTA:
                    this.searchDoor(surrounding);
                    break;

                case SAINDO_SALA:
                    System.out.println(this.doorPosition);
                    exit();
                    break;
                case FIM:
                    return;
            }
        }

    }

    private void searchDoor(ArrayList<Element> surrounding) {
        Element door = filterElementByTypeSingle(surrounding, ElementType.door);
        if(door != null){
            this.estado = Estado.SAINDO_SALA;
            this.doorPosition = door.getPosition();
        } else
            this.explore();
    }


    /**
     * Estado de busca por sacos
     * @param surrounding
     */
    public void searchBags(ArrayList<Element> surrounding) {
        ArrayList<Element> bags = filterElementByType(surrounding, ElementType.bag);

        if (!this.hasAllBags()){
            if (bags.size() > 0) {
                if (Config.DEBUG) System.out.println("Going to Bag");
                goGetBag((Bag) getClosestElemet(bags));
            } else {
                if (Config.DEBUG) System.out.println("EXPLORING");
                explore();
            }
        } else {
            if(!this.hasAllChests())
                this.estado = Estado.PROCURA_BAU;
            else
                this.estado = Estado.DISTRIBUI_MOEDAS;
        }
    }

    private Element getClosestElemet(ArrayList<Element> elements){
        Element closest = null;
        int minDistance = Integer.MAX_VALUE;
        int currentDistance;
        for(Element element: elements){
            currentDistance = this.position.manhattanDistance(element.getPosition());
            if(minDistance > currentDistance){
                closest = element;
                minDistance = currentDistance;
            }
        }

        return closest;
    }

    private void moveWithAStar(Position destination, int offset) {
        moveWithAStar(map.getElementAt(this.position), map.getElementAt(destination), offset);
    }

    private void moveWithAStar(Element from, Element to, int offset){
        Stack<Position> path = AStar.run(from, to);
        if(Config.DEBUG) System.out.println(path);

        int size =  path.size();
        if(Config.DEBUG) map.printWithPath(path);
        for (int i = 0; i < size - offset ; i++) {
            position = path.pop();
            if(i == (size-2 - offset)&& map.getElementAt(position).getType() == ElementType.bag){
                System.out.println(map.getElementAt(position).getType());
                bags.add(map.catchBag(position));
            }
            slowExecution();

            ArrayList<Element> elements = this.lookAround();
            if(doorPosition == null) lookForDoor(elements);
            if(!hasAllChests()) lookForChests(elements);

            if(Config.DEBUG) System.out.println("A* to " + position);
            System.out.println(map);
        }
    }

    private boolean hasAllBags() {
        return this.bags.size() == Config.BAG_VALUES.length;
    }

    private boolean hasAllChests() {
        return this.chests.size() == Config.NumberOfChests;
    }

    private void lookForChests(ArrayList<Element> surrounding) {
        ArrayList<Element> chests = filterElementByType(surrounding, ElementType.chest);
        for(Element chest: chests)
            this.chests.add((Chest) chest);
    }

    private void lookForDoor(ArrayList<Element> surrounding) {
        if(doorPosition == null) {
            Element door = filterElementByTypeSingle(surrounding, ElementType.door);
            if(door != null) this.doorPosition = door.getPosition();
        }
    }

    /**
     * Vai até bag e pega ela e armazena
     * @param element
     */
    private void goGetBag(Bag element) {
        moveWithAStar(element.getPosition(),0);
        addPoints( element.getValue() * Config.Point_Coin );
        bags.add(map.catchBag(element.getPosition()));
    }

    /**
     * Estado de busca para guardar sacos
     * @param surrounding
     */
    public void searchChests(ArrayList<Element> surrounding){
        if(!this.hasAllChests()){
            lookForChests(surrounding);
            explore();
        } else {
            this.estado = Estado.DISTRIBUI_MOEDAS;
        }
    }

    /**
     * Distribui moedas pelas Baus
     */
    private void deliverCoins() {
        int[] rawSolution = Genetic.solution(this.bags);

        // distribuir moedas A*
        HashMap<Chest, ArrayList<Bag>> solution = understandSolution(rawSolution );
        printSolution(solution);
        for(Chest chest: solution.keySet()){
            addCoinsToChest(chest, solution.get(chest));
        }
        // Se nao save onde está a porta busca estado vira Procura porta se nao vira Saindo...
        if(this.doorPosition == null)
            this.estado = Estado.PROCURA_PORTA;
        else
            this.estado = Estado.SAINDO_SALA;

        addPoints(Config.Point_Door);
    }

    private void printSolution(HashMap<Chest, ArrayList<Bag>> solution) {
        System.out.println("Genetic Solution:");
        int i = 0;
        for (Chest chest: solution.keySet()) {
            System.out.print("chest_" + (++i) + " =>  ");
            for(Bag bag: solution.get(chest)){
                System.out.print(bag.getValue() +" ;");
            }
            System.out.println();
        }

    }

    private void addCoinsToChest(Chest chest, ArrayList<Bag> bags) {
        moveWithAStar(chest.getPosition(),0);
        for(Bag b: bags)
            chest.addBag(b);
    }

    private HashMap<Chest, ArrayList<Bag>> understandSolution(int[] solution) {
        HashMap<Chest, ArrayList<Bag>> result = new HashMap<>();
        List<Chest> chests = new ArrayList<>(this.chests);
        for(int i = 0; i < this.bags.size(); i++){
            if(result.containsKey(chests.get(solution[i]))){
                result.get(chests.get(solution[i])).add(this.bags.get(i));
            } else {
                ArrayList<Bag> b = new ArrayList<>();
                b.add(this.bags.get(i));
                result.put(chests.get(solution[i]), b);
            }
        }
        return result;
    }


    /**
     * Estado de saída da sala
     */
    public void exit(){
        this.moveWithAStar(doorPosition,1);
        map.openDoor();
        System.out.println(map);
        this.exitMap();
        addPoints(Config.Point_Exit);
        this.estado = Estado.FIM;
    }

    private void exitMap() {
        int x = this.position.getX() - this.doorPosition.getX();
        int y = this.position.getY() - this.doorPosition.getY();
        for (int i = 0; i < 2; i++) {
            this.setPosition(new Position(this.position.getX() - x, this.position.getY() - y));
            System.out.println(map);
        }

    }

    public void explore() {
        while(true) {
            switch(random.nextInt(4)) {
                case 0:
                    if(map.outOfBounds(position.getX() + 1, position.getY()) ) {
                        break;
                    }
                    if(map.getElementAt(position.getX() + 1, position.getY()).isWalkable()) {
                        if(Config.DEBUG) System.out.println("walked" + ( position.getX() + 1) + " - " +  position.getY());
                        position.setX(position.getX() + 1);
                        return;
                    } else if( map.getElementAt(position.getX() + 1, position.getY()).getType() == ElementType.hole) {

                        if(!map.outOfBounds(position.getX() + 2, position.getY()) &&
                                map.getElementAt(position.getX() + 2, position.getY()).isWalkable()) {
                            position.setX(position.getX() + 2);
                            if(Config.DEBUG) System.out.println("Jumped" + ( position.getX() + 2) + " - " +  position.getY());
                            addPoints(Config.Point_Jump);
                            return;
                        }
                    }
                    break;
                case 1:
                    if(map.outOfBounds(position.getX() - 1, position.getY()) ){
                        break;
                    }

                    if(map.getElementAt(position.getX() - 1, position.getY()).isWalkable()) {
                        position.setX(position.getX() - 1);
                        if(Config.DEBUG) System.out.println("walked" + ( position.getX() - 1) + " - " +  position.getY());
                        return;
                    } else if( map.getElementAt(position.getX() - 1, position.getY()).getType() == ElementType.hole) {

                        if(!map.outOfBounds(position.getX() - 2, position.getY()) &&
                                map.getElementAt(position.getX() -2, position.getY()).isWalkable()){
                            if(Config.DEBUG) System.out.println("Jumped" + ( position.getX() -2) + " - " +  position.getY());
                            position.setX(position.getX() - 2);
                            addPoints(Config.Point_Jump);
                            return;
                        }
                    }
                    break;
                case 2:
                    if(map.outOfBounds(position.getX(), position.getY()+1) ){
                        break;
                    }

                    if(map.getElementAt(position.getX(), position.getY() + 1).isWalkable()) {
                        position.setY(position.getY() + 1);
                        if(Config.DEBUG) System.out.println("walked" + ( position.getX() ) + " - " +  ( position.getY() + 1));
                        return;
                    }  else if( map.getElementAt(position.getX() , position.getY() + 1).getType() == ElementType.hole) {

                        if(!map.outOfBounds(position.getX() , position.getY()+2) &&
                                map.getElementAt(position.getX() , position.getY()+2).isWalkable()){
                            if(Config.DEBUG) System.out.println("Jumped" + ( position.getX() + 1) + " - " +  ( position.getY() + 2));
                            position.setY(position.getY() + 2);
                            addPoints(Config.Point_Jump);
                            return;
                        }
                    }
                    break;
                case 3:
                    if(map.outOfBounds(position.getX(), position.getY()-1) ){
                        break;
                    }


                    if(map.getElementAt(position.getX(), position.getY() - 1).isWalkable()) {
                        if(Config.DEBUG) System.out.println("walked" + ( position.getX() + 1) + " - " +  ( position.getY() - 1));
                        position.setY(position.getY() - 1);
                        return;
                    } else if( map.getElementAt(position.getX() , position.getY() - 1).getType() == ElementType.hole) {

                        if(!map.outOfBounds(position.getX() , position.getY()-2) &&
                                map.getElementAt(position.getX() , position.getY()-2).isWalkable()){
                            if(Config.DEBUG) System.out.println("Jumped" + ( position.getX() + 1) + " - " +  ( position.getY() - 2));
                            position.setY(position.getY() - 2);
                            addPoints(Config.Point_Jump);
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
    public Element filterElementByTypeSingle(ArrayList<Element> all, ElementType type){
        for (int i = 0; i < all.size(); i++) {
            if(all.get(i).getType() == type) {
                return all.get(i);
            }
        }
        return null;
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
        for (int x = this.position.getX()-2; x <= this.position.getX()+2; x++) {
            for (int y = this.position.getY()-2; y <=this.position.getY()+2; y++) {
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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public HashSet<Chest> getChests() {
        return chests;
    }

    public void setChests(HashSet<Chest> chests) {
        this.chests = chests;
    }

    public ArrayList<Bag> getBags() {
        return bags;
    }

    public void setBags(ArrayList<Bag> bags) {
        this.bags = bags;
    }
}
