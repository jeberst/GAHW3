import java.util.Random;

public class StrategyGA extends Strategy {

	/**
	 * Encoding for a GA strategy
	 */
	// 0 = defect, 1 = cooperate
	
	int [] moveHistory;
	int [] moveChoice;

	//This constructor is for the GA program that tries to find optimal chomosome
	public StrategyGA(String chromosome) {
		name = "GA Strategy";
		moveHistory = new int[6];
		moveChoice = new int[64];
		Random rand = new Random();
		for(int i = 0; i < moveHistory.length; i++){
			//randomly initalize the beginning moveset to explore more of chromosome
			if (rand.nextDouble() < .5)
				moveHistory[i] = 1;
			else 
				moveHistory[i] = 0;
		}
		for(int i = 0; i < moveChoice.length; i++){
			if (chromosome.charAt(i) == '1')
				moveChoice[i] = 1;
			else
				moveChoice[i] = 0;
			
		}
		/*
		for(int i = 0; i < moveChoice.length; i++){
			System.out.print(moveChoice[i]);
		}
		System.out.println();
		*/
	} /* StrategyGA */
	
	//this constructor is for Dr. Wu's ipd code to call and run
	//REQUIREMENT: file called GAPolicy.txt is already in source folder
	public StrategyGA(){
		name = "GA Strategy";
		moveHistory = new int[6];
		moveChoice = new int[64];
		for(int i = 0; i < moveHistory.length; i++){
			moveHistory[i]= 1;
		}
		/*
		rank selection:
			This is for when you're drawing from last generation only:
			1) component 1 alone:  (Average Average: 481.567 STD: 3.671)
			0011000001110000010011000100001010001000000000100101111000101100
			2) component 2 alone:  (Average Average: 418.733 STD: 5.103)
			0110001100001101010000000101101101010000000000000010000000010100
			3) hybrid: (Average Average: 457.433 STD: 3.707)
			0011001001101010000010100010101110011111010000010000011000000000
		
			this is for when you're drawing the bestOfAllChromo
			4) component 1 alone: (Average Average: 482.493 STD: 4.187)
			0011001101011100000001000100111000001000000000100000001000000100
			5) component 2 alone: (Average Average: 417.487 STD: 3.184)
			1000000001001100000000001000000111111001000001011000101111111110
			6) hybrid: (Average Average: 428.967 STD: 4.211)
			0101110101010110011010000111100101000110010001010000101101000110

		tournament selection:
			This is for when you're drawing from last generation only:
			7) component 1 alone:  (Average Average: 481.940 STD: 2.796)
			0010001011110001100000001100100000001010000001100001101100100100
			8) component 2 alone: (Average Average: 445.200 STD: 3.869)
			0000100010110101111000100000111000000100110100000001010010011010
			9) hybrid: (Average Average: 471.033 STD: 3.043)
			0011010100111111100000110000100000011000010001100000110100101010
	
			this is for when you're drawing the bestOfAllChromo
			10) component 1 alone: (Average Average: 479.473 STD: 4.136)
			0011011001010001000000011110010000001100000001100011001000000000
			11) component 2 alone: (Average Average: 446.047 STD: 3.338)
			1000011010000010000010101001000101010000110110011000111101111110
			12) hybrid:  (Average Average: 453.133 STD: 3.754)
			1000001000010011000010001100000000001110000000101000111110101001
		*/
		String input = "0011010100111111100000110000100000011000010001100000110100101010";
		for(int i = 0; i < moveChoice.length; i++){
			if (input.charAt(i) == '1')
				moveChoice[i] = 1;
			else
				moveChoice[i] = 0;
			
		}
	}

	public int nextMove() {
		//printMoveHistory();
		//System.out.println(getDec());
		return moveChoice[getDec()];
	} /* nextMove */

	private int twoToPower(int power) {
		int answer = 1;
		for (int i = 0; i < power; i++) {
			answer *= 2;
		}
		return answer;
	}
	
	private void pushVal(int val){
		for(int i = moveHistory.length -1; i > 0; i--){
			moveHistory[i]= moveHistory[i-1];
		}
		moveHistory[0] = val;
	}
	private int getDec(){
		int returnVal = 0;
		for(int i = 0; i < moveHistory.length; i++){
			if (moveHistory[i] == 1){
				//this means cooperation btw
				returnVal += twoToPower(moveHistory.length - i - 1);
			}
		}
		return returnVal;
	}
	public void printMoveHistory(){
		for(int i = 0; i < moveHistory.length; i++){
			System.out.print(moveHistory[i]);
		}
		System.out.println();
	}
		//note: in the ipd, you will save your move first, and then your opponents
		//therefore, history is read as: OY for t=n, OY for t=n-1, OY for t=n-2
	   public void saveOpponentMove(int move)  {pushVal(move); }
	   public void saveMyMove(int move)  { pushVal(move); }
	   public int getOpponentLastMove()  { return moveHistory[0]; }
	   public int getMyLastMove()  { return moveHistory[1]; }
}
