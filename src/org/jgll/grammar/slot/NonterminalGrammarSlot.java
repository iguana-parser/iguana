package org.jgll.grammar.slot;

import java.util.List;

import org.jgll.grammar.symbol.Nonterminal;


public class NonterminalGrammarSlot extends ChoiceGrammarSlot {

	private Nonterminal nonterminal;

	public NonterminalGrammarSlot(Nonterminal nonterminal, List<Transition> transitions) {
		super(transitions);
		this.nonterminal = nonterminal;
	}
	
	public boolean test(int v)  {
		return false;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}

}
