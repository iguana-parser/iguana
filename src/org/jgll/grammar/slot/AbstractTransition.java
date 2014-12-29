package org.jgll.grammar.slot;

import org.jgll.parser.AbstractGLLParserImpl;
import org.jgll.util.logging.LoggerWrapper;



public abstract class AbstractTransition implements Transition {
	
	protected static final LoggerWrapper log = LoggerWrapper.getLogger(AbstractGLLParserImpl.class);
	
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
