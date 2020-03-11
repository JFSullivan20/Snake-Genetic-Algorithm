package com.thejfsullivan.components;

import java.util.Random;

import processing.core.*;

public class Food extends Cell {
	
	PApplet p;
	Random rand = new Random();
	
	int size;
	
	public Food(PApplet p) {
		this(p, 10);
	}

	public Food(PApplet p, int s) {
		super();
		this.p = p;
		this.size = s;
		randomizeLocation();
	}
	
	public void show() {
		p.fill(0, 255, 0);
		p.rect(x, y, size, size);
	}
	
	public void randomizeLocation() {
		int cols = (500 / size) - 2; // TODO: fix this size thingy
		int rows = (p.height / size) - 3;
		
		x = size + (int) Math.floor(rand.nextInt(cols)) * size;
		y = size * 4 + (int) Math.floor(rand.nextInt(rows)) * size;
	}

}
