package com.company.helpers;

import com.company.Config;
import com.company.models.Agent;
import com.company.models.Bag;
import com.company.models.Chest;
import com.company.models.Element;
import com.company.models.Hole;
import com.company.models.Positon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

public class Map {
    private static Map INSTANCE =  new Map();
    private Element _map[][] = new Element[Config.MapX][Config.MapY];
    private int freeSpace = Config.MapX * Config.MapY;
    private boolean doorClosed = true;
    private Positon frontDoor;
    private boolean visited[][];
    private int total;
    private Random random;
    private int doorPlacement;

    private Map(){
        this.generateMap();
    }

    public static synchronized Map getInstance(){
        return INSTANCE;
    }
    
    private boolean generateChest(int x, int y) {
        if(_map[x][y] == null) {
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
                    valido = generateChest(1, random.nextInt(10));
                } while(!valido);
                break;

            case 1:
                do {
                    valido = generateChest(8, random.nextInt(10));
                } while(!valido);
                break;

            case 2:
                do {
                    valido = generateChest(random.nextInt(10), 1);
                } while(!valido);
                break;

            case 3:
                do {
                    valido = generateChest(random.nextInt(10), 8);
                } while(!valido);
                break;
        }
        }
    }

    private void generateMap() {
        random = new Random();
        this.setDoorLocationAndWalls();
        //TODO put objects
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
            if(_map[x][y] == null) {
                return new int[] {x,y};
            }
        }
    }

    private void setDoorLocationAndWalls() {
        //Place Door
        doorPlacement = random.nextInt(4);
        switch (doorPlacement){
            case 0:
                createDoor(0, random.nextInt(10), true);
                break;

            case 1:
                createDoor(9, random.nextInt(10),true);
                break;

            case 2:
                createDoor(random.nextInt(10), 0, false);
                break;

            case 3:
                createDoor(random.nextInt(10), 9, false);
                break;
        }
        

        generateChests();
        // Place Walls
        
        for (int i = 0; i < Config.MapX; i++) {
            for (int j = 0; j < Config.MapY; j++) {
                Element e = _map[i][j];
                System.out.println("i: j: " + e != null ? e.toString() : "");
            }
        }
        
        System.exit(0);

        int x,y;
        boolean vertical;
        for (int i = 0; i < Config.NumberOfWalls ; i++) {
            int maxX = Config.MapX/2;
            int maxY = Config.MapY/2;

            do {
                x = random.nextInt(maxX) + (doorPlacement == 0 ? 1 : 0);
                y = random.nextInt(maxY) + (doorPlacement == 2 ? 1 : 0);
                vertical = random.nextBoolean();
            } while (this.willHaveConflict(x,y,vertical) || this.willGetStuck(x,y,vertical));

        }
        System.out.println("6");
        
        for(int i=0; i<Config.BAG_VALUES.length; i++) {
            int[] pos = generateElement();
            _map[pos[0]][pos[1]] = new Bag(pos[0],pos[1], Config.BAG_VALUES[i]);
        }
        
        for(int i=0; i<Config.Holes; i++) {
            int[] pos = generateElement();
            _map[pos[0]][pos[1]] = new Hole(pos[0],pos[1]);
        }
        
    }

    private void createDoor(int x,int y, boolean fixed){
        if(fixed){
            for(int i = 0; i < _map.length; i++){
                if(i == y) continue;
                addElement(x, i, ElementType.wall);
            }
        } else {
            for(int i = 0; i < _map[x].length; i++){
                if(i == x) continue;
                addElement(i, y, ElementType.wall);
            }
        }
        //The Door
        addElement(x, y, ElementType.door);

        //Keep track of the element in front of the door
        if(!fixed)
            this.frontDoor = new Positon(x  , y == Config.MapY -1 ? y - 1 : 1);
        else
            this.frontDoor = new Positon(x == Config.MapX -1 ? x - 1 : 1 ,  y);

    }

    private void removeElement(int x, int y) {
        this._map[x][y] = null;
        this.freeSpace ++;
    }

    private void addElement(int x,int y,ElementType type){
        this._map[x][y] = new Element(x,y,type);
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
                if(_map[x][y] == null){
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
        if(!outOfRaster(x,y) && this.visited[x][y] !=true && this._map[x][y] == null )
            return true;
        return false;
    }

    public boolean outOfRaster(int x, int y) {
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
        
        if(e != null && e.isWalkable())
            return e;
        return null;
    }

    public Element getElementAt(int x, int y){
        if(outOfRaster(x,y))
            return null;
        return this._map[x][y];
    }
    public Element getElementAt(Positon p){
        return getElementAt(p.getX(), p.getY());
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
                if(Agent.getInstance().getPositon().equals(new Positon(i,j))) {
                    str.append('A');
                } else
                switch (_map[i][j].getType()){
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
                        str.append('D');
                        break;
                }
            }
            str.append('\n');
        }
        return str.toString();
    }

    public void printWithPath(Stack<Positon> g){
        ArrayList<Positon> p = new ArrayList<>(g);
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < this._map.length; i++){
            for(int j = 0; j < this._map[i].length; j++){
                if(p.contains(new Positon(i,j))){
                    str.append('*');
                }
                else
                switch (_map[i][j].getType()){
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
                        str.append('D');
                        break;
                }
            }
            str.append('\n');
        }
        System.out.println(str.toString());
    }
}
