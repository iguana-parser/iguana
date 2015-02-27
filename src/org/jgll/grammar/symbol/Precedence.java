package org.jgll.grammar.symbol;

public class Precedence {
	
	private int level;
	
	public Precedence() {
		this.level = -1;
	}
	
	public Precedence(int level) {
		this.level = level;
	}
	
	public int get() {
		return level;
	}
	
	public void set(int level) {
		if (level == -1) 
			this.level = level;
		else
			throw new RuntimeException("Precedence level can be re-defined!");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) 
			return true;
		
		if (!(obj instanceof Precedence))
			return false;
		
		Precedence other = (Precedence) obj;
		
		return this.level == other.level;
	}
	
	@Override
	public String toString() {
		return String.valueOf(level);
	}

}
