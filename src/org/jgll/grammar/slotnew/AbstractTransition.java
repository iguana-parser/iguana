package org.jgll.grammar.slotnew;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.condition.Condition;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.nodecreator.NodeCreator;


public abstract class AbstractTransition implements Transition {
	
	protected final Set<Condition> preConditions;
	
	protected final Set<Condition> postConditions;

	protected final GrammarSlot dest;

	protected final GrammarSlot origin;

	protected final NodeCreator nodeCreator;

	public AbstractTransition(GrammarSlot origin, 
							  GrammarSlot dest, 
							  Set<Condition> preConditions, 
							  Set<Condition> postConditions,
							  NodeCreator nodeCreator) {
		this.origin = origin;
		this.dest = dest;
		this.preConditions = new HashSet<>(preConditions);
		this.postConditions = new HashSet<>(postConditions);
		this.nodeCreator = nodeCreator;
	}

	@Override
	public GrammarSlot destination() {
		return dest;
	}

	@Override
	public GrammarSlot origin() {
		return origin;
	}
	
	public Set<Condition> getPostConditions() {
		return postConditions;
	}
	
	public Set<Condition> getPreConditions() {
		return preConditions;
	}

}
