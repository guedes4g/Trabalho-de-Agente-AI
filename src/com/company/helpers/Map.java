package com.company.helpers;

import com.company.Config;
import com.company.models.*;
import com.company.models.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Map {
    private static Map INSTANCE =  new Map();
    private Element _map[][];
    private int freeSpace;
    private boolean doorClosed;
    private Position frontDoor;
    private boolean visited[][];
    private int total;
    private Random random;
    private int doorPlacement;

    private Map(){
        this._map = new Element[Config.MapX][Config.MapY];
        freeSpace = Config.MapX * Config.MapY;
        doorClosed = true;
        this.generateMap();
    }

    private void clearMap(){
        this._map = new Element[Config.MapX][Config.MapY];
    }

    public static synchronized Map getInstance(){
        return INSTANCE;
    }

    public Position getFreePosition() {
        Element e;
        while(true){
            e = getElementAt(random.nextInt(Config.MapY), random.nextInt(Config.MapY));
            if(e != null && e.isWalkable()){
                return e.getPosition();
            }
        }
    }

    private boolean generateChest(int x, int y) {
        if(_map[x][y] == null && !frontDoor.equals(new Position(x,y))) {
            _map[x][y] = new Chest(x,y);
            return true;
        }
        return false;
    }

    private void generateChests() {
        for(int i=0; i<4; i++) {
        boolean valido;
        switch (doorPlacement){
            case 0:
                do {
                    valido = generateChest(1, random.nextInt(Config.MapY));
                } while(!valido);
                break;

            case 1:
                do {
                    valido = generateChest(Config.MapX-2, random.nextInt(Config.MapY));
                } while(!valido);
                break;

            case 2:
                do {
                    valido = generateChest(random.nextInt(Config.MapX), 1);
                } while(!valido);
                break;

            case 3:
                do {
                    valido = generateChest(random.nextInt(Config.MapX), Config.MapY-2);
                } while(!valido);
                break;
        }
        }
    }
    private void generateMap() {
        random = new Random();
        boolean didGenerate = false;
        try {
            while(!didGenerate){
                didGenerate = this.setDoorLocationAndWalls();
                if(!didGenerate) clearMap();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        this.setFreeSpaces();
    }

    private void setFreeSpaces(){
        for (int x = 0; x < Config.MapX; x++)
            for (int y = 0; y < Config.MapY; y++)
                if(_map[x][y] == null) _map[x][y] = new Element(x,y, ElementType.floor);
    }

    private int[] generateElement() {
        while(true) {
            int x = random.nextInt(Config.MapX);
            int y = random.nextInt(Config.MapY);
            if(_map[x][y] == null && !frontDoor.equals(new Position(x,y)) && !holesAround(x,y)) {
                return new int[] {x,y};
            }
        }
    }

    private boolean holesAround(int x, int y) {
        if(!outOfBounds(x+1,y)){
            if( getElementAt(x+1,y) != null && getElementAt(x+1,y).getType() == ElementType.hole) return true;
        }
        if(!outOfBounds(x-1,y)){
            if( getElementAt(x-1,y) != null && getElementAt(x-1,y).getType() == ElementType.hole) return true;
        }
        if(!outOfBounds(x,y+1)){
            if( getElementAt(x,y+1) != null && getElementAt(x,y+1).getType() == ElementType.hole) return true;
        }
        if(!outOfBounds(x,y-1)){
            if( getElementAt(x,y-1) != null && getElementAt(x,y-1).getType() == ElementType.hole) return true;
        }
        return false;
    }

    /**
     *
     * @return retorna false se nao conseguiu gerar o mapa
     * @throws Exception
     */
    private boolean setDoorLocationAndWalls() throws Exception {
        //Place Door
        doorPlacement = random.nextInt(4);
        switch (doorPlacement){
            case 0:
                createDoor(0, random.nextInt(10), true);
                break;

            case 1:
                createDoor(Config.MapX-1, random.nextInt(10),true);
                break;

            case 2:
                createDoor(random.nextInt(10), 0, false);
                break;

            case 3:
                createDoor(random.nextInt(10), Config.MapY-1, false);
                break;
        }


        generateChests();
        // Place Walls


        int x,y;
        int tentativas;
        boolean vertical;
        for (int i = 0; i < Config.NumberOfWalls ; i++) {
            tentativas = 0;
            int maxX = Config.MapX - Config.WallSize;
            int maxY = Config.MapY - Config.WallSize;

            do {
                tentativas++;
                x = random.nextInt(maxX) + (doorPlacement == 0 ? 1 : 0);
                y = random.nextInt(maxY) + (doorPlacement == 2 ? 1 : 0);
                vertical = random.nextBoolean();
                if(tentativas > 1000) return false;
            } while (this.willHaveConflict(x,y,vertical) || this.willGetStuck(x,y,vertical));

        }
        for(int i=0; i<Config.Holes; i++) {
            int[] pos = generateElement();
            this.addElement(pos[0],pos[1],new Hole(pos[0],pos[1]));

        }
        for(int i=0; i<Config.BAG_VALUES.length; i++) {
            int[] pos = generateElement();
            _map[pos[0]][pos[1]] = new Bag(pos[0],pos[1], Config.BAG_VALUES[i]);
        }
        return true;
    }

    private void createDoor(int x,int y, boolean fixed){
        if(fixed){
            for(int i = 0; i < _map[x].length; i++){
                if(i == y) continue;
                addElement(x, i, ElementType.wall);
            }
        } else {
            for(int i = 0; i < _map.length; i++){
                if(i == x) continue;
                addElement(i, y, ElementType.wall);
            }
        }
        //The Door
        addElement(x, y, ElementType.door);

        //Keep track of the element in front of the door
        if(!fixed)
            this.frontDoor = new Position(x  , y == Config.MapY -1 ? y - 1 : 1);
        else
            this.frontDoor = new Position(x == Config.MapX -1 ? x - 1 : 1 ,  y);

    }

    private void removeElement(int x, int y) {
        this._map[x][y] = null;
        this.freeSpace ++;
    }

    private void addElement(int x,int y,ElementType type){
        addElement(x,y, new Element(x,y,type));
    }
    private void addElement(int x,int y,Element e){
        this._map[x][y] = e;
        this.freeSpace --;
    }

    private void putWall(int x,int y,boolean vertical){
        for (int j = 0; j < Config.WallSize; j++) {
            addElement(x,y, ElementType.wall);
            if (vertical) y++;
            else x++;
        }
    }

    private void removeWall(int x,int y,boolean vertical){
        for (int j = 0; j < Config.WallSize; j++) {
            removeElement(x,y);
            if (vertical) y++;
            else x++;
        }
    }


    private boolean willGetStuck(int x,int y,boolean vertical)  {
        if(this.willBlockTheDoor(x,y,vertical))
            return true;

        putWall(x,y, vertical);
        for (int j = 0; j < Config.WallSize; j++) {
            try {
                if(this.floodFill()){
                    return false;
                } else {
                    removeWall(x,y, vertical);
                    return true;
                }
            } catch (Exception e){
                e.printStackTrace();
                System.exit(5);
            }
            if (vertical) y++;
            else x++;
        }
        return false;
    }

    private boolean willBlockTheDoor(int x, int y, boolean vertical) {
        for (int j = 0; j < Config.WallSize; j++) {
            if(this.frontDoor.getX() == x && this.frontDoor.getY() == y)
                return true;
            if (vertical) y++;
            else x++;
        }
        return false;
    }

    private boolean floodFill() throws Exception {
        this.visited = new boolean[Config.MapX][Config.MapY];
        for (int x = 0; x < this._map.length; x++)
            Arrays.fill(this.visited[x], Boolean.FALSE);

        this.total = 0;
        for (int x = 0; x < this._map.length; x++) {
            for (int y = 0; y < this._map[x].length ; y++) {
                if(_map[x][y] == null || _map[x][y].getType() == ElementType.chest){
                    int v = _floodFill(x,y);

                    return this.total == this.freeSpace;
                }
            }
        }
        throw new Exception("No Free Space");
    }

    private int _floodFill(int x, int y ) {
        if(shouldFloadFill(x,y)){
            this.visited[x][y] = true;
            if(shouldFloadFill(x+1,y)) _floodFill(x+1, y);
            if(shouldFloadFill(x-1,y)) _floodFill(x-1, y);
            if(shouldFloadFill(x,y+1)) _floodFill(x, y+1);
            if(shouldFloadFill(x,y-1)) _floodFill(x, y-1);
            this.total = this.total+1;
        }
        return 0;
    }

    private boolean shouldFloadFill(int x, int y) {
        if(!outOfBounds(x,y) && this.visited[x][y] !=true && ( this._map[x][y] == null || _map[x][y].getType() == ElementType.chest) )
            return true;
        return false;
    }

    public boolean outOfBounds(int x, int y) {
        if(x < 0 || y < 0 || x >= Config.MapX || y >= Config.MapY)
            return true;
        return false;
    }


    private boolean willHaveConflict(int x,int y,boolean vertical) {
        for (int j = 0; j < Config.WallSize; j++) {
            if(this._map[x][y] != null) return true;
            if (vertical) y++;
            else x++;
        }
        return false;
    }


    public Element[][] get_map() {
        return _map;
    }

    public Element getWalkableElementAt(int x, int y){
        if (x < 0 || y < 0 || x >= Config.MapX || y >= Config.MapY) {
            return null;
        }

        Element e = getElementAt(x,y);

        if(e != null && e.isWalkableOrHole())
            return e;
        return null;
    }
    public Element getElementAt(int x, int y){
        if(outOfBounds(x,y))
            return null;
        return this._map[x][y];
    }

    public Element getElementAt(Position p){
        if(p != null)
            return getElementAt(p.getX(), p.getY());
        else return null;
    }


    public boolean isDoorClosed() {
        return doorClosed;
    }

    public void setDoorClosed(boolean doorClosed) {
        this.doorClosed = doorClosed;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this._map.length; i++){
            for(int j = 0; j < this._map[i].length; j++){
                if(Agent.getInstance().getPosition().equals(new Position(i,j))) {
                    str.append("A ");
                } else if(_map[i][j] == null) {
                    str.append("_ ");
                } else {
                    switch (_map[i][j].getType()) {
                        case floor:
                            str.append('_');
                            break;
                        case hole:
                            str.append('o');
                            break;
                        case wall:
                            str.append('#');
                            break;
                        case door:
                            if (this.doorClosed) {
                                str.append('D');
                            } else {
                                str.append(']');
                            }
                            break;
                        case chest:
                            str.append('c');
                            break;
                        case bag:
                            str.append('B');
                            break;

                    }
                    str.append(' ');
                }
            }
            str.append('\n');
        }
        return str.toString();
    }

    public void printWithPath(Stack<Position> g){
        ArrayList<Position> p = new ArrayList<>(g);
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this._map.length; i++){
            for(int j = 0; j < this._map[i].length; j++){
                if(Agent.getInstance().getPosition().equals(new Position(i,j))) {
                    str.append("A");
                } else if(p.contains(new Position(i,j))){
                    str.append("*");
                }
                else
                    switch (_map[i][j].getType()) {
                        case floor:
                            str.append('_');
                            break;
                        case hole:
                            str.append('o');
                            break;
                        case wall:
                            str.append('#');
                            break;
                        case door:
                            if (this.doorClosed) {
                                str.append('D');
                            } else {
                                str.append(']');
                            }
                            break;
                        case chest:
                            str.append('c');
                            break;
                        case bag:
                            str.append('B');
                            break;
                }
                str.append(' ');
            }
            str.append('\n');
        }
        System.out.println(str.toString());
    }

    public Bag catchBag(Position position) {
        Bag bg = (Bag) getElementAt(position);
        _map[position.getX()][position.getY()] = new Element(position.getX(), position.getY(), ElementType.floor);
        return bg;
    }

    public void openDoor() {
        this.doorClosed = false;
    }
}
