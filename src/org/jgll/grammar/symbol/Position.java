package org.jgll.grammar.symbol;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Position {
	
	private final Rule rule;
	
	private final int position;
	
	public Position(Rule rule, int position) {
		this.rule = rule;
		this.position = position;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return position;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Position))
			return false;
		
		Position other = (Position) obj;
		
		return rule.equals(other.rule) && position == other.position;
	}
}
