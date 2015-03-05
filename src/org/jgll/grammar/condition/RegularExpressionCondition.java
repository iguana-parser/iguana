package org.jgll.grammar.condition;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.jgll.regex.RegularExpression;
import org.jgll.traversal.IConditionVisitor;

/**
 * Conditions relating to the keyword exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularExpressionCondition extends Condition {
	
	private static final long serialVersionUID = 1L;
	
	private transient SlotAction action;

	private RegularExpression regularExpression;
	
	public RegularExpressionCondition(ConditionType type, RegularExpression regularExpression) {
		super(type);
		this.regularExpression = regularExpression;
		
		if (regularExpression.getPreConditions().size() != 0)
			throw new IllegalArgumentException("RegularExpression conditions cannot have conditions themselves.");
		
		action = createSlotAction(regularExpression);
	}

	@Override
	public String toString() {
		return type.toString() + " " + regularExpression;
	}
	
	// Reading the transiet action field
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		action = createSlotAction(regularExpression);
	}
	
	public SlotAction createSlotAction(RegularExpression r) {
		r.initMatcher();
		
		switch (type) {
		
		    case FOLLOW:
		    	return (input, node, i) -> r.getMatcher().match(input, i) == -1;
		    	
		    case NOT_FOLLOW:
		    case NOT_FOLLOW_IGNORE_LAYOUT:
		    	return (input, node, i) -> r.getMatcher().match(input, i) >= 0;
		    	
		    case MATCH:
		    	throw new RuntimeException("Unsupported");
		
			case NOT_MATCH: 
				return (input, node, i) -> r.getMatcher().match(input, node.getInputIndex(), i);
				
			case NOT_PRECEDE:
				return (input, node, i) -> {
					return r.getBackwardsMatcher().match(input, i) >= 0;
				};
				
			case PRECEDE:
				return (input, node, i) -> {
					return r.getBackwardsMatcher().match(input, i) == -1;
				};
				
			default:
				throw new RuntimeException("Unexpected error occured.");
		}
	}
	
	@Override
	public SlotAction getSlotAction() {
		return action;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if(!(obj instanceof RegularExpressionCondition)) 
			return false;
		
		RegularExpressionCondition other = (RegularExpressionCondition) obj;
		
		return type == other.type && regularExpression.equals(other.regularExpression);
	}
	
	@Override
	public int hashCode() {
		return type.hashCode() * 31 + regularExpression.hashCode();
	}

	@Override
	public String getConstructorCode() {
		return "new " + RegularExpressionCondition.class.getSimpleName() + "(ConditionType." + type.name() + ", " + regularExpression.getConstructorCode() + ")";
	}
	
	public static RegularExpressionCondition notMatch(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition match(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.MATCH, regularExpression);
	}
	
	public static RegularExpressionCondition notFollow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition notFollowIgnoreLayout(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_FOLLOW_IGNORE_LAYOUT, regularExpression);
	}
	
	public static RegularExpressionCondition follow(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.FOLLOW, regularExpression);
	}
	
	public static RegularExpressionCondition precede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.PRECEDE, regularExpression);
	}
	
	public static RegularExpressionCondition notPrecede(RegularExpression regularExpression) {
		return new RegularExpressionCondition(ConditionType.NOT_PRECEDE, regularExpression);
	}

	@Override
	public <T> T accept(IConditionVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
