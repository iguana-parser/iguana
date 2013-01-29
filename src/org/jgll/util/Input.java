package org.jgll.util;


public class Input {
	
	private String input;
	
	private static Input instance;
	
	public static void initialize(String input) {
		instance = new Input(input);
	}
	
	public static Input getInstance() {
		if(instance == null) {
			throw new IllegalStateException("Input is not initialized yet.");
		}
		return instance;
	}
	
	private Input(String input) {
		this.input = input;
	}
	
	public String getLexeme(int startIndex, int endIndex) {
		return input.substring(startIndex, endIndex);
	}

}
