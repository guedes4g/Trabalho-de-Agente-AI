package com.company.helpers;

import com.company.Config;
import com.company.models.Agent;
import com.company.models.Element;
import com.company.models.Position;

import java.util.*;

public class AStar {



    public static Stack<Position> run(Element start, Element to){
        ArrayList<Position> open = new ArrayList<>();
        Set<Position> closed = new HashSet<>();
        HashMap<Position, Integer> g = new HashMap<>();
        HashMap<Position, Integer> f = new HashMap<>();
        HashMap<Position, Position> path = new HashMap<>();

        Map map = Map.getInstance();

        int gscore, fscore;


        open.add(start);
        g.put(start, 0);
        f.put(start, start.manhattanDistance(to));

        Position current = null;

        while(!open.isEmpty()){

            current = open.get(0);

            if(current.isNextTo(to)){
                path.put(to, current);
                return buildPath(path, to, map);
            }
            open.remove(0);
            closed.add(current);


            for(Position pos : lookAround(map, current)){

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

                fscore = g.get(pos) +
                        pos.manhattanDistance(to);
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

    private static Stack<Position> buildPath(HashMap<Position, Position> path, Element to, Map map){
        ArrayList<Position> totalPath = new ArrayList<>();
        Stack<Position> result = new Stack<>();
        Position current = to;

        while (current != null && current != path.get(current)) {
            Position previous = current;
            current = path.get(current);
            if (current != null){
                 totalPath.add(previous);
            }
        }
        for(int p = 0; p < totalPath.size(); p++){
            if(map.getElementAt(totalPath.get(p)).getType() == ElementType.hole) {
                Agent.getInstance().addPoints(Config.Point_Jump);
                continue;
            }
            result.push(totalPath.get(p));
        }

        return result;
    }

    private static List<Position> lookAround(Map map, Position current){
        List<Position> result = new ArrayList<>();

        addAt(current, 1,0, result, map);
        addAt(current, -1,0, result, map);
        addAt(current, 0,1, result, map);
        addAt(current, 0,-1, result, map);
        return result;
    }

    private static void addAt(Position p, int dx, int dy , List<Position> result, Map map) {
        Element e = map.getWalkableElementAt(p.getX()+dx,p.getY()+dy); // returns hole as well


        if(e != null) {
            result.add(e);
        }
    }


}
