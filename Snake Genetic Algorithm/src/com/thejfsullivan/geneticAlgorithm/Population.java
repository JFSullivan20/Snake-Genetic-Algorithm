package com.thejfsullivan.geneticAlgorithm;

import java.util.Random;

import com.thejfsullivan.components.Snake;

import processing.core.PApplet;

public class Population {

	Random rand = new Random();

	Snake[] snakes;
	Snake currentLongestSnake;
	int currentBestLength = 0;
	double maxFitness = 0;

	Snake bestEverSnakeFitness;
	Snake bestEverSnakeLength;
	int maxLength = 0;
	public double avgLength = 0;

	int generation = 1;
	double mutationRate;
	boolean showAll = true;

	int populationID = rand.nextInt(10000);

	public Population(PApplet p, int s, int g, double mr, int numSnakes) {
		this.mutationRate = mr;
		snakes = new Snake[numSnakes];
		for (int i = 0; i < numSnakes; i++) {
			snakes[i] = new Snake(p, s, g);
		}
		currentLongestSnake = snakes[0];
	}

	public int getGeneration() {
		return generation;
	}

	public void live() {
		for (Snake snake : snakes) {
			if (snake.isAlive()) {
				snake.look();
				snake.setVelocity();
				snake.move();
				if (snake.isAlive() && (showAll || (snake.equals(currentLongestSnake)))) {
					snake.show();
				}
			}
		}
		setCurrentLongest();
	}

	public void calculateFitness() {
		for (Snake snake : snakes) {
			snake.calculateFitness();
		}
	}

	public void naturalSelection() {
		Snake[] newSnakes = new Snake[snakes.length];

		setBestSnakeFitness();
		setBestSnakeLength();

		newSnakes[0] = bestEverSnakeFitness.clone();
		for (int i = 1; i < newSnakes.length; i++) {
			Snake p1 = selectSnake(null);
			Snake p2 = selectSnake(p1);

			Snake child = p1.crossover(p2);
			child.mutate(mutationRate);
			newSnakes[i] = child.clone();
		}

		snakes = newSnakes;
		currentLongestSnake = snakes[0];

		generation++;
		currentBestLength = 0;
		maxFitness = 0;
	}

	public Snake selectSnake(Snake otherParent) {
		int safetyNet = 0;
		while (true) {
			int index = rand.nextInt(snakes.length);
			Snake snake = snakes[index];
			double r = rand.nextDouble() * maxFitness;
			if (!snake.equals(otherParent) && r < snake.fitness()) {
				return snake;
			}
			safetyNet++;
			if (safetyNet > 10000) {
				System.out.println("safetynet " + snake.fitness());
				return snake;
			}
		}
	}

	public boolean finished() {
		for (Snake snake : snakes) {
			if (snake.isAlive()) {
				return false;
			}
		}
		return true;
	}

	public void mutate() {
		for (Snake snake : snakes) {
			snake.mutate(mutationRate);
		}
	}

	public void setCurrentLongest() {
		if (!finished()) {
			int maxLength = 0;
			int maxIndex = 0;
			for (int i = 0; i < snakes.length; i++) {
				if (snakes[i].isAlive() && snakes[i].length > maxLength) {
					maxLength = snakes[i].length;
					maxIndex = i;
				}
			}

			currentBestLength = Math.max(currentBestLength, maxLength);

			if (!currentLongestSnake.isAlive() || maxLength > currentLongestSnake.length + 2) {
				currentLongestSnake = snakes[maxIndex];
			}
		}
	}

	public void setBestSnakeFitness() {
		// calculate max fitness
		int maxIndex = 0;
		for (int i = 0; i < snakes.length; i++) {
			if (snakes[i].fitness() > maxFitness) {
				maxFitness = snakes[i].fitness();
				maxIndex = i;
			}
		}
		// if best this gen is better than the best ever then set the best ever as the
		// best this gen
		if (bestEverSnakeFitness == null || maxFitness > bestEverSnakeFitness.fitness()) {
			bestEverSnakeFitness = snakes[maxIndex];
		}
	}

	public void setBestSnakeLength() {
		// calculate max fitness
		int maxIndex = 0;
		int totalLength = 0;
		for (int i = 0; i < snakes.length; i++) {
			totalLength += snakes[i].length;
			if (snakes[i].length > maxLength) {
				maxLength = snakes[i].length;
				maxIndex = i;
			}
		}
		avgLength = totalLength / snakes.length;
		// if best this gen is better than the best ever then set the best ever as the
		// best this gen
		if (bestEverSnakeLength == null || maxLength > bestEverSnakeLength.length) {
			bestEverSnakeLength = snakes[maxIndex];
		}
	}

}
