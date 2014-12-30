package org.jgll.grammar.slot;




public abstract class AbstractTransition implements Transition {
	
	protected final GrammarSlot dest;

	protected final GrammarSlot origin;

	public AbstractTransition(GrammarSlot origin, GrammarSlot dest) {
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
