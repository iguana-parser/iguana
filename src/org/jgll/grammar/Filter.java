package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;


class Filter {
	
	private final List<Symbol> parent;
	private final List<Symbol> child;
	private final int position;
	private final String nonterminal;
	
	public Filter(String nonteriminal, List<Symbol> parent, int position, List<Symbol> child) {
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
	
	/**
	 * A direct filter is of the form (E, alpha .E beta, gamma).
	 * In other words, the filtered nonterminal is the same
	 * as the filter's nonterminal.
	 */
	public boolean isDirect() {
		return nonterminal.equals(parent.get(position).getName());
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
		return position == child.size() - 1;
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
