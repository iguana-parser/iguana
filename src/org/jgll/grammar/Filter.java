package org.jgll.grammar;


class Filter {
	
	private Alternate parent;
	private Alternate child;
	private final int position;
	
	public Filter(Alternate parent, int position, Alternate child) {
		if(parent == null || child == null) {
			throw new IllegalArgumentException("parent or child alternates cannot be null.");
		}
		
		this.parent = parent;
		this.position = position;
		this.child = child;
	}
		
	public Alternate getParent() {
		return parent;
	}
	
	public Alternate getChild() {
		return child;
	}
	
	public int getPosition() {
		return position;
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
		sb.append("(").append(parent.getHead()).append(", ")
		  .append(parent).append(", ").append(position)
		  .append("\\ ").append(child)
		  .append(")");
		return sb.toString();
	}
	
}
