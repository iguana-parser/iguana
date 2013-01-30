package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;


/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {

	/**
	 * 
	 */
	private final Nonterminal head;
	
	public LastGrammarSlot(int id, String label, int position, Nonterminal head, BodyGrammarSlot previous) {
		super(id, label, position, previous);
		this.head = head;
	}
	
	public Nonterminal getHead() {
		return head;
	}

	@Override
	public void code(Writer writer) throws IOException {
		
	}

	@Override
	public Set<Terminal> getTestSet() {
		return null;
	}

}
