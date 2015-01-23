package org.jgll.grammar.symbol;


public class LayoutPosition extends Position {

	public LayoutPosition(Position position) {
		super(position.getRule(), position.getPosition());
	}
	
	@Override
	public int getPosition() {
		return super.getPosition() + 1;
	}
	
	@Override
	public boolean isFirst() {
		return false;
	}
	
	@Override
	public boolean isLast() {
		return false;
	}
	
}
