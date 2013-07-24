package org.jgll.grammar.condition;

import java.util.List;

/**
 * 
 * Conditions relating to the literal exclusions or follow restrictions. 
 * For example, Id !>> "[]" or Id \ "if"
 * 
 * @author Ali Afroozeh
 *
 */
public class LiteralCondition extends Condition {
	
	private List<int[]> strings;
	
	public LiteralCondition(ConditionType type, List<int[]> list) {
		super(type);
		this.strings = list;
	}
	
	public List<int[]> getStrings() {
		return strings;
	}

}
