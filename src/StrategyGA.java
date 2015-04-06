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
		for(int i = 0; i < moveHistory.length; i++){
			moveHistory[i]= 1;
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
		//string from only testing against predefined strategies and better parameters
		//NOTE: This one tries to win, even if it's score is a bit lower. 
		//VS titfortat: win 436 - 430
		//0010000001010011001100000011000000001011000001100100011010100100
		
		//string from just going against other GAs
		//NOTE: this is bad, it can't beat StrategyRandom
		//0001011101101111100100101000010011100111011011110111111100011101
		
		//string from hybrid method:
		//NOTE: this one cooperates to maximize score. Also wrecks StrategyRandom
		//VS titfortat: tie 500 - 500
		//0010010001000001000000000000010000001011010011100100001100001101
		String input = "0010010001000001000000000000010000001011010011100100001100001101";
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
