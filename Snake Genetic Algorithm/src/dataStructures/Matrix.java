package dataStructures;

import java.util.Random;

public class Matrix {
	Random rand = new Random();

	// local variables
	public int rows;
	public int cols;
	public double[][] matrix;

	// constructor
	public Matrix(int r, int c) {
		rows = r;
		cols = c;
		matrix = new double[rows][cols];
	}

	// constructor from 2D array
	public Matrix(double[][] m) {
		matrix = m;
		cols = m.length;
		rows = m[0].length;
	}

	// print matrix
	public void print() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				System.out.print(matrix[i][j] + "  ");
			}
			System.out.println(" ");
		}
		System.out.println();
	}

	public void multiply(double n) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] *= n;
			}
		}
	}

	// return a matrix which is this matrix dot product parameter matrix
	public Matrix dot(Matrix n) {
		Matrix result = new Matrix(rows, n.cols);

		if (cols == n.rows) {
			// for each spot in the new matrix
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < n.cols; j++) {
					float sum = 0;
					for (int k = 0; k < cols; k++) {
						sum += matrix[i][k] * n.matrix[k][j];
					}
					result.matrix[i][j] = sum;
				}
			}
		}

		return result;
	}

	// set the matrix to random numbers between -1 and 1
	public void randomize() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = -1 + rand.nextInt(3);

				// // set the boundaries to 1 and -1
				// if (matrix[i][j] > 1) {
				// matrix[i][j] = 1;
				// }
				// if (matrix[i][j] < -1) {
				// matrix[i][j] = -1;
				// }
			}
		}
	}

	// add a scalar to the matrix
	public void add(float n) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] += n;
			}
		}
	}

	// return a matrix which is this matrix + parameter matrix
	public Matrix add(Matrix n) {
		Matrix newMatrix = new Matrix(rows, cols);
		if (cols == n.cols && rows == n.rows) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					newMatrix.matrix[i][j] = matrix[i][j] + n.matrix[i][j];
				}
			}
		}
		return newMatrix;
	}

	// return a matrix which is this matrix - parameter matrix
	public Matrix subtract(Matrix n) {
		Matrix newMatrix = new Matrix(cols, rows);
		if (cols == n.cols && rows == n.rows) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					newMatrix.matrix[i][j] = matrix[i][j] - n.matrix[i][j];
				}
			}
		}
		return newMatrix;
	}

	// return a matrix which is this matrix * parameter matrix (element wise
	// multiplication)
	public Matrix multiply(Matrix n) {
		Matrix newMatrix = new Matrix(rows, cols);
		if (cols == n.cols && rows == n.rows) {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					newMatrix.matrix[i][j] = matrix[i][j] * n.matrix[i][j];
				}
			}
		}
		return newMatrix;
	}

	// return a matrix which is the transpose of this matrix
	public Matrix transpose() {
		Matrix n = new Matrix(cols, rows);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				n.matrix[j][i] = matrix[i][j];
			}
		}
		return n;
	}

	// Creates a single column array from the parameter array
	public static Matrix singleColumnMatrixFromArray(double[] inputsArr) {
		Matrix n = new Matrix(inputsArr.length, 1);
		for (int i = 0; i < inputsArr.length; i++) {
			n.matrix[i][0] = inputsArr[i];
		}
		return n;
	}

	// sets this matrix from an array
	public void fromArray(double[] whiArr) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				matrix[i][j] = whiArr[j + i * cols];
			}
		}
	}

	// returns an array which represents this matrix
	public double[] toArray() {
		double[] arr = new double[rows * cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				arr[j + i * cols] = matrix[i][j];
			}
		}
		return arr;
	}

	// for ix1 matrixes adds one to the bottom
	public Matrix addBiasNode() {
		Matrix n = new Matrix(rows + 1, 1);
		for (int i = 0; i < rows; i++) {
			n.matrix[i][0] = matrix[i][0];
		}
		n.matrix[rows][0] = 1;
		return n;
	}

	// applies the activation function(sigmoid) to each element of the matrix
	public Matrix sigmoid() {
		Matrix n = new Matrix(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				n.matrix[i][j] = sigmoid(matrix[i][j]);
			}
		}
		return n;
	}

	public double sigmoid(double x) {
		return 1 / (1 + Math.pow((float) Math.E, -x));
	}

	// applies the activation function(relu) to each element of the matrix
	public Matrix relu() {
		Matrix n = new Matrix(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				n.matrix[i][j] = relu(matrix[i][j]);
			}
		}
		return n;
	}

	public double relu(double x) {
		return Math.max(0, x);
	}

	// returns the matrix which is this matrix with the bottom layer removed
	public Matrix removeBottomLayer() {
		Matrix n = new Matrix(rows - 1, cols);
		for (int i = 0; i < n.rows; i++) {
			for (int j = 0; j < cols; j++) {
				n.matrix[i][j] = matrix[i][j];
			}
		}
		return n;
	}

	public void mutate(double mutationRate) {

		// for each element in the matrix
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double r = rand.nextDouble();
				if (r < mutationRate) { // if chosen to be mutated
					matrix[i][j] += rand.nextGaussian() / 5; // add a random value to it (can be negative)

					// // set the boundaries to 1 and -1
					// if (matrix[i][j] > 1) {
					// matrix[i][j] = 1;
					// }
					// if (matrix[i][j] < -1) {
					// matrix[i][j] = -1;
					// }
				}
			}
		}
	}

	// returns a matrix which has a random number of values from this matrix and the
	// rest from the parameter matrix
	public Matrix singlePointCrossover(Matrix partner) {
		Matrix child = new Matrix(rows, cols);

		// pick a random point in the matrix
		int randC = (int) Math.floor(rand.nextDouble() * cols);
		int randR = (int) Math.floor(rand.nextDouble() * rows);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				if ((i < randR) || (i == randR && j <= randC)) { // if before the random point then copy from this
					// matrix
					child.matrix[i][j] = matrix[i][j];
				} else { // if after the random point then copy from the parameter array
					child.matrix[i][j] = partner.matrix[i][j];
				}
			}
		}
		return child;
	}

	public Matrix uniformCrossover(Matrix partner) {
		Matrix child = new Matrix(rows, cols);

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {

				double r = rand.nextDouble();
				child.matrix[i][j] = r > 0.5 ? matrix[i][j] : partner.matrix[i][j];

			}
		}
		return child;
	}

	// return a copy of this matrix
	public Matrix clone() {
		Matrix clone = new Matrix(rows, cols);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				clone.matrix[i][j] = matrix[i][j];
			}
		}
		return clone;
	}
}
