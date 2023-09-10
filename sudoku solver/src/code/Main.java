package code;

public class Main {

	public static void main(String[] args) throws Exception {
		/*
		 * if a cell can only be one number, its that number
		 * 
		 * if a potential is only present at that cell within its row/column/box, its that number
		 * 
		 * if two cells in the same row/column/box have the same 2 potentials, no other cell in the
		 * row/column/box can have those potentials
		 */
		
		Board puzzle = new Board();
		puzzle.loadPuzzle("blank");
		puzzle.display();
		puzzle.logicCycles();
		System.out.println();
		
		puzzle.display();
		System.out.println("Solved: " + puzzle.isSolved());
		System.out.println("Error: " + puzzle.errorFound());
		
	}

}
