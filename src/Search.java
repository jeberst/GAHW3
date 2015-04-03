/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];  // 0=Avg, 1=Best
	private static double fitnessTable[][][]; // [0 = Avg, 1 = Best][generation][run]
	private static boolean optimalFound[][]; //[generation][run]
	private static Chromo bestChromoPerRun[];
	private static double avgBestChromoPerRun;
	private static double stdBestChromoPerRun;
	private static int [][] allHisto; // all 50 histograms for you to make a pie chart later.

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void main(String[] args) throws java.io.IOException{
		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

	//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);

	//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);

	//	Set up Fitness Statistics matrix
		//fitnessStats is an accumulation of all the averages over all runs
		fitnessStats = new double[2][Parameters.generations];
		//fitnessTable should be the the averages per generation per run
		fitnessTable = new double [2][Parameters.generations][Parameters.numRuns + 1];
		//optimalFound keeps track of how many optimums were found this generation
		optimalFound = new boolean [Parameters.generations][Parameters.numRuns+1];
		for (int i=0; i<Parameters.generations; i++){
			fitnessStats[0][i] = 0;
			fitnessStats[1][i] = 0;
		}
		for (int i=0; i<Parameters.generations; i++){
			for (int j = 0; j < Parameters.numRuns; j++){
				fitnessTable[0][i][j] = 0;
				fitnessTable[1][i][j] = 0;	
				optimalFound[i][j] = false;
			}
		}

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("NM")){
				problem = new NumberMatch();
		}
		else if (Parameters.problemType.equals("OM")){
				problem = new OneMax();
		}
		else if (Parameters.problemType.equals("LSSGA")){
				problem = new labSchedulingSGA(args[1]);
		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;
		
		bestChromoPerRun = new Chromo[Parameters.numRuns + 1];
		for(int i = 0; i < Parameters.numRuns + 1; i++){
			bestChromoPerRun[i] = new Chromo();
		}
		allHisto = new int[Parameters.numRuns+1][];//append histograms to this
		
		//check to see if reportData folder exists. if not, create it.
		if(!(new File("reportData").exists())){
			new File("reportData").mkdir();
		}
		
		//  Start program for multiple runs
		for (R = 1; R <= Parameters.numRuns; R++){
			//file outputs for stuff to graph in final report
			String gOut1 = String.format("reportData\\%d.csv", R);
			BufferedWriter gOutWrite1 = new BufferedWriter(new FileWriter(gOut1));
			gOutWrite1.write("run,generation,best_gen_raw,avg_raw,std_dev_raw\n");
			//the 2nd graph
			String gOut2 = String.format("reportData\\%d_histo.csv", R);
			BufferedWriter gOutWrite2 = new BufferedWriter(new FileWriter(gOut2));
			gOutWrite2.write("first,second,third,forth\n");

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				member[i] = new Chromo();
				child[i] = new Chromo();
			}

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				sumRawFitness2 = 0;
				bestOfGenChromo.rawFitness = defaultBest;

				//	Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					member[i].rawFitness = 0;
					member[i].sclFitness = 0;
					member[i].proFitness = 0;

					problem.doRawFitness(member[i]);
					
					//check to see if an optimum was found this generation:
					if (member[i].rawFitness == problem.optimalVal){
						optimalFound[G][R] = true;
					}

					sumRawFitness = sumRawFitness + member[i].rawFitness;
					sumRawFitness2 = sumRawFitness2 +
						member[i].rawFitness * member[i].rawFitness;

					if (Parameters.minORmax.equals("max")){
						if (member[i].rawFitness > bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness > bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness > bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member[i].rawFitness < bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member[i]);
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member[i].rawFitness < bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member[i]);
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member[i].rawFitness < bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member[i]);
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}
				
				//you're still on a per generation basis here
				// Accumulate fitness statistics
				fitnessStats[0][G] += sumRawFitness / Parameters.popSize;
				fitnessStats[1][G] += bestOfGenChromo.rawFitness;
				
				//store these results for finding std.
				fitnessTable[0][G][R] = sumRawFitness / Parameters.popSize;
				fitnessTable[1][G][R] = bestOfGenChromo.rawFitness;

				averageRawFitness = sumRawFitness / Parameters.popSize;
				stdevRawFitness = Math.sqrt(
							Math.abs(sumRawFitness2 - 
							sumRawFitness*sumRawFitness/Parameters.popSize)
							/
							(Parameters.popSize-1)
							);
				
				/*
				 * print to file run #, gen #, bestOfGenChromo.rawfitness,
				 * average gen fitness for this run (fitnessTable[0][G][R] )
				 * along with it's std stdevRawFitness. file: gOutWrite1
				 */
				String gOutWriteString = String.format("%d,%d,%f,%f,%f\n", 
						R , G , bestOfGenChromo.rawFitness,fitnessTable[0][G][R] , 
						stdevRawFitness );
				gOutWrite1.write(gOutWriteString);

				// Output generation statistics to screen
				System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\n");


		// *********************************************************************
		// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
		// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = member[i].rawFitness + .000001;
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member[i].sclFitness = 1/(member[i].rawFitness + .000001);
						sumSclFitness += member[i].sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=Parameters.popSize-1; i>0; i--){
						for (int j=0; j<i; j++){
							if (memberFitness[j] > memberFitness[j+1]){
								TmemberIndex = memberIndex[j];
								TmemberFitness = memberFitness[j];
								memberIndex[j] = memberIndex[j+1];
								memberFitness[j] = memberFitness[j+1];
								memberIndex[j+1] = TmemberIndex;
								memberFitness[j+1] = TmemberFitness;
							}
						}
					}
					//  Copy ordered array to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member[i].rawFitness;
					}
					//  Bubble Sort the array by floating point number
					for (int i=1; i<Parameters.popSize; i++){
						for (int j=(Parameters.popSize - 1); j>=i; j--){
							if (memberFitness[j-i] < memberFitness[j]){
								TmemberIndex = memberIndex[j-1];
								TmemberFitness = memberFitness[j-1];
								memberIndex[j-1] = memberIndex[j];
								memberFitness[j-1] = memberFitness[j];
								memberIndex[j] = TmemberIndex;
								memberFitness[j] = TmemberFitness;
							}
						}
					}
					//  Copy array order to scale fitness fields
					for (int i=0; i<Parameters.popSize; i++){
						member[memberIndex[i]].sclFitness = i;
						sumSclFitness += member[memberIndex[i]].sclFitness;
					}

					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}


		// *********************************************************************
		// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
		// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member[i].proFitness = member[i].sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member[i].proFitness;
				}

		// *********************************************************************
		// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
		// *********************************************************************

				int parent1 = -1;
				int parent2 = -1;

				//  Assumes always two offspring per mating
				for (int i=0; i<Parameters.popSize; i=i+2){

					//	Select Two Parents
					parent1 = Chromo.selectParent();
					parent2 = parent1;
					while (parent2 == parent1){
						parent2 = Chromo.selectParent();
					}

					//	Crossover Two Parents to Create Two Children
					randnum = r.nextDouble();
					if (randnum < Parameters.xoverRate){
						Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
					}
					else {
						Chromo.mateParents(parent1, member[parent1], child[i]);
						Chromo.mateParents(parent2, member[parent2], child[i+1]);
					}
				} // End Crossover

				//	Mutate Children
				for (int i=0; i<Parameters.popSize; i++){
					child[i].doMutation();
				}

				//	Swap Children with Last Generation
				for (int i=0; i<Parameters.popSize; i++){
					Chromo.copyB2A(member[i], child[i]);
				}

			} //  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			//print out graph stuff and close output stream
			System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);
			//problem.debugPrint(bestOfRunChromo);
			Chromo.copyB2A(bestChromoPerRun[R], bestOfRunChromo);
			StringBuilder histogramString = new StringBuilder();
			int [] hist = problem.getHisto(bestOfRunChromo);
			for (int i = 1; i < hist.length-1; i++){
				histogramString.append(hist[i] + ",");
			}
			histogramString.append(hist[hist.length -1]);
			gOutWrite2.write(histogramString.toString());
			allHisto[R] = hist;
			gOutWrite1.close();
			gOutWrite2.close();
		} //End of a Run
		
		//print out avg pie chart stuff and close output
		String gOut3 = String.format("reportData\\pie_chart.csv", R);
		BufferedWriter gOutWrite3 = new BufferedWriter(new FileWriter(gOut3));
		gOutWrite3.write("first,second,third,forth\n");
		double [] histoAvg = new double [allHisto[1].length];
		for (int i = 1; i <=Parameters.numRuns; i++){
			//there's no run 0
			for(int j = 1; j < allHisto[i].length; j++){
				//first index of histogram is not used
				histoAvg[j] += allHisto[i][j];
			}
		}
		for(int j = 1; j < histoAvg.length; j++){
			//first index of histogram is not used
			histoAvg[j] /= (double)Parameters.numRuns;
			gOutWrite3.write(histoAvg[j] + ",");
		}
		gOutWrite3.close();
		
		/*
		 * find answers to 1B
		 */
		avgBestChromoPerRun = 0;
		for(int i = 1; i <= Parameters.numRuns; i++){
			avgBestChromoPerRun += bestChromoPerRun[i].rawFitness;
		}
		avgBestChromoPerRun /= Parameters.numRuns;
		
		double [] bestChromoPerRunDoubles = new double [bestChromoPerRun.length]; 
		for(int index = 1; index <= Parameters.numRuns; index++ ){
			bestChromoPerRunDoubles[index] = bestChromoPerRun[index].rawFitness;
		}
		stdBestChromoPerRun = calcStd(bestChromoPerRunDoubles, avgBestChromoPerRun);
		/*
		 * done finding answers to 1B
		 */

		Hwrite.left("B", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);
		

		//	Output Fitness Statistics matrix
		summaryOutput.write("Gen            AvgFit              BestFit             StdAvgFit           StdBestFit          numOptimal\n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i]/Parameters.numRuns, 20, 2, summaryOutput);
			
			Hwrite.left(calcStd(fitnessTable[0][i], 
					fitnessStats[0][i]/Parameters.numRuns), 20, 2, summaryOutput);
			Hwrite.left(calcStd(fitnessTable[1][i], 
					fitnessStats[1][i]/Parameters.numRuns), 20, 2, summaryOutput);
			
			Hwrite.left(countOptimal(optimalFound[i]), 20, 2, summaryOutput);
			
			summaryOutput.write("\n");
		}

		summaryOutput.write("\n");
		summaryOutput.write("Average Best Fitness Per Run: " + avgBestChromoPerRun + "\n");
		summaryOutput.write("STD of Average Best Fitness Per Run: "+ stdBestChromoPerRun);
		summaryOutput.close();

		System.out.println();
		System.out.println("Start:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);

	} // End of Main Class
	
	
	private static double calcStd(double [] run, double avg){
		//the 1D array is taken from fitnessTable [][] and represents
		//the fitness values of a particular generation over all runs.
		//don't include the run value stored at index 0, it's invalid
		double sum = 0;
		for (int i = 1; i < run.length; i++){
			sum += (run[i] - avg) * (run[i] - avg);
		}
		sum /= (run.length - 1);
		return Math.sqrt(sum);
		
	}
	
	private static int countOptimal(boolean [] run){
		int count = 0;
		for (int i = 1; i < run.length; i++){
			if(run[i] == true)
				count++;
		}
		return count;
	}

}   // End of Search.Java ******************************************************

