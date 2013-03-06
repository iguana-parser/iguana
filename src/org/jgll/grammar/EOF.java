package org.jgll.grammar;

public class EOF extends Character {
	
	private static final long serialVersionUID = 1L;

	private static EOF instance;
	
	public static EOF getInstance() {
		if(instance == null) {
			instance = new EOF();
		}
		return instance;
	}
	
	private EOF() {
		super(-1);
	}
	
	
	@Override
	public String toString() {
		return "$";
	}
	

}
