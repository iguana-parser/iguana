package org.jgll.grammar;

import java.util.List;


class Filter {
	
	private List<Symbol> parent;
	private List<Symbol> child;
	private final int position;
	
	public Filter(List<Symbol> parent, int position, List<Symbol> child) {
		if(parent == null || child == null) {
			throw new IllegalArgumentException("parent or child alternates cannot be null.");
		}
		
		this.parent = parent;
		this.position = position;
		this.child = child;
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
	
	public boolean isLeftMost() {
		return position == 0;
	}
	
	public boolean isRightMost() {
		return position == child.size() - 1;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(")
		  .append(parent).append(", ").append(position)
		  .append("\\ ").append(child)
		  .append(")");
		return sb.toString();
	}
	
}
