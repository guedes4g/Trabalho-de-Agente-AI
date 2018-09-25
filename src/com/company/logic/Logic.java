package com.company.logic;

import com.company.helpers.AStar;
import com.company.helpers.Map;
import com.company.models.Agent;
import com.company.models.Element;
import com.company.models.Positon;

import java.util.Stack;

public class Logic {

    public void run(){

        Map map = Map.getInstance();
        
        System.out.println(map);
        /*Element start = map.getElementAt(2,2);
        Element end = map.getElementAt(7,7);
        Stack<Positon> result = AStar.run(start, end);
        System.out.println(result);
        map.printWithPath(result);*/
        
    }
}
