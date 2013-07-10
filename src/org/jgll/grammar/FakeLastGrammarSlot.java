package org.jgll.grammar;

import org.jgll.parser.GLLParser;
import org.jgll.util.Input;

public class FakeLastGrammarSlot extends LastGrammarSlot {

	public FakeLastGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot head, Object object) {
		super(label, position, previous, head, object);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		// do nothing
		return null;
	}

}
