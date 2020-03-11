package com.thejfsullivan.geneticAlgorithm;

import dataStructures.Matrix;
import processing.core.PApplet;
import processing.data.Table;
import processing.data.TableRow;

public class NeuralNet {
	PApplet p;

	int inputNodes;
	int hiddenNodes1;
	int hiddenNodes2;
	int outputNodes;

	Matrix whi;// weights between the input nodes and the hidden nodes
	Matrix whh;// weights between the hidden nodes and the second layer hidden nodes
	Matrix woh;// weights between the second hidden layer nodes and the output nodes

	Matrix inputs;
	Matrix hiddenOutputs;
	Matrix hiddenOutputs2;
	Matrix outputs;

	public NeuralNet(PApplet p, int i, int h1, int h2, int o) {
		this.p = p;

		inputNodes = i;
		hiddenNodes1 = h1;
		hiddenNodes2 = h2;
		outputNodes = o;

		// create weighted matrixes including a bias weight
		whi = new Matrix(h1, i + 1);
		whh = new Matrix(h2, h1 + 1);
		woh = new Matrix(o, h2 + 1);

		whi.randomize();
		whh.randomize();
		woh.randomize();
	}

	public void mutate(double mutationRate) {
		whi.mutate(mutationRate);
		whh.mutate(mutationRate);
		woh.mutate(mutationRate);
	}

	public double[] output(double[] inputsArr) {
		// convert array to matrix
		inputs = Matrix.singleColumnMatrixFromArray(inputsArr);

		// add bias
		inputs = inputs.addBiasNode();

		/** Calculate the guessed output */

		// apply weights to first layer
		Matrix hiddenInputs = whi.dot(inputs);
		// pass through relu function
		hiddenOutputs = hiddenInputs.relu();
		// add bias
		hiddenOutputs = hiddenOutputs.addBiasNode();

		// apply layer two weights
		Matrix hiddenInputs2 = whh.dot(hiddenOutputs);
		hiddenOutputs2 = hiddenInputs2.relu();
		hiddenOutputs2 = hiddenOutputs2.addBiasNode();

		// apply level three weights
		Matrix outputInputs = woh.dot(hiddenOutputs2);
		outputs = outputInputs.sigmoid();

		// convert to an array and return
		return outputs.toArray();
	}

	// crossover function for genetic algorithm
	public NeuralNet crossover(NeuralNet partner) {

		// creates a new child with layer matrices from both parents
		NeuralNet child = new NeuralNet(p, inputNodes, hiddenNodes1, hiddenNodes2, outputNodes);
		child.whi = whi.singlePointCrossover(partner.whi);
		child.whh = whh.singlePointCrossover(partner.whh);
		child.woh = woh.singlePointCrossover(partner.woh);
		return child;
	}

	// return a neural net which is a clone of this Neural net
	public NeuralNet clone() {
		NeuralNet clone = new NeuralNet(p, inputNodes, hiddenNodes1, hiddenNodes2, outputNodes);
		clone.whi = whi.clone();
		clone.whh = whh.clone();
		clone.woh = woh.clone();

		return clone;
	}

	// converts the weights matrices to a single table
	// used for storing the snakes brain in a file
	public Table netToTable() {

		// create table
		Table t = new Table();

		// convert the matricies to an array
		double[] whiArr = whi.toArray();
		double[] whhArr = whh.toArray();
		double[] wohArr = woh.toArray();

		// set the amount of columns in the table
		for (int i = 0; i < Math.max(whiArr.length, Math.max(whhArr.length, wohArr.length)); i++) {
			t.addColumn();
		}

		// set the first row as whi
		TableRow tr = t.addRow();

		for (int i = 0; i < whiArr.length; i++) {
			tr.setDouble(i, whiArr[i]);
		}

		// set the second row as whh
		tr = t.addRow();

		for (int i = 0; i < whhArr.length; i++) {
			tr.setDouble(i, whhArr[i]);
		}

		// set the third row as woh
		tr = t.addRow();

		for (int i = 0; i < wohArr.length; i++) {
			tr.setDouble(i, wohArr[i]);
		}

		// return table
		return t;
	}

	// takes in table as parameter and overwrites the matrices data for this neural
	// network
	// used to load snakes from file
	public void tableToNet(Table t) {

		// create arrays to temporarily store the data for each matrix
		double[] whiArr = new double[whi.rows * whi.cols];
		double[] whhArr = new double[whh.rows * whh.cols];
		double[] wohArr = new double[woh.rows * woh.cols];

		// set the whi array as the first row of the table
		TableRow tr = t.getRow(0);

		for (int i = 0; i < whiArr.length; i++) {
			whiArr[i] = tr.getDouble(i);
		}

		// set the whh array as the second row of the table
		tr = t.getRow(1);

		for (int i = 0; i < whhArr.length; i++) {
			whhArr[i] = tr.getDouble(i);
		}

		// set the woh array as the third row of the table

		tr = t.getRow(2);

		for (int i = 0; i < wohArr.length; i++) {
			wohArr[i] = tr.getDouble(i);
		}

		// convert the arrays to matrices and set them as the layer matrices
		whi.fromArray(whiArr);
		whh.fromArray(whhArr);
		woh.fromArray(wohArr);
	}

	// draw the genome on the screen
	public void draw(int startX, int startY, int w, int h) {

		int inputSpacing = h / (inputNodes + 2);
		int hiddenSpacing1 = h / (hiddenNodes1 + 2);
		int hiddenSpacing2 = h / (hiddenNodes2 + 2);
		int outputSpacing = h / (outputNodes + 1);

		double weight = 0;

		for (int i = 0; i < whi.cols; i++) {
			for (int j = 0; j < whi.rows; j++) {
				weight = whi.matrix[j][i];
				if (weight > 0) {
					p.stroke(0, 255, 0);
				} else if (weight < 0) {
					p.stroke(255, 0, 0);
				} else {
					p.noStroke();
				}
				float sw = (float) (3 * Math.pow(Math.abs(weight), 3));
				if (sw > 3) {
					sw = 3;
				}
				p.strokeWeight(sw);
				if (sw > 1)
					p.line(startX, startY + (i + 1) * inputSpacing, startX + w / 3, startY + (j + 1) * hiddenSpacing1);
			}
		}

		for (int i = 0; i < whh.cols; i++) {
			for (int j = 0; j < whh.rows; j++) {
				weight = whh.matrix[j][i];
				if (weight > 0) {
					p.stroke(0, 255, 0);
				} else if (weight < 0) {
					p.stroke(255, 0, 0);
				} else {
					p.noStroke();
				}
				float sw = (float) (3 * Math.pow(Math.abs(weight), 3));
				if (sw > 3) {
					sw = 3;
				}
				p.strokeWeight(sw);
				if (sw > 1.5)
					p.line(startX + w / 3, startY + (i + 1) * hiddenSpacing1, startX + 2 * w / 3,
							startY + (j + 1) * hiddenSpacing2);
			}
		}

		for (int i = 0; i < woh.cols; i++) {
			for (int j = 0; j < woh.rows; j++) {
				weight = woh.matrix[j][i];
				if (weight > 0) {
					p.stroke(0, 255, 0);
				} else if (weight < 0) {
					p.stroke(255, 0, 0);
				} else {
					p.noStroke();
				}
				float sw = (float) (3 * Math.pow(Math.abs(weight), 3));
				if (sw > 3) {
					sw = 3;
				}
				p.strokeWeight(sw);
				p.line(startX + 2 * w / 3, startY + (i + 1) * hiddenSpacing2, startX + w,
						startY + (j + 1) * outputSpacing);
			}
		}

		p.stroke(255, 255, 0);
		p.strokeWeight(1);
		p.fill(0);
		for (int i = 0; i < inputNodes + 1; i++) {
			p.fill(0);
			if (inputs.matrix[i][0] > .85)
				p.fill(255, 255, 0);
			p.circle(startX, startY + (i + 1) * inputSpacing, 10);
		}
		for (int i = 0; i < hiddenNodes1 + 1; i++) {
			if (hiddenOutputs.matrix[i][0] > .85)
				p.fill(255, 255, 0);
			p.circle(startX + w / 3, startY + (i + 1) * hiddenSpacing1, 10);
			p.fill(0);
		}
		for (int i = 0; i < hiddenNodes2 + 1; i++) {
			if (hiddenOutputs2.matrix[i][0] > .85)
				p.fill(255, 255, 0);
			p.circle(startX + 2 * w / 3, startY + (i + 1) * hiddenSpacing2, 10);
			p.fill(0);
		}
		double max = 0;
		int maxIndex = 0;
		for (int i = 0; i < outputNodes; i++) {
			if (max < outputs.matrix[i][0]) {
				max = outputs.matrix[i][0];
				maxIndex = i;
			}
		}
		for (int i = 0; i < outputNodes; i++) {
			if (i == maxIndex)
				p.fill(255, 255, 0);
			p.circle(startX + w, startY + (i + 1) * outputSpacing, 10);
			p.fill(0);
		}
		p.fill(0);
	}

}
