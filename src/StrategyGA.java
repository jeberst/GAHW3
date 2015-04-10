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
		//CHROMOSOMES FROM LASTGEN:
		//static fitness function (485.6933333333334):
		//0011001000010000001100000101101000001010000000100101101100101100
		
		//GA tournament (418.93333333333345):
		//0110001100001101010000000101101101010000000000000010000000010100
		
		//hybrid (449.0333333333333):
		//0001000110111101000010000111000001001011000011110001111101001111
		
		//CHROMOSOMES FROM BESTOFALLCHROMO
		//static fitness function (486.34666666666664):
		//0010000101010101011101011111000100001010000001100100001110100100
		
		//GA tournament (418.59333333333336):
		//1000000001001100000000001000000111111001000001011000101111111110
		
		//hybrid (452.19333333333344):
		//1001000001010001011000011001000000011000000000111001001001001101
		String input = "0010000101010101011101011111000100001010000001100100001110100100";
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
