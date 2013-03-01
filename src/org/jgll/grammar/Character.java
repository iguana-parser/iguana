package org.jgll.grammar;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Character implements Terminal {
	
	private static final long serialVersionUID = 1L;

	private final int c;

	public Character(int c) {
		this.c = c;
	}
	
	public int get() {
		return c;
	}
	
	@Override
	public boolean match(int i) {
		return c == i;
	}
	
	@Override
	public String toString() {
		return (char) c + "";
	}

	@Override
	public String getMatchCode() {
		return "I[ci] == " + c;
	}

	@Override
	public int hashCode() {
		return 31 * 17 + c;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Character)) {
			return false;
		}
		Character other = (Character) obj;
		
		return c == other.c;
	}

}
