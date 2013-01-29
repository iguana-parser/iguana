package org.jgll.grammar;

import java.util.Set;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends GrammarSlot {

	/**
	 * 
	 */
	private final Nonterminal head;
	
	public LastGrammarSlot(int id, String label, int position, Nonterminal head, GrammarSlot previous) {
		super(id, label, position, previous);
		this.head = head;
	}
	
	public Nonterminal getHead() {
		return head;
	}

	@Override
	public Set<Integer> getTestSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String code() {
		return null;
	}

}
