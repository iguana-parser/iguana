package org.jgll.exception;

import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;

@SuppressWarnings("serial")
public class DisambiguationFailedException extends Exception {
	
	private final int leftExtent;
	private final int rightExtent;
	private final NonPackedNode node;

	public DisambiguationFailedException(NonPackedNode node) {
		this.node = node;
		this.leftExtent = node.getLeftExtent();
		this.rightExtent = node.getRightExtent();
	}
	
	@Override
	public String toString() {
		return "Disambiguation failed at node " + node.getLabel() ;
	}
	
	public int getLeftExtent() {
		return leftExtent;
	}
	
	public int getRightExtent() {
		return rightExtent;
	}
	
	public SPPFNode getNode() {
		return node;
	}
	
}
