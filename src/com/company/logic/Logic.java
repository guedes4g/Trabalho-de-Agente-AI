package com.company.logic;

import com.company.helpers.Map;
import com.company.models.Agent;

public class Logic {

    public void run(){

        Map map = Map.getInstance();
        Agent agent = Agent.getInstance();

        agent.setPosition(
                map.getFreePosition()
        );
        agent.start();
        System.out.println("Pontos " + agent.getPontuacao());
    }
}
