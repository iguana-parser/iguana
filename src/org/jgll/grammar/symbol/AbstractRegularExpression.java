package org.jgll.grammar.symbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.regex.RegularExpression;
import org.jgll.regex.StateAction;


public abstract class AbstractRegularExpression extends AbstractSymbol implements RegularExpression {

	private static final long serialVersionUID = 1L;
	
	protected List<StateAction> actions;

	public AbstractRegularExpression(String name) {
		super(name);
		actions = new ArrayList<>();
	}
	
	@Override
	public void addFinalStateAction(StateAction action) {
		actions.add(action);
	}
	
	@Override
	public RegularExpression addConditions(Collection<Condition> conditions) {
		return (RegularExpression) super.addConditions(conditions);
	}

}
