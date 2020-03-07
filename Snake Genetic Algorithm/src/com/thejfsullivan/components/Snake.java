package com.thejfsullivan.components;

import java.util.ArrayList;
//import java.util.Random;

import processing.core.*;

public class Snake {
	
	PApplet p;
//	Random rand = new Random();
	
	BodyPart head;
	ArrayList<BodyPart> tail;

	int xVel = 1;
	int yVel = 0;
	int size;
	int growthConstant;
	int length; // length of tail
	boolean isAlive = true;
	Opacity o = Opacity.MIN;
	enum Opacity {
		MIN, FIRST, SECOND, THIRD, FOURTH, FIFTH, SIXTH,
		SEVENTH, EIGHTH, NINTH, TENTH, ELEVENTH,
		MAX {
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
	int score = 0;
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
		head = new BodyPart(size, size);
		tail = new ArrayList<>();
		growthConstant = g;
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
		//for random colors
//		for (int i = 0; i < tail.size(); i++) {
//			p.fill(rand.nextFloat() * 255, rand.nextFloat() * 255, rand.nextFloat() * 255);
//			p.rect(tail.get(i).x, tail.get(i).y, size, size);
//		}
		p.fill(255);
		p.rect(head.x, head.y, size, size);
	}
	
	public void showDeath() {
		p.fill(255,0,0);
		for (BodyPart b : tail) {
			p.rect(b.x, b.y, size, size);
		}
		p.rect(head.x, head.y, size, size);
	}

	public void changeDirection(int i, int j) {
		xVel = i;
		yVel = j;
	}

	public boolean move() {
		if (isAlive) {
			if (length > 0) {
				if (length == tail.size() && !tail.isEmpty()) {
					tail.remove(0);
				}
				tail.add(new BodyPart(head.x, head.y));
			}
			
			head.x += xVel * size;
			head.y += yVel * size;
			
			movesTillFruit++;
			
			head.x = PApplet.constrain(head.x, size, p.width - 2 * size);
			head.y = PApplet.constrain(head.y, size * 2, p.height - 2 * size);
		}
		
		if (death()) {
			isAlive = false;
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public boolean eat(Food f) {
		if (head.equals(f)) {
			score += ( ( length * length + 1 ) / ( movesTillFruit / scoreConstant ) );
			movesTillFruit = 0;
			length += growthConstant; // grow the snake
			return true;
		}
		return false;
	}
	
	public boolean death() {
		// TODO: wall death ( might already work ? )
		
		// returns true if head is colliding with the tail
		return tail.contains(head);
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
		return Math.round(Math.max(score, highScore));
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
}
