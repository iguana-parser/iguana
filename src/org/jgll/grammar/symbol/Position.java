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
		
		// FIXME: account for position in a symbol
		
		StringBuilder sb = new StringBuilder();
		sb.append(rule.getHead()).append(" ::= ");
		
		if (rule.size() == 0) { 
			sb.append(".");
		} else {
			int i;
			for (i = 0; i < rule.size(); i++) {
				if (i == posInRule) {
					sb.append(". ");
				}
				sb.append(rule.symbolAt(i) + " ");
			}
			
			if (posInRule == rule.size()) {
				sb.append(".");
			}
		}
		
		return sb.toString().trim();
	}
}
