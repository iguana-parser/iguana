package org.jgll.grammar.patterns;

import java.io.Serializable;
import java.util.List;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Symbol;


public class PrecedencePattern extends AbstractPattern implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public PrecedencePattern(Nonterminal nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		super(nonteriminal, parent, position, child);
	}

	/**
	 * A direct filter is of the form (E, alpha .E beta, gamma).
	 * In other words, the filtered nonterminal is the same
	 * as the filter's nonterminal.
	 */
	public boolean isDirect() {
		return nonterminal.equals(parent.get(position));
	}
	
	public boolean isParentBinary() {
		return nonterminal.equals(parent.get(0)) && nonterminal.equals(parent.get(parent.size() - 1));
	}
	
	public boolean isChildBinary() {
		return nonterminal.equals(child.get(0)) && nonterminal.equals(child.get(child.size() - 1));
	}
	
	public boolean isLeftMost() {
		return position == 0;
	}
	
	public boolean isRightMost() {
		return position == parent.size() - 1;
	}
}
