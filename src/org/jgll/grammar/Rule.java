package org.jgll.grammar;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Rule implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> body;
	private final Nonterminal head;

	public Rule(Nonterminal head, List<Symbol> body) {
		this.head = head;
		this.body = body;
	}
	
	public Nonterminal getHead() {
		return head;
	}
	
	public List<Symbol> getBody() {
		return body;
	}

}
