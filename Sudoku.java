package sudoku;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sudoku {

	/**
	 * Main Function
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime, endTime;
		Sudoku startGame = new Sudoku();
		startTime = System.currentTimeMillis();
		List<String> listOfTiming = new ArrayList<String>();
		//Get the list of boards all from a txt file
		List<List<String>> generateAllBoards = startGame.readFile("sudoku.txt");
		int board = 1;
		for(List<String>singleBoard: generateAllBoards){
			String [] fixedNumbers = singleBoard.toArray(new String[singleBoard.size()]);
			startTime = System.currentTimeMillis();
			//Initialize each board to solve them
			startGame.init(fixedNumbers);
			endTime = System.currentTimeMillis();
			long timeTaken = endTime - startTime;
			//Get the total time taken to complete each board
			
			startTime = System.currentTimeMillis();
			//Initialize each board to solve them
			startGame.initBruteForceAlgo(fixedNumbers);
			endTime = System.currentTimeMillis();
			long timeTaken2 = endTime - startTime;
			//Get the total time taken to complete each board
			System.out.println("Solved Board:"+board+ " Time:"+timeTaken+ " Brute Force Time:"+timeTaken2 );
			listOfTiming.add(timeTaken+","+timeTaken2);
			board++;
		}

		//Loop through the list of timings to give all the time taken
		for(String timeTaken : listOfTiming){
			System.out.println("Total Time Taken: "+ timeTaken);
		}
	}

	/**
	 * Parse a file pathname to retrieve all the boards
	 * @param path
	 * @return List of Boards
	 */
	public List<List<String>> readFile(String path){
		BufferedReader br = null;
		FileReader fr = null;
		List<List<String>> allRows = new ArrayList<List<String>>();
		try {
			fr = new FileReader(path);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				List<String> singleRow = new ArrayList<String>();
				//Get each specified row of numbers
				String row1 = sCurrentLine.substring(0, 9);
				String row2 = sCurrentLine.substring(9, 18);
				String row3 = sCurrentLine.substring(18, 27);
				String row4 = sCurrentLine.substring(27, 36);
				String row5 = sCurrentLine.substring(36, 45);
				String row6 = sCurrentLine.substring(45, 54);
				String row7 = sCurrentLine.substring(54, 63);
				String row8 = sCurrentLine.substring(63, 72);
				String row9 = sCurrentLine.substring(72, 81);
				singleRow = convertFormat(singleRow, row1, 0);
				singleRow = convertFormat(singleRow, row2, 1);
				singleRow = convertFormat(singleRow, row3, 2);
				singleRow = convertFormat(singleRow, row4, 3);
				singleRow = convertFormat(singleRow, row5, 4);
				singleRow = convertFormat(singleRow, row6, 5);
				singleRow = convertFormat(singleRow, row7, 6);
				singleRow = convertFormat(singleRow, row8, 7);
				singleRow = convertFormat(singleRow, row9, 8);
				allRows.add(singleRow);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return allRows;
	}

	/**
	 * Generate a format specified for the puzzle to read
	 * @param inputRow the input
	 * @param rowData the value of the cell
	 * @param rowNumber the row number
	 * @return
	 */
	public List<String> convertFormat(List<String>inputRow, String rowData, int rowNumber){
		for(int x = 0; x<rowData.length(); x++){
			String getNumber = rowData.substring(x, x+1);
			//If the cell has no value, it will be '0'
			if(!getNumber.equalsIgnoreCase("0")){
				String generateFormat = String.valueOf(rowNumber)+String.valueOf(x)+getNumber;
				inputRow.add(generateFormat);
			}
		}
		return inputRow;
	}

	/**
	 * Initialize the board
	 * @param input
	 */
	public void init(String [] input){
		CellData[][] matrix = defineBoard(input);
		//System out the initial Sudoku Board
		//printSudokuBoard(matrix);
		if (solve(0,0,matrix)){
			//System out the Completed Sudoku Board
			//printSudokuBoard(matrix); 
		}    		
		else{
			System.out.println("No Solution");
		}
	}
	
	/**
	 * Initialize the board
	 * @param input
	 */
	public void initBruteForceAlgo(String [] input){
		CellData[][] matrix = defineBoard(input);
		//System out the initial Sudoku Board
		//printSudokuBoard(matrix);
		if (solve(0,0,matrix)){
			//System out the Completed Sudoku Board
			//printSudokuBoard(matrix); 
		}    		
		else{
			System.out.println("No Solution");
		}
	}

	/**
	 * Solving the puzzle 
	 * @param i
	 * @param j
	 * @param cells
	 * @return
	 */
	public boolean solve(int i, int j, CellData[][] cells) {

		if (i == 9) {
			i = 0;
			if (++j == 9) 
				return true; 
		}
		if (cells[i][j].getValue() != 0){
			return solve(i+1,j,cells);
		}

		//Change the val to the remaining numbers left
		List<Integer> getAvailableNumbers = cells[i][j].getRemainingValuesAvailable();
		//System.out.println("Total number: "+getAvailableNumbers.size()+ " i: "+i+" j: "+j);
		for (Integer val : getAvailableNumbers) {
			if (legal(i,j,val,cells)) {  
				cells[i][j].setNewValue(val);       
				if (solve(i+1,j,cells))  
					return true;
			}
		}

		cells[i][j].setNewValue(0); // reset on backtrack
		return false;
	}

	/**
	 * Perform a check to see if any rules were violated / Duplicate Values
	 * @param i row number
	 * @param j column number
	 * @param val cell value
	 * @param cells Cell Object
	 * @return
	 */
	public boolean legal(int i, int j, int val, CellData[][] cells) {
		for (int k = 0; k < 9; ++k)  // row
			if (val == cells[k][j].getValue())
				return false;

		for (int k = 0; k < 9; ++k) // col
			if (val == cells[i][k].getValue())
				return false;

		int boxRowOffset = (i / 3)*3;
		int boxColOffset = (j / 3)*3;
		for (int k = 0; k < 3; ++k){
			for (int m = 0; m < 3; ++m){
				if (val == cells[boxRowOffset+k][boxColOffset+m].getValue()){
					return false;
				}
			}
		}

		return true; 
	}

	/**
	 * Create the board and populate with the pre-defined values
	 * @param args Pre-define values
	 * @return
	 */
	public CellData[][] defineBoard(String[] args) {
		CellData[][] cellData = new CellData[9][9];
		for(int row = 0; row < 9; row++){
			for(int col = 0; col < 9; col++){
				CellData newCell = new CellData();
				cellData[row][col] = newCell;
			}
		}

		for (int n = 0; n < args.length; ++n) {
			int i = Integer.parseInt(args[n].substring(0,1));   
			int j = Integer.parseInt(args[n].substring(1,2));   
			int val = Integer.parseInt(args[n].substring(2,3)); 

			CellData getCell = cellData[i][j];
			getCell.setInitValue(val);

			//Remove values for row
			for (int k = 0; k < 9; ++k){
				cellData[k][j].remove(val);
			}

			//Remove values for col
			for (int k = 0; k < 9; ++k){
				cellData[i][k].remove(val);
			}

			int boxRowOffset = (i / 3)*3;
			int boxColOffset = (j / 3)*3;
			//Remove values for 3x3 section
			for (int k = 0; k < 3; ++k){
				for (int m = 0; m < 3; ++m){
					cellData[boxRowOffset+k][boxColOffset+m].remove(val);
				}
			}
		}
		return cellData;
	}
	
	public CellData[][] defineOldAlgoBoard(String[] args) {
		CellData[][] cellData = new CellData[9][9];
		for(int row = 0; row < 9; row++){
			for(int col = 0; col < 9; col++){
				CellData newCell = new CellData();
				cellData[row][col] = newCell;
			}
		}
		return cellData;
	}

	/**
	 * Print the sudoku board
	 * @param solution
	 */
	static void printSudokuBoard(CellData[][] solution) {
		for (int i = 0; i < 9; ++i) {
			if (i % 3 == 0)
				System.out.println(" -----------------------");
			for (int j = 0; j < 9; ++j) {
				if (j % 3 == 0) System.out.print("| ");
				System.out.print(solution[i][j].getValue() == 0
						? " "
								: Integer.toString(solution[i][j].getValue()));
				System.out.print(' ');
			}
			System.out.println("|");
		}
		System.out.println(" -----------------------");
	}

	/**
	 * Data Access Object for Each Sudoku Cell
	 */
	public class CellData{
		int valueOfCell;
		List<Integer> remainingValuesAvailable;

		public CellData(){
			remainingValuesAvailable = new ArrayList<Integer>();
			for(int i = 1; i<10; i++){
				remainingValuesAvailable.add(i);
			}
		}

		/**
		 * Set the value for each cell during the beginning
		 * @param value
		 */
		private void setInitValue(int value){
			valueOfCell = value;
			remainingValuesAvailable.clear();
		}

		/**
		 * Set the value for each cell
		 * @param value
		 */
		private void setNewValue(int value){
			valueOfCell = value;
		}

		/**
		 * Get Value of each cell
		 * @return
		 */
		private int getValue(){
			return valueOfCell;
		}

		/**
		 * Get a list of available values left for each cell
		 * @return
		 */
		private List<Integer> getRemainingValuesAvailable(){
			return remainingValuesAvailable;
		}

		/**
		 * Remove the value from the list of values
		 * @param val
		 * @return
		 */
		private boolean remove(Integer val){
			remainingValuesAvailable.remove(val);
			return true;
		}
	}
}

