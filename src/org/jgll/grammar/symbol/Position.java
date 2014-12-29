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
	
	public boolean isFirst() {
		return position == 0;
	}
	
	public boolean isLast() {
		return position == rule.size();
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		
		if (rule.size() == 0) { 
			sb.append(".");
		} else {
			int i;
			for (i = 0; i < rule.size(); i++) {
				if (i == position) {
					sb.append(". ");
				}
				sb.append(rule.symbolAt(i) + " ");
			}
			
			if (position == rule.size()) {
				sb.append(".");
			}
		}
		
		return sb.toString().trim();
	}
}
