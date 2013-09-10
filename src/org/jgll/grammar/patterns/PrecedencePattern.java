package org.jgll.grammar.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.Symbol;
import org.jgll.parser.HashFunctions;


public class PrecedencePattern implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final List<Symbol> parent;
	private final List<Symbol> child;
	private final int position;
	private final String nonterminal;
	
	public PrecedencePattern(String nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
		if(parent == null || child == null) {
			throw new IllegalArgumentException("parent or child alternates cannot be null.");
		}
		
		this.parent = new ArrayList<>(parent);
		this.position = position;
		this.child = new ArrayList<>(child);
		this.nonterminal = nonteriminal;
	}
		
	public int getPosition() {
		return position;
	}
	
	public List<Symbol> getParent() {
		return parent;
	}
	
	public List<Symbol> getChild() {
		return child;
	}
	
	public String getNonterminal() {
		return nonterminal;
	}
	
	public String getFilteredNontemrinalName() {
		return parent.get(position).getName();
	}
	
	/**
	 * A direct filter is of the form (E, alpha .E beta, gamma).
	 * In other words, the filtered nonterminal is the same
	 * as the filter's nonterminal.
	 */
	public boolean isDirect() {
		return nonterminal.equals(parent.get(position).getName());
	}
	
	public boolean isParentBinary() {
		return nonterminal.equals(parent.get(0).getName()) && nonterminal.equals(parent.get(parent.size() - 1).getName());
	}
	
	public boolean isChildBinary() {
		return nonterminal.equals(child.get(0).getClass()) && nonterminal.equals(child.get(child.size() - 1).getName());
	}
	
	public boolean isLeftMost() {
		return position == 0;
	}
	
	public boolean isRightMost() {
		return position == child.size() - 1;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(nonterminal.hashCode(), position, parent.hashCode());
	}
	
	/**
	 * Two filters are equal if they can be applied to the same alternate.
	 * Let F1 be (nonterminal1, parent1, position1, child1) and
	 * F2 be (nonterminal2, parent2, position2, child2). Then F1 equals F2
	 * if nontermainl1 == nontermianl1, parent1 == parent2 and
	 * position1 == position2.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof PrecedencePattern)) {
			return false;
		}
		
		PrecedencePattern other = (PrecedencePattern) obj;
		
		return other.nonterminal.equals(this.nonterminal) &&
			   other.position == this.position && 
			   other.parent.equals(this.parent);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(nonterminal);
		sb.append(", ");
		
		int i = 0;
		for (Symbol symbol : parent) {
			if (i == position) {
				sb.append(". ");
			}
			sb.append(symbol).append(" ");
			i++;
		}

		sb.delete(sb.length() - 1, sb.length());
		sb.append(", ");

		i = 0;
		for (Symbol symbol : child) {
			sb.append(symbol).append(" ");
		}
		sb.delete(sb.length() - 1, sb.length());

		sb.append(")");
		return sb.toString();
	}

}
