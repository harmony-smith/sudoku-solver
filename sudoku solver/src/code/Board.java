package code;

import java.io.File;
import java.util.Scanner;

public class Board {

	/*The Sudoku Board is made of 9x9 cells for a total of 81 cells.
	 * In this program we will be representing the Board using a 2D Array of cells.
	 * 
	 */

	private Cell[][] board = new Cell[9][9];
	
	//The variable "level" records the level of the puzzle being solved.
	private String level = "";
	
	private int current;

	private Cell[][][] stack = new Cell[81][][];
	
	// CONSTRUCTOR
	//This must initialize every cell on the board with a generic cell.  It must also assign all of the boxIDs to the cells
	public Board() {
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				board[x][y] = new Cell();
				board[x][y].setBoxID( 3*(x/3) + (y)/3+1);
			}
		current = 0;
	}
	
	
	//loadPuzzle
	/*This method will take a single String as a parameter.  The String must be either "easy", "medium" or "hard"
	 * If it is none of these, the method will set the String to "easy".  The method will set each of the 9x9 grid
	 * of cells by accessing either "easyPuzzle.txt", "mediumPuzzle.txt" or "hardPuzzle.txt" and setting the Cell.number to 
	 * the number given in the file.  
	 * 
	 * This must also set the "level" variable
	 * TIP: Remember that setting a cell's number affects the other cells on the board.
	 */
	public void loadPuzzle(String level) throws Exception {
		this.level = level;
		String fileName = "easyPuzzle.txt";
		if(level.contentEquals("medium"))
			fileName = "mediumPuzzle.txt";
		else if(level.contentEquals("hard"))
			fileName = "hardPuzzle.txt";
		else if(level.contentEquals("blank"))
			fileName = "blankPuzzle.txt";
		Scanner input = new Scanner (new File(fileName));
		
		for(int x = 0; x < 9; x++)
			for(int y = 0 ; y < 9; y++)
			{
				int number = input.nextInt();
				if (number != 0)
					solve(x, y, number);
			}
						
		input.close();
		
	}
	
	// isSolved
	/*This method scans the board and returns TRUE if every cell has been solved.  Otherwise it returns FALSE
	 * 
	 */
	public boolean isSolved() {
		for (int x = 0; x < 9 ; x++) {
			for (int y = 0; y < 9; y++) {
				if (board[x][y].getNumber() == 0)
					return false;
			}
		}
		return true;
	}


	// DISPLAY
	/*This method displays the board neatly to the screen.  It must have dividing lines to show where the box boundaries are
	 * as well as lines indicating the outer border of the puzzle
	 */
	public void display() {
		for (int x = 0; x < 9; x++) {
			if (x % 3 == 0)
				System.out.println("+ - - - + - - - + - - - +");
			for (int y = 0; y < 9; y++) {
				if (y  % 3 == 0)
					System.out.print("| ");
				System.out.print(board[x][y].getNumber() + " ");
			}
			System.out.print("|");
			System.out.println();
		}
		System.out.println("+ - - - + - - - + - - - +");
	}
	
	//solve
	/*This method solves a single cell at x,y for number.  It also must adjust the potentials of the remaining cells in the same row,
	 * column, and box.
	 */
	public void solve(int x, int y, int number) {
		board[x][y].setNumber(number);
		
		//x
		for (int z = 0; z < 9; z++) {
			if (z != x)
				board[z][y].cantBe(number);
			//System.out.println("cell: " + x + ", " + y);
			//board[z][y].displayPotential();
		}
		
		//y
		for (int z = 0; z < 9; z++) {
			if (z != y)
				board[x][z].cantBe(number);
		}
		//box
		for (int z = 0; z < 9; z++) {
			for (int w = 0; w < 9; w++) {
				if (board[z][w].getBoxID() == board[x][y].getBoxID() && board[z][w] != board[x][y])
					board[z][w].cantBe(number);
			}
		}
		
		board[x][y].canBe(number);
	}
	
	
	//logicCycles() continuously cycles through the different logic algorithms until no more changes are being made.
	public void logicCycles()throws Exception {
		int[][] guess = new int[81][3];
		while(isSolved() == false)
		{
			int changesMade = 0;
			do
			{
				changesMade = 0;
				changesMade += logic1();
				changesMade += logic2();
				changesMade += logic3();
				changesMade += logic4();
				//System.out.println("Changes: " + changesMade);
				if(errorFound()) {
					break;
				}
			}while(changesMade != 0);
			//System.out.println(":(");
			
			//display();
			
			//return;
			
			if (errorFound()) {	
			//	System.out.println("reject guess, return to before\n\n\n\n\n\n");
				current--;
				board = stack[current];
				//System.out.println("guess: x:" + guess[current][1] + " y " + guess[current][0] + " num " + guess[current][2]);
				/*
				System.out.println("Potentials: ");
				for (int bob = 0; bob < 10; bob++) {
					if (board[guess[current][0]][guess[current][1]].canBe(bob)) {
						System.out.print(bob + " ");
					}
				}
				System.out.println();
				*/
				board[guess[current][0]][guess[current][1]].cantBe(guess[current][2]);
			}
			
			boolean found = false;
			for (int x = 0; x < 9 && !found; x++) {
				for (int y = 0; y < 9 && !found; y++) {
					if (board[x][y].getNumber() == 0) 
						for (int z = 1; z < 10; z++) 
							if (board[x][y].canBe(z) && board[x][y].numberOfPotentials() > 1) {
								//System.out.println(board[x][y].numberOfPotentials());
								//guess
								found = true;
								/*
								System.out.println("Potentials: ");
								for (int bob = 0; bob < 10; bob++) {
									if (board[x][y].canBe(bob)) {
										System.out.print(bob + " ");
									}
								}
								System.out.println();
								*/
								guess[current][0] = x;
								guess[current][1] = y;
								guess[current][2] = z;
								//System.out.println("guess: x:" + guess[current][1] + " y " + guess[current][0] + " num " + guess[current][2]);
								//System.out.println("x: " + y + "   y: " + x + "   guess: " + z);
								copy();
								solve(x, y, z);
								//display();
								break;
							}
				}
			}
		}			
	}
	
	
	//logic1
	/*This method searches each row of the puzzle and looks for cells that only have one potential.  If it finds a cell like this, it solves the cell 
	 * for that number. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic1() {
		int changesMade = 0;
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				if (board[x][y].numberOfPotentials() == 1) {
					if (board[x][y].getNumber() != board[x][y].getFirstPotential())
						changesMade++;
					solve(x, y, board[x][y].getFirstPotential());
				}
			}
		}	
		return changesMade;			
	}
	
	//logic2
	/*This method searches each row for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell.  It then does the same thing for the columns.This also tracks the number of cells that 
	 * it solved as it traversed the board and returns that number.
	 */
	
	public int logic2() {
		int changesMade = 0;
		
		for (int x = 0; x < 9; x++) {
			for (int value = 1; value < 10; value++) {
				boolean found = false;
				int place = 0;
				for (int y = 0; y < 9; y++) {
					if (board[x][y].canBe(value) && found == false) {
						found = true;
						place = y;
					} else if (board[x][y].canBe(value) && found == true) {
						found = false;
						break;
					}	
				}
				if (found == true && board[x][place].getNumber() == 0) {
					changesMade++;
					solve(x, place, value);
				}
			}
		}
		
		
		for (int y = 0; y < 9; y++) {
			for (int value = 1; value < 10; value++) {
				boolean found = false;
				int place = 0;
				for (int x = 0; x < 9; x++) {
					if (board[x][y].canBe(value) && found == false) {
						found = true;
						place = x;
					} else if (board[x][y].canBe(value) && found == true) {
						found = false;
						break;
					}	
				}
				if (found == true && board[place][y].getNumber() == 0) {
					changesMade++;
					solve(place, y, value);
				}
			}
		}
		return changesMade;
	}
	
	//logic3
	/*This method searches each box for a cell that is the only cell that has the potential to be a given number.  If it finds such a cell and it
	 * is not already solved, it solves the cell. This also tracks the number of cells that it solved as it traversed the board and returns that number.
	 */
	public int logic3() {
		int changesMade = 0;
		int[] place = new int[2];
		boolean quit = false;
		
		for (int x = 0; x < 9; x += 3 ) {
			for (int y = 0; y < 9; y += 3) {
				
				for (int value = 1; value < 10; value++) {
					boolean found = false;
					quit = false;
					for (int i = 0; i < 3 && !quit; i++) {
						for (int j = 0; j < 3 && !quit; j++) {
							if (board[x + i][y + j].canBe(value) && found == false) {
								found = true;
								place[0] = i;
								place[1] = j;
							} else if (found == true && board[x+i][y+j].canBe(value)) {
								found = false;
								quit = true;
							}
						}
					}
					
					if (found == true && board[x + place[0]][y + place[1]].getNumber() == 0) {
						changesMade++;
						solve(x + place[0], y + place[1], value);
					}
				}
			}
		}
		
		return changesMade;
	}
	
	
	//logic4
		/*This method searches each row for the following conditions:
		 * 1. There are two unsolved cells that only have two potential numbers that they can be
		 * 2. These two cells have the same two potentials (They can't be anything else)
		 * 
		 * Once this occurs, all of the other cells in the row cannot have these two potentials.  Write an algorithm to set these two potentials to be false
		 * for all other cells in the row.
		 * 
		 * Repeat this process for columns and boxes.
		 * 
		 * This also tracks the number of cells that it solved as it traversed the board and returns that number.
		 */
	public int logic4() {
		int changesMade = 0;
		
		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 9; y++) 
				if (board[x][y].numberOfPotentials() == 2) 
					for (int y2 = 0; y2 < 9; y2++) 
						if (board[x][y2].numberOfPotentials() == 2 && y2 != y && board[x][y].getFirstPotential() == board[x][y2].getFirstPotential() && board[x][y].getSecondPotential() == board[x][y2].getSecondPotential())
							for (int z = 0; z < 9; z++) 
								if (z != y && z != y2) {
									if (board[x][z].canBe(board[x][y].getFirstPotential()) || board[x][z].canBe(board[x][y].getSecondPotential()))
										changesMade++;
									board[x][z].cantBe(board[x][y].getFirstPotential());
									board[x][z].cantBe(board[x][y].getSecondPotential());
								}
		
		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 9; y++) 
				if (board[x][y].numberOfPotentials() == 2) 
					for (int x2 = 0; x2 < 9; x2++) 
						if (board[x2][y].numberOfPotentials() == 2 && x2 != x && board[x][y].getFirstPotential() == board[x2][y].getFirstPotential() && board[x][y].getSecondPotential() == board[x2][y].getSecondPotential())
							for (int z = 0; z < 9; z++) 
								if (z != x && z != x2) {
									if (board[z][y].canBe(board[x][y].getFirstPotential()) || board[z][y].canBe(board[x][y].getSecondPotential()))
										changesMade++;
									
									board[z][y].cantBe(board[x][y].getFirstPotential());	
									board[z][y].cantBe(board[x][y].getSecondPotential());								
								}
		
		for (int x = 0; x < 9; x += 3) 
			for (int y = 0; y < 9; y += 3) 
				for (int x2 = 0; x2 < 3; x2 ++) 
					for (int y2 = 0; y2 < 3; y2 ++) 						
						if (board[x + x2][y + y2].numberOfPotentials() == 2) 
							for (int x3 = 0; x3 < 3; x3++) 
								for (int y3 = 0; y3 < 3; y3 ++) 
									if (board[x + x3][y + y3].numberOfPotentials() == 2 && (x3 != x2 || y3 != y2) && board[x + x3][y + y3].getFirstPotential() == board[x + x2][y + y2].getFirstPotential() && board[x + x3][y + y3].getSecondPotential() == board[x + x2][y + y2].getSecondPotential()) 
										for (int x4 = 0; x4 < 3; x4++) 
											for (int y4 = 0; y4 < 3; y4++) 
												if ((x4 != x3 || y4 != y3) && (x4 != x2 || y4 != y2)) {
													if (board[x + x4][y + y4].canBe(board[x + x2][y + y2].getFirstPotential()) || board[x + x4][y + y4].canBe(board[x + x2][y + y2].getSecondPotential())) {
														changesMade++;
													}
													
													board[x + x4][y + y4].cantBe(board[x + x2][y + y2].getFirstPotential());
													board[x + x4][y + y4].cantBe(board[x + x2][y + y2].getSecondPotential());
												}
											
		return changesMade;
	}
	
	
	// errorFound
	/*This method scans the board to see if any logical errors have been made.  It can detect this by looking for a cell that no longer has the potential to be 
	 * any number.
	 */
	public boolean errorFound() {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				if (board[x][y].numberOfPotentials() == 0) {
				//	System.out.println("x: " + y + "    y: " + x + "    value: " + board[x][y].getNumber() + "    current: " + current);
					return true;
				}
			}
		}
		return false;
	}
	
	
	//copy
	//takes old board, makes new empty board, then copies over information
	public void copy() {
		Cell[][] copy = new Cell[9][9];
		for (int x = 0; x < 9; x++) 
			for (int y = 0; y < 9; y++) {
				copy[x][y] = new Cell();
				copy[x][y].setBoxID( 3*(x/3) + (y)/3+1);
				if (board[x][y].getNumber() != 0) 
					copy[x][y].setNumber(board[x][y].getNumber());
				else
					for (int z = 1; z < 10; z++) 
						if (!board[x][y].canBe(z)) 
							copy[x][y].cantBe(z);	
			}
		
		stack[current] = copy;
		current++;
		
	}
}
