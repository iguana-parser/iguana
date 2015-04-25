package org.iguana.traversal;

import org.iguana.grammar.condition.ContextFreeCondition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;

public interface IConditionVisitor<T> {
	
	public T visit(ContextFreeCondition condition);
	
	public T visit(DataDependentCondition condition);
	
	public T visit(PositionalCondition condition);
	
	public T visit(RegularExpressionCondition condition);

}
