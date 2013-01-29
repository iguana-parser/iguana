package org.jgll.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class TestSet {
	
	/**
	 * Indexed by nonterminal_index, alternate_index, position.
	 *  
	 * This array should be initalized by concrete subclasses. 
	 */
	protected int[][][][] testSets;
	
	public TestSet(String fileName) {
		try {
			load(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param nontermalIndex
	 * @param alternateIndex
	 * @return
	 */
	public int[] get(int nontermalIndex, int alternateIndex, int position) {
		return testSets[nontermalIndex][alternateIndex][position];
	}
	
	private void load(String fileName) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(fileName);
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		int nonterminals = Integer.parseInt(in.readLine());
		testSets = new int[nonterminals][][][];
		
		for(int nonterminal = 0; nonterminal < nonterminals; nonterminal++) {
			int alternates = Integer.parseInt(in.readLine());
			testSets[nonterminal] = new int[alternates][][];
			
			for(int alternate = 0; alternate < alternates; alternate++) {
				int alternateSize = Integer.parseInt(in.readLine());
				testSets[nonterminal][alternate] = new int[alternateSize][];
				
				for(int position = 0; position < alternateSize; position++) {
					int testSetSize = Integer.parseInt(in.readLine());
					testSets[nonterminal][alternate][position] = new int[testSetSize];
					
					for(int testSetItem = 0; testSetItem < testSetSize; testSetItem++) {
						testSets[nonterminal][alternate][position][testSetItem] = Integer.parseInt(in.readLine());
					}
				}
			}
		}
		
		is.close();
		in.close();
	}
	
}
