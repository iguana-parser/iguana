package org.jgll.grammar.slot;

import java.util.Set;

import org.jgll.grammar.symbol.Nonterminal;


public class NonterminalGrammarSlot extends BodyGrammarSlot {

	private final Nonterminal nonterminal;

	public NonterminalGrammarSlot(Nonterminal nonterminal, Set<Transition> transitions) {
		super(transitions);
		this.nonterminal = nonterminal;
	}
	
	public NonterminalGrammarSlot(Nonterminal nonterminal) {
		this.nonterminal = nonterminal;
	}
	
	public boolean test(int v)  {
		return false;
	}
	
	public Nonterminal getNonterminal() {
		return nonterminal;
	}
	
	@Override
	public String toString() {
		return nonterminal.getName();
	}
	
}
