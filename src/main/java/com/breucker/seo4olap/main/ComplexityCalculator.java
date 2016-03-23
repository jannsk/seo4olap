package com.breucker.seo4olap.main;

import java.util.ArrayList;
import java.util.List;

public class ComplexityCalculator {

	private ComplexityCalculator() {}

	public static void main(String[] args) {
		int m = 1;
		int[] dimensions = {10,10,10,10};
		int maxFreeDimensions = 3;
		int maxMembers2Dice = 2;
		System.out.println(calculateViews(m, dimensions, maxFreeDimensions, maxMembers2Dice));
		System.out.println(calculateMaxViews(m, dimensions));
		
//		List<String[]> results = generateComplexityResults(10, 15, 1);
//		for(String[] result : results){
//			System.out.println(result[0] + "," + result[1] + "," + result[2] + "," + result[3] + "," 
//					+ result[4] + "," + result[5] + "," + result[6]);
//		}
	}

	/**
	 * Generates a list of complexity results
	 * @param membersPerDimension
	 * @param maxDimensions
	 * @param amountMeasures
	 * @return
	 */
	public static List<String[]> generateComplexityResults(int membersPerDimension, int maxDimensions, int amountMeasures){
		List<String[]> results = new ArrayList<String[]>();
		
		String[] header = {"membersPerDimension", "AmountOfDimensions", "AmountOfMeasures", 
				"maxFreeDimensions", "maxMembers2Dice", "amountViews", "maxAmountViews"};
		results.add(header);
		for(int n = 1; n <= maxDimensions; n ++){
			for(int maxFreeDimensions = 1; maxFreeDimensions <= n; maxFreeDimensions++){
				for(int maxMembers2Dice = 1; maxMembers2Dice <= n; maxMembers2Dice++){
					int[] dimensions = generateArray(membersPerDimension, n);
					long amountViews = calculateViews(amountMeasures, dimensions, maxFreeDimensions, maxMembers2Dice);
					long maxAmountViews = calculateMaxViews(amountMeasures, dimensions);
					
					String[] result = {""+ membersPerDimension, ""+ n, ""+ amountMeasures, ""+ maxFreeDimensions, 
							"" + maxMembers2Dice, "" + amountViews, "" + maxAmountViews};
					results.add(result);
				}
			}
		}
		return results;
	}
	
	/**
	 * Calculates the maximum possible amount of views
	 * @param measureCount the amount of measures
	 * @param dimensions an Array consisting of the amount of members per dimension
	 * @return
	 */
	public static long calculateMaxViews(int measureCount, int[] dimensions){
		final int n = dimensions.length;
		
		long result = 1;
		for(int i = 0; i < n; i ++){
			result *= (dimensions[i] + 2); 
		}
		
		return measureCount * result;
	}
	
	/**
	 * Calculates the amount of views 
	 * @param measureCount the amount of measures
	 * @param dimensions an Array consisting of the amount of members per dimension
	 * @param maxFreeDimensions 
	 * @param maxMembers2Dice
	 * @return
	 */
	public static long calculateViews(int measureCount, int[] dimensions, int maxFreeDimensions, int maxMembers2Dice){
		final int n = dimensions.length;
		
		long result = factor(maxFreeDimensions, 0, n);
		
		for(int i = 1; i <= maxMembers2Dice; i ++){
			result += factor(maxFreeDimensions, i, n) * diceCombinations(dimensions, i);
		}
		
		return measureCount * result;
	}	
	
	private static long diceCombinations(int[] dimensions, int diceDimensionality){
		final int n = dimensions.length;
		
		int upperBound = n - diceDimensionality + 1;
		
		long sum = recursiveSum(dimensions, 1, upperBound);
		
		return sum;
	}
	
	private static long recursiveSum(int[] dimensions, int lowerBound, int upperBound){
		final int n = dimensions.length;
		
		long sum = 0;		
		
		for(int i = lowerBound; i<=upperBound; i++){
		
			if(upperBound == n){
				sum += dimensions[i-1];
			}
			else{
				sum += dimensions[i-1] * recursiveSum(dimensions, i+1, upperBound + 1);
			}
		}
		return sum;
	}
	
	private static long factor(int maxFreeDim, int diceDim, int dimensionCount){
		long factor = 0;
		
		// (n-t)!/(l!*(n-t-l)!)
		for(int i = 0; i <= maxFreeDim; i ++){
			long increment = 0;
			if((dimensionCount - diceDim - i) >= 0 ){
				increment = factorial(dimensionCount - diceDim) / (factorial(i) * factorial(dimensionCount - diceDim - i)); 
			}
			factor += increment;
		}
		
		return factor;
	}
	
	
    private static int factorial(int n){
        
    	if( n <= 1)     // base case
            return 1;
        else
            return n * factorial( n - 1 );
    }
    

	private static int[] generateArray(int value, int size){
		int[] array = new int[size];
		for(int i = 0 ; i< size; i ++ ){
			array[i] = value;
		}
		return array;
	}
}
