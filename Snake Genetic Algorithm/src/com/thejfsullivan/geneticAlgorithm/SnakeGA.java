package com.thejfsullivan.geneticAlgorithm;

import processing.core.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.thejfsullivan.components.*;

public class SnakeGA extends PApplet {

	Snake snake;
	Food food;
	int size = 20;
	public int growthConstant = 1;

	PFont font;
	float fontSize = 24;
	int highScore = 0;
	String highScoreFile = "Scores/HighScores.txt";

	int lifetime;
	int lifecycle;
	Population population;
//	Snake recordSnake;
	int bestLength = 0;
	double bestFitness = 0;

	int rate = 60;

	// The argument passed to main must match the class name
	public static void main(String[] args) {
		PApplet.main(new String[] { SnakeGA.class.getName() });
	}

	// method used only for setting the size of the window
	public void settings() {
		size(1000, 540);
	}

	public void setup() {
		snake = new Snake(this, size, growthConstant);
		food = new Food(this, size);
		font = createFont("Fonts/joystix monospace.ttf", 16);
		textFont(font);
		highScore = getHighScore(highScoreFile);

		// Create a population with a mutation rate, and population max
		double mutationRate = 0.15;
		population = new Population(this, size, growthConstant, mutationRate, 500);

//        recordSnake = null;
	}

	public void draw() {
		background(0);
		frameRate(rate);

		// If the generation hasn't ended yet
		if (!population.finished()) {
			population.live();
		} else {
			// Otherwise a new generation
			population.calculateFitness();
			population.naturalSelection();
		}

		if (population.currentLongestSnake.getBrain().inputs != null)
			population.currentLongestSnake.getBrain().draw(550, 70, 400, 450);

		// Display some info
		fill(255);
		textAlign(LEFT);
		if (population.bestEverSnakeFitness != null) {
			bestLength = population.bestEverSnakeLength.length;
			bestFitness = population.bestEverSnakeFitness.fitness();
		}
		text("Generation #:" + population.getGeneration(), 5, 18);
		text("Frame Rate:" + Float.toString(frameRate).substring(0, Math.min(5, Float.toString(frameRate).length())), 5, 36);
		textAlign(CENTER);
		text("Longest Ever:" + bestLength, width / 2, 18);
		text("Avg Length:" + Double.toString(population.avgLength).substring(0, Math.min(5, Double.toString(population.avgLength).length())), width / 2, 36);
		textAlign(RIGHT);
		text("Max Fitness:" + String.format("%6.3e", bestFitness), width - 5, 18);
		text("Avg Fitness:" + String.format( "%6.3e", population.avgFitness), width - 5, 36);
		

		// block border
    	fill(0,0,255);
    	for (int i = 1; i < 500 / size; i++ ) {
    		rect(i * size, size * 3, size, size);
    		rect(i * size, height - size, size, size);
    		rect(0, (i + 2) * size, size, size);
    		rect(500 - size, (i + 2) * size, size, size);
    	}
		
		// lined border
		stroke(0, 0, 255);
		strokeWeight(1);
		// TODO: fix this size thingy
		line(size, size * 4, size, height - size);
		line(size, size * 4, 500 - size, size * 4);
		line(size, height - size, 500 - size, height - size);
		line(500 - size, size * 4, 500 - size, height - size);
		stroke(0);
    	
//    	if (snake.eat(food)) {
//    		do {
//    			food.randomizeLocation();
//    		} while (snake.getTail().contains(food) || snake.getHead().equals(food));
//    	}
//    	food.show();
//    	if (!snake.move()) { // snake dies
//    		snake.showDeath();
//    		updateHighScore(highScoreFile);
//    	} else {
//        	snake.show();
//    	}
//    	
//    	highScore = snake.displayScore(highScore, fontSize);
	}

	@Override
	public void keyPressed() {
		switch (keyCode) {
//		case UP:
//			snake.changeDirection(0, -1);
//			break;
//		case DOWN:
//			snake.changeDirection(0, 1);
//			break;
		case LEFT:
			rate -= 5;
			break;
		case RIGHT:
			rate += 5;
			break;
		case 32: // space bar
			population.showAll = !population.showAll;
			break;

		default:
			System.out.println("KeyPressed Default");
			break;
		}
	}

	private int getHighScore(String string) {
		File file = new File(string);
		Scanner input = null;
		int n = -1;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File unable to load high score");
		}
		if (input != null && input.hasNextInt()) {
			n = input.nextInt();
		}
		input.close();
		return n;
	}

//    private void updateHighScore(String string) {
//    	try {
//			FileWriter fw = new FileWriter(string);
//			fw.write(highScore + "\n");
//			fw.close();
//		} catch (IOException e) {
//			System.out.println("Unable to update high score");
//		}
//    }

}
