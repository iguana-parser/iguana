package org.jgll.grammar.symbol;

import java.util.*;

import org.jgll.grammar.condition.*;
import org.jgll.parser.*;

public class Nonterminal extends AbstractSymbol {

	private static final long serialVersionUID = 1L;
	
	private final boolean ebnfList;
	
	private final int index;
	
	public static Nonterminal withName(String name) {
		return new Builder(name).build();
	}
	
	public Nonterminal(String name, int index, boolean ebnfList, Set<Condition> conditions, String label, Object object) {
		super(name, conditions, label, object);
		this.ebnfList = ebnfList;
		this.index = index;
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

	public static class Builder extends SymbolBuilder<Nonterminal> {

		private String name;
		
		private boolean ebnfList;
		
		private int index;
		
		public Builder(Nonterminal nonterminal) {
			super(nonterminal);
			this.name = nonterminal.name;
			this.ebnfList = nonterminal.ebnfList;
			this.index = nonterminal.index;
		}

		public Builder(String name) {
			this.name = name;
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
			return new Nonterminal(name, index, ebnfList, conditions, label, object);
		}
	}

	@Override
	public SymbolBuilder<? extends Nonterminal> builder() {
		return new Builder(this);
	}
	
}
