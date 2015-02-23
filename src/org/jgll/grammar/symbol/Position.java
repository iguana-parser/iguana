package org.jgll.grammar.symbol;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Position {
	
	private final Rule rule;
	
	private final int posInRule;
	
	private final int posInSymbol;
	
	public Position(Rule rule, int posInRule) {
		this(rule, posInRule, -1);
	}
	
	public Position(Rule rule, int posInRule, int posInSymbol) {
		this.rule = rule;
		this.posInRule = posInRule;
		this.posInSymbol = posInSymbol;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	public int getPosition() {
		return posInRule;
	}
	
	public boolean isFirst() {
		return posInRule == 1;
	}
	
	public boolean isLast() {
		return posInRule == rule.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Position))
			return false;
		
		Position other = (Position) obj;
		
		return rule.equals(other.rule) && posInRule == other.posInRule && posInSymbol == other.posInSymbol;
	}
	
	@Override
	public String toString() {
		
		if (posInSymbol == -1) 
			return "-";
		
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		
		if (rule.size() == 0) { 
			sb.append(".");
		} else {
			int i;
			if (posInRule == 0 && posInSymbol == 0) {
				sb.append(" . ");
			}
			for (i = 0; i < rule.size(); i++) {
				if (i + 1 == posInRule) {
					sb.append(rule.symbolAt(i).toString(posInSymbol) + " ");
				} else {
					sb.append(rule.symbolAt(i) + " ");
				}
			}
			
		}
		
		return sb.toString().trim();
	}
}
