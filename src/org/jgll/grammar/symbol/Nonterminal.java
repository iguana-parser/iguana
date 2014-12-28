package org.jgll.grammar.symbol;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.HashFunctions;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	public static Nonterminal withName(String name) {
		return new Builder(name).build();
	}
	
	private Nonterminal(Builder builder) {
		super(builder);
		this.ebnfList = builder.ebnfList;
		this.index = builder.index;
	}
	
	public boolean isEbnfList() {
		if (ebnfList == true) {
			return true;
		} else {
			if(name.startsWith("List")) {
				return true;
			}
		}

		return false;
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		return index > 0 ? name + index : name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Nonterminal)) {
			return false;
		}
		
		Nonterminal other = (Nonterminal) obj;
		
		return name.equals(other.name) && index == other.index;
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(name.hashCode(), index);
	}
	
	public static Builder builder(String name) {
		return new Builder(name);
	}

	public static class Builder extends SymbolBuilder<Nonterminal> {

		private boolean ebnfList;
		
		private int index;
		
		public Builder(Nonterminal nonterminal) {
			super(nonterminal);
			this.name = nonterminal.name;
			this.ebnfList = nonterminal.ebnfList;
			this.index = nonterminal.index;
		}

		public Builder(String name) {
			super(name);
		}
		
		public Builder setIndex(int index) {
			this.index = index;
			return this;
		}
		
		public Builder setEbnfList(boolean ebnfList) {
			this.ebnfList = ebnfList;
			return this;
		}
		
		@Override
		public Nonterminal build() {
			return new Nonterminal(this);
		}
	}

	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		return new StringBuilder()
		  .append("Nonterminal.builder(\"" + name + "\")")
		  .append(label == null? "" : ".setLabel(" + label + ")")
		  .append(object == null? "" : ".setObject(" + object + ")")
		  .append(preConditions.isEmpty()? "" : ".setPreConditions(" + getConstructorCode(preConditions, registry) + ")")
		  .append(postConditions.isEmpty()? "" : ".setPostConditions(" + getConstructorCode(postConditions, registry) + ")")
		  .append(index == 0 ? "" : ".setIndex(" + index + ")")
		  .append(ebnfList == false? "" : ".setEbnfList(" + ebnfList + ")")
		  .append(".build()").toString();
	}
	
}
