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
				sb.append(".");
			}
			sb.append(symbol);
			i++;
		}

		sb.append(" \\ ");

		i = 0;
		for (Symbol symbol : child) {
			sb.append(symbol);
		}

		sb.append(")");
		return sb.toString();
	}

}
