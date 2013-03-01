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
	
	private final Object object;

	public Rule(Nonterminal head, List<Symbol> body, Object object) {
		this.head = head;
		this.body = body;
		this.object = object;
	}
	
	public Nonterminal getHead() {
		return head;
	}
	
	public List<Symbol> getBody() {
		return body;
	}
	
	public Object getObject() {
		return object;
	}

}
