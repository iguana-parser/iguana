package org.jgll.grammar.condition;

import org.jgll.grammar.symbol.Terminal;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class TokenCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private int tokenID;
	
	public TokenCondition(ConditionType type, Terminal terminal) {
		super(type);
	}
	
	@Override
	public String toString() {
		return type.toString() + " " + tokenID;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof TokenCondition)) {
			return false;
		}
		
		TokenCondition other = (TokenCondition) obj;
		
		return type == other.type && 
			   tokenID == other.tokenID;
	}

}
