package org.jgll.grammar.condition;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.jgll.traversal.IConditionVisitor;




/**
 *  
 * @author Ali Afroozeh
 *
 */
public class PositionalCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private transient SlotAction action;
	
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
	public String getConstructorCode() {
		return "new PositionalCondition(" + type.name() + ")";
	}
	
	// Reading the transiet action field
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		action = createSlotAction();
	}

	private SlotAction createSlotAction() {

		switch (type) {
		
			case START_OF_LINE:
		    	return (input, node, i) -> !input.isStartOfLine(i);
		    
			case END_OF_LINE:
				return (input, node, i) -> !input.isEndOfLine(i);
				
			case END_OF_FILE:
				return (input, node, i) -> !input.isEndOfFile(i);
		
		    default: 
		    	throw new RuntimeException();
		}
	}
	
	@Override
	public SlotAction getSlotAction() {
		return action;
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
