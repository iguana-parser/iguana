package org.jgll.sppf;

import org.jgll.util.HashCode;

public abstract class NonPackedNode extends SPPFNode {
	
	protected final int grammarIndex;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	private final int hash;
	
	/**
	 * @param grammarIndex
	 * @param leftExtent
	 * @param rightExtent
	 * @param symbolName
	 * @param nonterminal a boolean value indicating whether the symbol node represents a nonterminal.
	 * 					  if false, the symbol node represents a terminal.
	 */
	public NonPackedNode(int grammarIndex, int leftExtent, int rightExtent) {
		this.grammarIndex = grammarIndex;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		
		hash = HashCode.hashCode(grammarIndex, leftExtent, rightExtent);
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	public abstract void addPackedNode(PackedNode newPackedNode, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public abstract boolean isAmbiguous();
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof NonPackedNode)) {
			return false;
		}
		
		NonPackedNode other = (NonPackedNode) obj;

		return  grammarIndex == other.grammarIndex &&
				leftExtent == other.leftExtent &&
				rightExtent == other.rightExtent;
	}
	
	public int getGrammarIndex() {
		return grammarIndex;
	}
	
	public int getLeftExtent() {
		return leftExtent;
	}

	public int getRightExtent() {
		return rightExtent;
	}
	
	public String toString() {
		return String.format("(%s, %d, %d)", getLabel(), leftExtent, rightExtent);
	}
	
	@Override
	public String getId() {
		return grammarIndex + "," + leftExtent + "," + rightExtent;
	}

}
