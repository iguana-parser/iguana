package org.jgll.grammar.slot;

import java.util.Collections;
import java.util.Set;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

public class EndGrammarSlot implements GrammarSlot {
	
	protected final NonterminalGrammarSlot nonterminal;

	public EndGrammarSlot(NonterminalGrammarSlot nonterminal) {
		this.nonterminal = nonterminal;
	}

	@Override
	public void execute(GLLParser parser, Input input, NonPackedNode node) {
		if (nonterminal.test(node.getRightExtent()))
			parser.pop();
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return null;
	}
	
	public Object getObject() {
		return null;
	}
	
	public NonterminalGrammarSlot getNonterminal() {
		return nonterminal;
	}

	@Override
	public Set<Transition> getTransitions() {
		return Collections.emptySet();
	}

	@Override
	public boolean addTransition(Transition transition) {
		return false;
	}

}
