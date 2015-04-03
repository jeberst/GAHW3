import java.io.*;
import java.util.*;
import java.text.*;

public class labSchedulingSGA extends FitnessFunction{
	
/*******************************************************************************
*                            PRIVATE CLASSESS                                  *
*******************************************************************************/
	//gene class is designed to be sorted by gene value, and stores original 
	//array position
	private class gene implements Comparable<gene>{
		int geneVal;
		int arrayPosition;
		
		public gene(int _geneVal, int _arrayPosition){
			geneVal = _geneVal;
			arrayPosition = _arrayPosition;
		}
		
		public int getGeneVal(){
			return geneVal;
		}
		
		public int getArrayPosition(){
			return arrayPosition;
		}
		
		public int compareTo(gene compareGene) {		 
			//ascending order
			return this.getGeneVal() - compareGene.getGeneVal();
		}
	}

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/
	Scanner parmInput; 
	studentPref [] studentPrefs;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/
	private static final int numLabSlots = 5;
	private static final int numLabDays = 7;
	private static final int numStudents = 7;
	private static final int slotsPerStudent = 5;
	private static  double [] rewards;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public labSchedulingSGA(String fileName) throws java.io.IOException{
		//setup the reward values
		rewards = new double[5];
		rewards[0] = .1; //this is the penalty
		rewards[1] = 5;
		rewards[2] = 1;
		rewards[3] = .1;
		rewards[4] = .001;
		
		name = "Lab Scheduling with Simple Genetic Algorithm Problem";
		optimalVal = 176;
		parmInput = new Scanner (new File(fileName));
		studentPrefs = new studentPref[numStudents];
		int [][] tempPref;
		String tempName; 
		
		//scan in all data for all students
		for (int i = 0; i < numStudents; i++){
			tempName = parmInput.next(); 
			tempPref = new int [slotsPerStudent][numLabDays];
			for (int j = 0; j < slotsPerStudent; j++){
				for(int k = 0; k < numLabDays; k++){
					tempPref[j][k]=parmInput.nextInt();
				}
			} //done reading in a particular student's data
			studentPrefs[i] = new studentPref(tempName,tempPref);
			//studentPrefs[i].print();
		}
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){

		X.rawFitness = 0;		
		gene [] chromoVals  = new gene [Parameters.numGenes];
		int tempGeneVal;
		int temp = 0;
		double fitness = 0;
		
		int [] studentTimeRanks = new int [rewards.length]; //count how many 0's, 1's,...,4's
		for(int i = 0; i < studentTimeRanks.length; i++){
			studentTimeRanks[i] = 0;
		}
		
		for (int z = 0; z < Parameters.numGenes; z++){
			tempGeneVal = X.getIntGeneValue(z);
			chromoVals[z] = new gene(tempGeneVal, z);
		}
		Arrays.sort(chromoVals); //sort chromosomes by their gene values
		
		for (int i = 0; i < studentPrefs.length; i++){
			for(int j = 0; j < slotsPerStudent; j++){
				//get if student likes this time or not (value 0 - 4)
				temp = studentPrefs[i].pref(chromoVals[i*5 + j].getArrayPosition());
				//increment counter
				studentTimeRanks[temp]++;
			}
		}
		
		if (studentTimeRanks[0] > 0){
			/* the only penalty that can happen with my representation is 
			 * that students can be mapped to times they don't want.
			 * I don't have to worry about having 5 slots per student, 
			 * no having any more or less than 1 monitor per lab slot *
			 */
			
			fitness = Power(rewards[0], studentTimeRanks[0]);
		}
		else{
			//this indicates there are no penalties
			fitness = 1.0;
			for(int i = 0; i < rewards.length - 1; i++){
				fitness += rewards[i] * studentTimeRanks[i];
			}
		}
		X.rawFitness = fitness;
		
		/* debugging code
		System.out.println("Now printing for debugging");
		for (int i = 0; i < studentPrefs.length; i++){
			for (int j = 0; j < slotsPerStudent; j++){
				System.out.print(chromoVals[i*5 + j].getArrayPosition() + " ");
			}
			System.out.println();
		}*/
		
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
		gene [] chromoVals  = new gene [Parameters.numGenes];
		int tempGeneVal;
		int temp = 0;
		
		int [] studentTimeRanks = new int [rewards.length]; //count how many 0's, 1's,...,4's
		for(int i = 0; i < studentTimeRanks.length; i++){
			studentTimeRanks[i] = 0;
		}
		
		for (int z = 0; z < Parameters.numGenes; z++){
			tempGeneVal = X.getIntGeneValue(z);
			chromoVals[z] = new gene(tempGeneVal, z);
		}
		Arrays.sort(chromoVals); //sort chromosomes by their gene values
		
		for (int i = 0; i < studentPrefs.length; i++){
			for(int j = 0; j < slotsPerStudent; j++){
				System.out.print(chromoVals[i*5 + j].getArrayPosition() + " ");
				//get if student likes this time or not (value 0 - 4)
				temp = studentPrefs[i].pref(chromoVals[i*5 + j].getArrayPosition());
				//increment counter
				studentTimeRanks[temp]++;
			}
		}
		return studentTimeRanks;
	}
	public void debugPrint(Chromo X){
		System.out.println("Now printing for debugging");
		gene [] chromoVals  = new gene [Parameters.numGenes];
		int tempGeneVal;
		int temp = 0;
		
		int [] studentTimeRanks = new int [rewards.length]; //count how many 0's, 1's,...,4's
		for(int i = 0; i < studentTimeRanks.length; i++){
			studentTimeRanks[i] = 0;
		}
		
		for (int z = 0; z < Parameters.numGenes; z++){
			tempGeneVal = X.getIntGeneValue(z);
			chromoVals[z] = new gene(tempGeneVal, z);
		}
		Arrays.sort(chromoVals); //sort chromosomes by their gene values
		
		for (int i = 0; i < studentPrefs.length; i++){
			System.out.println("Student: " + studentPrefs[i].getName());
			for(int j = 0; j < slotsPerStudent; j++){
				System.out.print(chromoVals[i*5 + j].getArrayPosition() + " ");
				//get if student likes this time or not (value 0 - 4)
				temp = studentPrefs[i].pref(chromoVals[i*5 + j].getArrayPosition());
				//increment counter
				studentTimeRanks[temp]++;
			}
			System.out.println();
		}
		System.out.println("The student ranks:");
		for(int i = 0; i < studentTimeRanks.length; i++){
			System.out.print(studentTimeRanks[i] + " ");
		}
		System.out.println();
		
	}
/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/
    private static double Power(double base, int power){
        double answer = 1.0;
        for (int i = 0; i < power; i++){
            answer *= base;
        }
        return answer;
    }

}   // End of labScheduling.java ******************************************************

