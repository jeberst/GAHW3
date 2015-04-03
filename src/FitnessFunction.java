/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

class FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public String name;
	int optimalVal;

/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public FitnessFunction() {

		System.out.print("Setting up Fitness Function.....");

	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){
		System.out.println("Executing FF Raw Fitness");
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
		System.out.println("Executing FF Gene Output");
	}
	
	public void debugPrint(Chromo X){
		System.out.println("debugPrint is extension that is used only lab scheduling");
	}
	public int[] getHisto(Chromo X){
		System.out.println("getHisto is extension that is used only lab scheduling and prisoner dilema");
		return null;
	}


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/


}   // End of OneMax.java ******************************************************

