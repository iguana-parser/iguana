package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgll.grammar.condition.Condition;
import org.jgll.parser.HashFunctions;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Rule implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> body;
	
	private final Nonterminal head;
	
	private final Serializable object;
	
	private List<Condition> conditions;
	
	public Rule(Nonterminal head) {
		this(head, new ArrayList<Symbol>(), null);
	}
	
	private Rule(Builder builder) {
		this(builder.head, builder.body);
	}
	
	public Rule(Nonterminal head, Symbol...body) {
		this(head, Arrays.asList(body), null);
	}
	
	public Rule(Nonterminal head, List<? extends Symbol> body) {
		this(head, body, null);
	}
	
	public Rule(Nonterminal head, List<? extends Symbol> body, Serializable object) {
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
		
		this.conditions = new ArrayList<>();
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
	
	public Serializable getObject() {
		return object;
	}
		
	public Rule addCondition(Condition condition) {
		conditions.add(condition);
		return this;
	}
	
	public Iterable<Condition> getConditions() {
		return conditions;
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
		return HashFunctions.defaulFunction().hash(head.hashCode(), body.hashCode());
	}
	
	
	public static class Builder {
		private Nonterminal head;
		private List<Symbol> body;

		public Builder(Nonterminal head) {
			this.head = head;
			this.body = new ArrayList<>();
		}
		
		public Builder addSymbol(Symbol symbol) {
			body.add(symbol);
			return this;
		}
		
		public Rule build() {
			return new Rule(this);
		}
	}
}
