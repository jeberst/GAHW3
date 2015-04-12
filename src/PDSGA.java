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
		name = "Prisoners Dilema with Simple Genetic Algorithm Problem";
		//Optimum value achieved when opponent always cooperates, and you always defect
		//reward = 7 for temptation. Multiply that by the number of games. 
		optimalVal = 7 * numGames;


	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************
	
	public void doRawFitness(Chromo X, Chromo [] allChromo){
		StrategyGA strat = new StrategyGA(X.chromo);
		X.rawFitness = 0;
		
		
		double fitnessPart1 = 0.0;
		for (int i = 0; i < testStrategies.length; i++){
			IteratedPD ipd = new IteratedPD(strat, testStrategies[i]);
			ipd.runSteps(numGames);
			fitnessPart1 += ipd.player1Score();
			//System.out.println("GA Score for i = " + i + ": " + ipd.player1Score() + ", Player 2:" + ipd.player2Score());
		}
		fitnessPart1 /= testStrategies.length;
		X.rawFitness = fitnessPart1;
		
		
		
		
		/*
		double fitnessPart2 = 0.0;
		for (int i = 0; i < allChromo.length; i++){
			StrategyGA otherStrat = new StrategyGA(allChromo[i].chromo);
			IteratedPD ipd = new IteratedPD(strat, otherStrat);
			ipd.runSteps(numGames);
			fitnessPart2 += ipd.player1Score();
			//System.out.println("GA Score for i = " + i + ": " + ipd.player1Score() + ", Player 2:" + ipd.player2Score());
		}
		fitnessPart2 /= allChromo.length;
		//X.rawFitness = fitnessPart2;
		 */
		 
		
		
		//X.rawFitness = (fitnessPart1 + fitnessPart2)/2;
		
		
		
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

