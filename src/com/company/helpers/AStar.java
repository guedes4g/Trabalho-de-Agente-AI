package com.company.helpers;

import com.company.models.Element;
import com.company.models.Positon;

import java.util.*;

public class AStar {

    public static Stack<Positon> run(Element start, Element to){
        ArrayList<Positon> open = new ArrayList<>();
        Set<Positon> closed = new HashSet<>();
        HashMap<Positon, Integer> g = new HashMap<>();
        HashMap<Positon, Integer> f = new HashMap<>();
        HashMap<Positon, Positon> path = new HashMap<>();

        Map map = Map.getInstance();

        int gscore, fscore;


        open.add(start);
        g.put(start, 0);
        f.put(start, start.manhattanDistance(to));

        Positon current = null;

        while(!open.isEmpty()){

            current = open.get(0);

            if(current.isNextTo(to)){
                path.put(to, current);
                return buildPath(path, to);
            }
            open.remove(0);
            closed.add(current);


            for(Positon pos : lookAround(map, current)){

                if(closed.contains(pos)) continue;

                gscore = g.get(current) + 1;

                if(!open.contains(pos)){
                    open.add(pos);
                    f.put(pos, Integer.MAX_VALUE);
                }
                else if(gscore >= g.get(pos))
                    continue;

                path.put(pos, current);
                g.put(pos, gscore);

                fscore = g.get(pos) + pos.manhattanDistance(to);
                f.put(pos, fscore);

                open.sort((l,r)->{
                    if (f.get(l) < f.get(r))
                        return -1;
                    if (f.get(r) < f.get(l))
                        return 1;
                    return 0;
                });
            }
        }
        return null;
    }

    private static Stack<Positon> buildPath(HashMap<Positon, Positon> path, Element to){
        Stack<Positon> totalPath = new Stack<>();

        Positon current = to;

        while (current != null) {
            Positon previous = current;
            current = path.get(current);

            if (current != null)
                totalPath.push(previous);
        }

//        //E já remove a última
//        totalPath.remove(to);

        return totalPath;
    }

    private static List<Positon> lookAround(Map map, Positon current){
        List<Positon> result = new ArrayList<>();

        addAt(current, 1,0, result, map);
        addAt(current, -1,0, result, map);
        addAt(current, 0,1, result, map);
        addAt(current, 0,-1, result, map);
        return result;
    }

    private static void addAt(Positon p, int dx, int dy ,List<Positon> result, Map map) {
        Element e = map.getWalkableElementAt(p.getX()+dx,p.getY()+dy);
        if(e != null)
            //pula
            if(e.getType() == ElementType.hole)
                addAt(p, dx*2, dy*2, result, map);
            else
                result.add(e);
    }


}
