package org.jgll.grammar.slot;

import org.jgll.grammar.HeadGrammarSlot;

public class FirstNonterminalGrammarSlot extends NonterminalGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public FirstNonterminalGrammarSlot(String label, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(label, 0, null, nonterminal, head);
	}

}
