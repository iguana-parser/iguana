package org.iguana.grammar.slot;


public abstract class AbstractTransition implements Transition {
	
	protected final BodyGrammarSlot dest;

	protected final BodyGrammarSlot origin;

	public AbstractTransition(BodyGrammarSlot origin, BodyGrammarSlot dest) {
		this.origin = origin;
		this.dest = dest;
	}

	@Override
	public GrammarSlot destination() {
		return dest;
	}

	@Override
	public GrammarSlot origin() {
		return origin;
	}
	
	@Override
	public String toString() {
		return origin + " ---> " + dest;
	}
}
