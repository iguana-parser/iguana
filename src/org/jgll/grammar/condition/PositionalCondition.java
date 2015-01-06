package org.jgll.grammar.condition;

import org.jgll.grammar.GrammarRegistry;



/**
 *  
 * @author Ali Afroozeh
 *
 */
public class PositionalCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private transient final SlotAction action;
	
	public PositionalCondition(ConditionType type) {
		super(type);
		action = createSlotAction();
	}
	
	@Override
	public String toString() {
		return type.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) return true;
		
		if(!(obj instanceof PositionalCondition)) return false;
		
		PositionalCondition other = (PositionalCondition) obj;
		
		return type == other.type;
	}
	
	@Override
	public String getConstructorCode(GrammarRegistry registry) {
		return "new PositionalCondition(" + type.name() + ")";
	}

	private SlotAction createSlotAction() {

		switch (type) {
		
			case START_OF_LINE:
		    	return (input, node, i, env) -> !input.isStartOfLine(i);
		    
			case END_OF_LINE:
				return (input, node, i, env) -> !input.isEndOfLine(i);
		
		    default: 
		    	throw new RuntimeException();
		}
	}
	
	@Override
	public SlotAction getSlotAction() {
		return action;
	}

}
