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
	
	public Rule(Builder builder) {
		this.head = builder.head;
		this.object = builder.object;
		this.body = builder.body;
	}
	
	public Rule(Nonterminal head, List<Symbol> body, Object object) {
		if(head == null) {
			throw new IllegalArgumentException("head cannot be null.");
		}
		if(body == null) {
			throw new IllegalArgumentException("Object cannot be null.");
		}
		this.head = head;
		this.body = body;
		this.object = object;
	}
	
	public Nonterminal getHead() {
		return head;
	}
	
	public Iterable<Symbol> getBody() {
		return body;
	}
	
	public int getBodyLength() {
		return body.size();
	}
	
	public Object getObject() {
		return object;
	}
	
	public static class Builder {
		private List<Symbol> body = new ArrayList<>();
		private Nonterminal head;
		private Object object;
		
		public Builder head(Nonterminal head) {
			this.head = head;
			return this;
		}
		
		public Builder addSymbol(Symbol s) {
			body.add(s);
			return this;
		}
		
		public Builder body(Symbol...symbols) {
			for(Symbol s : symbols) {
				body.add(s);
			}
			return this;
		}
		
		public Builder addObject(Object object) {
			this.object = object;
			return this;
		}
		
		public Rule build() {
			return new Rule(this);
		}
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
}
