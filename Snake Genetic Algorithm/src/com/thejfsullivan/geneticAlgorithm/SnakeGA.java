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
	public int growthConstant = 4;
	
	PFont font;
	float fontSize = 24;
	int highScore = 0;
	String highScoreFile = "Scores/HighScores.txt";

    // The argument passed to main must match the class name
    public static void main(String[] args) {
        PApplet.main(new String[] { SnakeGA.class.getName() });
    }

    // method used only for setting the size of the window
    public void settings(){
        size(500, 500);
    }

    public void setup(){
        frameRate(15);
        snake = new Snake(this, size, growthConstant);
        food = new Food(this, size);
        font = createFont("Fonts/joystix monospace.ttf", 16);
        textFont(font);
        highScore = getHighScore(highScoreFile);
    }

	@SuppressWarnings("unlikely-arg-type")
	public void draw(){
    	background(0);
    	
    	// show border
    	fill(255,0,0);
    	for (int i = 1; i < width / size; i++ ) {
    		rect(i * size, size, size, size);
    		rect(i * size, height - size, size, size);
    		rect(0, i * size, size, size);
    		rect(width - size, i * size, size, size);
    	}
    	
    	if (snake.eat(food)) {
    		do {
    			food.randomizeLocation();
    		} while (snake.getTail().contains(food) || snake.getHead().equals(food));
    	}
    	food.show();
    	if (!snake.move()) { // snake dies
    		snake.showDeath();
    		updateHighScore(highScoreFile);
    	} else {
        	snake.show();
    	}
    	
    	highScore = snake.displayScore(highScore, fontSize);
    }
    
    @Override
    public void keyPressed() {
    	switch (keyCode) {
		case UP:
			snake.changeDirection(0, -1);
			break;
		case DOWN:
			snake.changeDirection(0, 1);
			break;
		case LEFT:
			snake.changeDirection(-1, 0);
			break;
		case RIGHT:
			snake.changeDirection(1, 0);
			break;
		case 32: // space bar
			setup();
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
    
    private void updateHighScore(String string) {
    	try {
			FileWriter fw = new FileWriter(string);
			fw.write(highScore + "\n");
			fw.close();
		} catch (IOException e) {
			System.out.println("Unable to update high score");
		}
    }
	
	
	
}
