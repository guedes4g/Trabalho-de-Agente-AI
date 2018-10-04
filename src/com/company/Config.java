package com.company;

public class Config {
    public static boolean DEBUG = true;

    //MAP DIMENSIONS
    public static int MapX = 10;
    public static int MapY = 10;

    //MAP OBJECTS
    public static int Holes = 5;
    public static int NumberOfChests = 4;
    public static int NumberOfWalls = 4;
    public static int WallSize = 5;
    public static int[] BAG_VALUES = {45,35,21,19,40,5,15,25,2,3,30,22,20,18,10,10};//{47,15,7,11,2,3,2,38,35,21,16,40,3,32,17,31};//{5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5};

    //POINTS
    public static int Point_Coin = 10;
    public static int Point_Jump = 30;
    public static int Point_Door = 300;
    public static int Point_Exit = 30;

    //EXECUTE SLOWER
    public static boolean Should_Execute_Slower = false;
    public static int timeout_ms = 500;
}
