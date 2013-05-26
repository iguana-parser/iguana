package org.jgll.grammar;

import java.io.Serializable;
import java.util.ArrayList;
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
	
	public Rule(Nonterminal head, List<? extends Symbol> body) {
		this(head, body, null);
	}
	
	public Rule(Nonterminal head, List<? extends Symbol> body, Object object) {
		if(head == null) {
			throw new IllegalArgumentException("head cannot be null.");
		}
		if(body == null) {
			throw new IllegalArgumentException("Object cannot be null.");
		}
		for(Symbol s : body) {
			if(s == null) {
				throw new IllegalArgumentException("Body of a rule cannot have null symbols.");
			}
		}
		this.head = head;
		
		this.body = new ArrayList<>(body);
		
		this.object = object;
	}
		
	public Nonterminal getHead() {
		return head;
	}
	
	public List<Symbol> getBody() {
		return body;
	}
	
	public int size() {
		return body.size();
	}
	
	public Object getObject() {
		return object;
	}
	
	/**
	 * Returns the symbols at the given  
	 * 
	 * @throws IllegalArgumentException if {@code index} is greater than the number of body symbols.
	 */
	public Symbol getSymbolAt(int index) {
		if(index > body.size()) {
			throw new IllegalArgumentException(index + " cannot be greater than " + body.size());
		}
		
		return body.get(index);
	}
	
		
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(head).append(" ::= ");
		for(Symbol s : body) {
			sb.append(s).append(" ");
		}
		return sb.toString();
	} 
	
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Rule)) {
			return false;
		}
		
		Rule other = (Rule) obj;
		
		return head.equals(other.head) && body.equals(other.body);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result += 31 * result + head.hashCode();
		result += 31 * result + body.hashCode();
		return result;
	}
}
