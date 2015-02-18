package org.jgll.grammar.symbol;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jgll.parser.HashFunctions;
import org.jgll.util.generator.ConstructorCode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class Rule implements ConstructorCode, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> body;
	
	private final Nonterminal head;
	
	/**
	 * An arbitrary data object that can be put in this grammar slot and
	 * retrieved later when traversing the parse tree.
	 * This object can be accessed via the getObject() method of a nonterminal symbol node.
	 */
	private final Serializable object;
	
	private final Nonterminal layout;
	
	private final LayoutStrategy layoutStrategy;
	
	public Rule(Builder builder) {
		this.body = builder.body;
		this.head = builder.head;
		this.object = builder.object;
		this.layout = builder.layout;
		this.layoutStrategy = builder.layoutStrategy;
	}
		
	public Nonterminal getHead() {
		return head;
	}
	
	public List<Symbol> getBody() {
		return body;
	}
	
	public int size() {
		return body == null ? 0 : body.size();
	}
	
	public Symbol symbolAt(int i) {
		if (i > body.size())
			throw new IllegalArgumentException(i + " cannot be greater than " + body.size());
		
		return body.get(i);
	}
	
	public Serializable getObject() {
		return object;
	}
	
	public Nonterminal getLayout() {
		return layout;
	}
	
	public LayoutStrategy getLayoutStrategy() {
		return layoutStrategy;
	}
	
	public boolean hasLayout() {
		return layout != null;
	}
		
	@Override
	public String toString() {
		
		if(body == null) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(head).append(" ::= ");
		for(Symbol s : body) {
			sb.append(s).append(" ");
		}
		return sb.toString();
	} 
	
	public boolean equals(Object obj) {
		
		if(this == obj)
			return true;
		
		if(!(obj instanceof Rule))
			return false;
		
		Rule other = (Rule) obj;
		
		return head.equals(other.head) && body == null ? other.body == null : body.equals(other.body);
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(head.hashCode(), body == null ? 0 : body.hashCode());
	}
	
	public Position getPosition(int i) {
		if (i < 0)
			throw new IllegalArgumentException("i cannot be less than zero.");
		
		if (i > size())
			throw new IllegalArgumentException("i cannot be greater than the size.");
		
		return new Position(this, i);
	}
	
	public static Builder withHead(Nonterminal nonterminal) {
		return new Builder(nonterminal);
	}
	
	public static class Builder {
		
		private Nonterminal head;
		private List<Symbol> body;
		private Serializable object;
		private LayoutStrategy layoutStrategy = LayoutStrategy.INHERITED;
		private Nonterminal layout;

		public Builder(Nonterminal head) {
			this.head = head;
			this.body = new ArrayList<>();
		}
		
		public Builder addSymbol(Symbol symbol) {
			body.add(symbol);
			return this;
		}
		
		public Builder addSymbols(Symbol...symbols) {
			body.addAll(Arrays.asList(symbols));
			return this;
		}
		
		public Builder addSymbols(List<Symbol> symbols) {
			if (symbols == null) {
				body = null;
			} else {
				body.addAll(symbols);
			}
			return this;
		}
		
		public Builder setLayoutStrategy(LayoutStrategy layoutStrategy) {
			this.layoutStrategy = layoutStrategy;
			return this;
		}
		
		public Builder setObject(Serializable object) {
			this.object = object;
			return this;
		}
		
		public Builder setLayout(Nonterminal layout) {
			this.layout = layout;
			return this;
		}
		
		public Rule build() {
			return new Rule(this);
		}
	}

	@Override
	public String getConstructorCode() {
		return Rule.class.getSimpleName() + ".withHead(" + head.getConstructorCode() + ")" + 
				(body == null ? "" : body.stream().map(s -> ".addSymbol(" + s.getConstructorCode() + ")").collect(Collectors.joining())) +
				(layout == null ? "" : ".setLayout(" + layout.getConstructorCode() + ")") +
				(layoutStrategy == LayoutStrategy.INHERITED ? "" : ".setLayoutStrategy(" + layoutStrategy + ")") +
				".build()";
	}
}
