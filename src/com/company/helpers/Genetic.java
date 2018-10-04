package com.company.helpers;

import java.util.*;
import java.util.Map;

import com.company.Config;
import com.company.models.Bag;

public class Genetic {
	
	private static final Random RANDOM = new Random();
	private static final int NUMBER_OF_CHESTS = Config.NumberOfChests;
	private static final int POPULATION_SIZE = 5;
	private static final int MUTATION_PROBABILITY = 5;
	private static final int CONVERGENCE_FACTOR = 4;
	
	public static int[] solution(List<Bag> bags) {
	    boolean foundSolution = false;
	    while (!foundSolution) {
            int numberOfBags = bags.size();
            int[][] population = initialPopulation(numberOfBags);
            int coinsPerChest = coinsPerChest(bags);
            while (true) {
                int[] fitnesses = fitnesses(bags, coinsPerChest, population);
                int bestSolution = elitism(fitnesses);
                if (fitnesses[bestSolution] == 0)
                    return population[bestSolution];
                population = generatePopulation(population, fitnesses, bestSolution);
                if (converged(population))
                    break;
                if (RANDOM.nextInt(100) < MUTATION_PROBABILITY)
                    mutation(population);
            }
        }
        return null;
	}
	
	private static int[][] generatePopulation(int[][] population, int[] fitnesses, int bestSolution) {
		int[][] newPopulation = new int[POPULATION_SIZE][population[0].length];
		newPopulation[0] = population[bestSolution].clone();
		int[] dad = population[tournment(fitnesses)];
		int[] mom = population[tournment(fitnesses)];
		int[][] children = reproduction(dad, mom);
		newPopulation[1] = children[0];
		newPopulation[2] = children[1];
		dad = population[tournment(fitnesses)];
		mom = population[tournment(fitnesses)];
		children = reproduction(dad, mom);
		newPopulation[3] = children[0];
		newPopulation[4] = children[1];
		return newPopulation;
	}
	
	private static int[][] initialPopulation(int numberOfBags) {
		int[][] population = new int[POPULATION_SIZE][numberOfBags];
		for(int i=0; i<POPULATION_SIZE; i++)
			population[i] = generateSolution(numberOfBags);
		return population;
	}
	
	private static int[] generateSolution(int numberOfBags) {
		int[] solution = new int[numberOfBags];
		for(int i=0; i<numberOfBags; i++)
			solution[i] = RANDOM.nextInt(NUMBER_OF_CHESTS);
		return solution;
	}
	
	private static void mutation(int[][] population) {
		population[RANDOM.nextInt(POPULATION_SIZE - 1) + 1]
				[RANDOM.nextInt(population[0].length)]
						= RANDOM.nextInt(NUMBER_OF_CHESTS);
	}
	
	private static int[][] reproduction(int[] dad, int[] mom) {
		int sizeOfSolution = dad.length;
		int[][] children = new int[2][sizeOfSolution];
		int half = sizeOfSolution/2;
		for(int i=0; i<half; i++) {
			children[0][i] = dad[i];
			children[1][i] = mom[i];
		}
		for(int i=half; i<sizeOfSolution; i++) {
			children[0][i] = mom[i];
			children[1][i] = dad[i];
		}
		return children;
	}
	
	private static int tournment(int[] fitnesses) {
		int solution1 = RANDOM.nextInt(fitnesses.length);
		int solution2 = RANDOM.nextInt(fitnesses.length);
		return (fitnesses[solution1] < fitnesses[solution2])? solution1: solution2;
	}
	
	private static int elitism(int[] fitnesses) {
		int fitness = fitnesses[0];
		int fittest = 0;
		for(int i=1; i<fitnesses.length; i++) {
			if(fitnesses[i] < fitness) {
				fitness = fitnesses[i];
				fittest = i;
			}
		}
		return fittest;
	}
	
	private static int[] fitnesses(List<Bag> bags, int coinsPerChest, int[][] population) {
		int[] fitnesses = new int[population.length];
		for(int i=0; i<fitnesses.length; i++)
			fitnesses[i] = fitness(bags, coinsPerChest, population[i]);
		return fitnesses;
	}
	
	private static int fitness(List<Bag> bags, int coinsPerChest, int[] solution) {
		int chest0 = 0, chest1 = 0, chest2 = 0, chest3 = 0;
		for(int i=0; i<solution.length; i++) {
			switch(solution[i]) {
			case 0:
				chest0 += bags.get(i).getValue();
				break;
			case 1:
				chest1 += bags.get(i).getValue();
				break;
			case 2:
				chest2 += bags.get(i).getValue();
				break;
			case 3:
				chest3 += bags.get(i).getValue();
				break;
			}
		}
		return Math.abs(chest0 - coinsPerChest) + Math.abs(chest1 - coinsPerChest) +
				Math.abs(chest2 - coinsPerChest) + Math.abs(chest3 - coinsPerChest);
	}
	
	private static int coinsPerChest(List<Bag> bags) {
		int totalNumOfCoins = 0;
		for(Bag bag: bags)
			totalNumOfCoins += bag.getValue();
		return totalNumOfCoins/NUMBER_OF_CHESTS;
	}

	private static boolean converged(int[][] population) {
		Map<String, Integer> solutions = new HashMap<>();
		for (int[] solution : population) {
		    String key = generateKey(solution);
			Integer occurrences = solutions.get(key);
			solutions.put(key, occurrences == null ? 1 : ++occurrences);
		}
		for(Integer occurrences: solutions.values()) {
			if(occurrences == CONVERGENCE_FACTOR) {
                return true;
            }
		}
		return false;
	}

    /**
     * Pseudo código nao otimizado para geraćão de chave de um array
     * @param solution
     * @return key (String)
     */
	private static String generateKey(int[] solution){
	    StringBuilder key = new StringBuilder();
        for (int i = 0; i < solution.length; i++) {
            key.append(solution[i]);
            key.append(';');
        }
        return key.toString();
    }
	
}
