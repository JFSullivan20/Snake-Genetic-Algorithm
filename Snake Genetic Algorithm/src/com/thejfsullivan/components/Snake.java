package com.thejfsullivan.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.thejfsullivan.geneticAlgorithm.NeuralNet;

import processing.core.*;
import processing.data.Table;
import processing.data.TableRow;

public class Snake {

	PApplet p;
	int minX, minY, maxX, maxY;

	BodyPart head;
	ArrayList<BodyPart> tail;

	NeuralNet brain;
	double[] vision = null; // input for neural net
	double[] decision; // output of neural net
	int movesLeft;
	int timeAlive = 0;
	double fitness = 0;
	double functionConstant;

	Food food;

	int xVel = 1;
	int yVel = 0;
	int size;
	int growthConstant;
	public int length; // length of tail
	boolean isAlive = true;
	Opacity o = Opacity.MIN;

	enum Opacity {
		MIN, FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH, SEVENTH, EIGHTH, NINTH, TENTH, ELEVENTH, MAX {
			@Override
			public Opacity next() {
				return values()[0];
			}
		};

		public Opacity next() {
			return values()[ordinal() + 1];
		}
	}

	// score variables
	double score = 1;
	int movesTillFruit = 0;
	double scoreConstant = 100;

	public Snake(PApplet p) {
		this(p, 10);
	}

	public Snake(PApplet p, int s) {
		this(p, s, 1);
	}

	public Snake(PApplet p, int s, int g) {
		this.p = p;
		this.size = s;
		head = new BodyPart(s * 4, 4 * s);
		tail = new ArrayList<>();
		growthConstant = g;
		food = new Food(p, size);

		minX = size;
		maxX = 500 - 2 * size; // TODO: fix this size thingy
		minY = 2 * size;
		maxY = p.height - 2 * size;
		movesLeft = ((maxX - minX) / size) * ((maxY - minY) / size) / 3;
		brain = new NeuralNet(p, 28, 19, 10, 4);
	}

	public NeuralNet getBrain() {
		return brain;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public BodyPart getHead() {
		return head;
	}

	public ArrayList<BodyPart> getTail() {
		return tail;
	}

	public void show() {
		// for white
		p.fill(255);
		for (BodyPart b : tail) {
			p.rect(b.x, b.y, size, size);
		}
		p.fill(255);
		p.rect(head.x, head.y, size, size);

		food.show();
	}

	public void showDeath() {
		p.fill(255, 0, 0);
		for (BodyPart b : tail) {
			p.rect(b.x, b.y, size, size);
		}
		p.rect(head.x, head.y, size, size);
	}

	// change direction of the snake
	public void setVelocity() {
		// output of the neural network
		decision = brain.output(vision);

		double max = 0;
		int maxIndex = 0;
		for (int i = 0; i < decision.length; i++) {
			if (max < decision[i]) {
				max = decision[i];
				maxIndex = i;
			}
		}
		// set the velocity based on this decision
		if (maxIndex == 0) { // go up
			if (xVel == 0 && yVel == 1) // if it was going down
				isAlive = false;
			xVel = 0;
			yVel = -1;
		} else if (maxIndex == 1) { // go left
			if (xVel == 1 && yVel == 0) // if it was going right
				isAlive = false;
			xVel = -1;
			yVel = 0;
		} else if (maxIndex == 2) { // go down
			if (xVel == 0 && yVel == -1) // if it was going up
				isAlive = false;
			xVel = 0;
			yVel = 1;
		} else { // go right
			if (xVel == -1 && yVel == 0) // if it was going left
				isAlive = false;
			xVel = 1;
			yVel = 0;
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public void move() {

		if (length > 0) {
			if (length == tail.size() && !tail.isEmpty()) {
				tail.remove(0);
			}
			tail.add(new BodyPart(head.x, head.y));
		}

		head.x += xVel * size;
		head.y += yVel * size;

		// head.x = PApplet.constrain(head.x, size, p.width - 2 * size);
		// head.y = PApplet.constrain(head.y, size * 2, p.height - 2 * size);

		movesTillFruit++;
		movesLeft--;
		timeAlive++;

		if (head.equals(food)) {
			eat();
		}

		if (death() || movesLeft < 0) {
			isAlive = false;
		}
	}

	@SuppressWarnings("unlikely-arg-type")
	public void eat() {
		score += ((length * length + 1) / (movesTillFruit / scoreConstant));
		movesTillFruit = 0;
		length += growthConstant; // grow the snake

		// randomize food location
		while (head.equals(food) || tail.contains(food)) {
			food.randomizeLocation();
		}

		// increase lifespan
		// TODO: mess with this number
		movesLeft += 75;
	}

	public boolean death() {
		if (head.x < minX || head.x > maxX || head.y < minY || head.y > maxY) {
			return true;
		}

		// returns true if head is colliding with the tail
		return tail.contains(head);
	}

	// mutates the neural net
	public void mutate(double mutationRate) {
		brain.mutate(mutationRate);
	}

	public double fitness() {
		return fitness;
	}

	public void calculateFitness() {
		// fitness = timeAlive + Math.pow(2, length + 1) * score;
		fitness = timeAlive + (Math.pow(2, length) + Math.pow(length, 2.1) * 500)
				- (Math.pow(length, 1.2) * Math.pow(0.25 * timeAlive, 1.3));
	}

	public void look() {
		vision = new double[28];
		// look left
		double[] tempValues = lookInDirection(new PVector(-1, 0));
		vision[0] = tempValues[0];
		vision[1] = tempValues[1];
		vision[2] = tempValues[2];
		// look left/up
		tempValues = lookInDirection(new PVector(-1, -1));
		vision[3] = tempValues[0];
		vision[4] = tempValues[1];
		vision[5] = tempValues[2];
		// look up
		tempValues = lookInDirection(new PVector(0, -1));
		vision[6] = tempValues[0];
		vision[7] = tempValues[1];
		vision[8] = tempValues[2];
		// look up/right
		tempValues = lookInDirection(new PVector(1, -1));
		vision[9] = tempValues[0];
		vision[10] = tempValues[1];
		vision[11] = tempValues[2];
		// look right
		tempValues = lookInDirection(new PVector(1, 0));
		vision[12] = tempValues[0];
		vision[13] = tempValues[1];
		vision[14] = tempValues[2];
		// look right/down
		tempValues = lookInDirection(new PVector(1, 1));
		vision[15] = tempValues[0];
		vision[16] = tempValues[1];
		vision[17] = tempValues[2];
		// look down
		tempValues = lookInDirection(new PVector(0, 1));
		vision[18] = tempValues[0];
		vision[19] = tempValues[1];
		vision[20] = tempValues[2];
		// look down/left
		tempValues = lookInDirection(new PVector(-1, 1));
		vision[21] = tempValues[0];
		vision[22] = tempValues[1];
		vision[23] = tempValues[2];

		vision[24] = (food.x - head.x) > 0 ? 1 : 0; // to the right -> 1
		vision[25] = (food.x - head.x) < 0 ? 1 : 0; // to the left -> 1
		vision[26] = (food.y - head.y) > 0 ? 1 : 0; // below the head -> 1
		vision[27] = (food.y - head.y) < 0 ? 1 : 0; // above the head -> 1
	}

	public double[] lookInDirection(PVector direction) {
		double[] visionInDirection = new double[3];

		PVector position = new PVector(head.x, head.y);
		boolean foundTail = false;
		boolean foodIsFound = false;
		int distance = 0;

		direction.mult(size);

		position.add(direction);
		distance++;

		while (position.x >= minX && position.x <= maxX && position.y >= minY && position.y <= maxY) {

			// check for food at the position
			if (!foodIsFound && position.x == food.x && position.y == food.y) {
				visionInDirection[0] = 1;
				foodIsFound = true;
			}

			if (!foundTail && tail.contains(new BodyPart((int) position.x, (int) position.y))) {
				foundTail = true;
				visionInDirection[1] = 1 / distance;
			}

			position.add(direction);
			distance++;
		}

		// if distance is 1 away, then set to 1 otherwise put through function
		visionInDirection[2] = 1 / distance;

		return visionInDirection;
	}

	public Snake crossover(Snake s) {
		Snake child = new Snake(p, size, growthConstant);
		child.brain = brain.crossover(s.brain);
		return child;
	}

	public Snake clone() {
		Snake clone = new Snake(p, size, growthConstant);
		clone.brain = brain.clone();
		clone.isAlive = true;
		return clone;
	}

	public class BodyPart extends Cell {
		private BodyPart(int x, int y) {
			super(x, y);
		}
	}

	public int displayScore(int highScore, float fontSize) {
		p.fill(255);
		if (score >= highScore) {
			incrementOpacity();
		}
		p.textAlign(PConstants.LEFT);
		p.text("Score: " + Math.round(score), size / 2, size * 3 / 4);
		String highScoreString = "High Score: " + Math.round(Math.max(score, highScore));
		p.textAlign(PConstants.RIGHT);
		p.text(highScoreString, p.width - size / 2, size * 3 / 4);
		return (int) Math.round(Math.max(score, highScore));
	}

	private void incrementOpacity() {
		o = o.next();
		switch (o) {
		case MIN:
			p.fill(255, (float) 0);
			break;
		case FIRST:
			p.fill(255, (float) 21.25);
			break;
		case SECOND:
			p.fill(255, (float) 42.5);
			break;
		case THIRD:
			p.fill(255, (float) 63.75);
			break;
		case FOURTH:
			p.fill(255, (float) 85);
			break;
		case FIFTH:
			p.fill(255, (float) 106.25);
			break;
		case SIXTH:
			p.fill(255, (float) 127.5);
			break;
		case SEVENTH:
			p.fill(255, (float) 148.75);
			break;
		case EIGHTH:
			p.fill(255, (float) 170);
			break;
		case NINTH:
			p.fill(255, (float) 191.25);
			break;
		case TENTH:
			p.fill(255, (float) 212.5);
			break;
		case ELEVENTH:
			p.fill(255, (float) 233.75);
			break;
		case MAX:
			p.fill(255, (float) 255);
			break;

		default:
			p.fill(255, (float) 255);
			break;
		}
	}

	// saves the snake to a file by converting it to a table
	public void saveSnake(int snakeNo, int score, int popID) throws IOException {
		// save the snakes top score and its population id
		Table snakeStats = new Table();
		snakeStats.addColumn("Top Score");
		snakeStats.addColumn("PopulationID");
		TableRow tr = snakeStats.addRow();
		tr.setFloat(0, score);
		tr.setInt(1, popID);

		snakeStats.save(new File("data/SnakeStats" + snakeNo + ".csv"), ".csv");
		// save snakes brain
		brain.netToTable().save(new File("data/Snake" + snakeNo + ".csv"), ".csv");
	}

	// return the snake saved in the parameter position
	public Snake loadSnake(int snakeNo, PApplet p, int s, int g) throws IOException {

		Snake load = new Snake(p, s, g);
		Table t = new Table(new File("data/Snake" + snakeNo + ".csv"));

		load.brain.tableToNet(t);
		return load;
	}

}
