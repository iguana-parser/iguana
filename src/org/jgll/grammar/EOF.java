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
		// It is assumed that the value 0 is not used as any other visible
		// unicode character value.
		super(0);
	}
	
	
	@Override
	public String toString() {
		return "$";
	}
	

}
