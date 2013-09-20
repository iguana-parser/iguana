package org.jgll.grammar.condition;


/**
 *  
 * @author Ali Afroozeh
 *
 */
public class PositionalCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	public PositionalCondition(ConditionType type) {
		super(type);
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof PositionalCondition)) {
			return false;
		}
		
		PositionalCondition other = (PositionalCondition) obj;
		
		return type == other.type;
	}

}
