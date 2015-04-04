import java.io.*;
import java.util.*;

public class PDSGA extends FitnessFunction{
	
/*******************************************************************************
*                            PRIVATE CLASSESS                                  *
*******************************************************************************/


/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/
	Scanner parmInput; 

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/
	private static int numGames;
	private static int numOpponents;
	Strategy [] testStrategies;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public PDSGA(){
		numGames = 64;
		testStrategies = new Strategy[6];
		testStrategies[0] = new StrategyTitForTat();
		testStrategies[1] = new StrategyAlwaysCooperate();
		testStrategies[2] = new StrategyAlwaysDefect();
		testStrategies[3] = new StrategyRandom();
		testStrategies[4] = new StrategyTitForTwoTats();
		testStrategies[5] = new StrategyPavlov();
		numOpponents = testStrategies.length; 
		name = "Prisoners Dilema with Simple Genetic Algorithm Problem";
		//this is only case when this player always defects, and other always cooperates
		optimalVal = 7 * numGames * numOpponents;


	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){

		X.rawFitness = 0;
		int fitness = 0;
		StrategyGA strat = new StrategyGA(X.chromo);
		for (int i = 0; i < testStrategies.length; i++){
			IteratedPD ipd = new IteratedPD(strat, testStrategies[i]);
			ipd.runSteps(numGames);
			fitness += ipd.player1Score();
			//System.out.println("GA Score for i = " + i + ": " + ipd.player1Score() + ", Player 2:" + ipd.player2Score());
		}
		

		X.rawFitness = fitness;
		
		
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getGeneAlpha(i),11,output);
		}
		output.write("   RawFitness");
		output.write("\n        ");
		for (int i=0; i<Parameters.numGenes; i++){
			Hwrite.right(X.getPosIntGeneValue(i),11,output);
		}
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");
		return;
	}
	
	public int [] getHisto(Chromo X){
		int[] scoreArray = new int[testStrategies.length + 1];
		StrategyGA strat = new StrategyGA(X.chromo);
		for (int i = 0; i < testStrategies.length; i++){
			IteratedPD ipd = new IteratedPD(strat, testStrategies[i]);
			ipd.runSteps(numGames);
			scoreArray[i+1] = ipd.player1Score();
		}
		return scoreArray;

		
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

}   // End of labScheduling.java ******************************************************

