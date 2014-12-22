package org.jgll.grammar.slotnew;

import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.GrammarSlot;


public abstract class AbstractTransition implements Transition {
	
	protected Set<Condition> preConditions;
	
	protected Set<Condition> postConditions;

	protected GrammarSlot dest;

	protected GrammarSlot origin;

	public AbstractTransition(GrammarSlot origin, GrammarSlot dest, Set<Condition> preConditions, Set<Condition> postConditions) {
		this.origin = origin;
		this.dest = dest;
		this.preConditions = preConditions;
		this.postConditions = postConditions;
	}

	@Override
	public GrammarSlot destination() {
		return dest;
	}

	@Override
	public GrammarSlot origin() {
		return origin;
	}

}
