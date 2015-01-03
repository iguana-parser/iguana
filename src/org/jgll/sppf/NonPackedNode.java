package org.jgll.sppf;

import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.util.hashing.ExternalHashEquals;
import org.jgll.util.hashing.hashfunction.HashFunction;


/**
 * 
 * A NonPackedNode is the abstract super class for nonterminal 
 * and intermediate symbol nodes.
 * 
 * 
 * @author Ali Afroozeh
 * 
 */

public abstract class NonPackedNode implements SPPFNode {
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	private final ExternalHashEquals<NonPackedNode> hashEquals;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent, ExternalHashEquals<NonPackedNode> hashEquals) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
		this.hashEquals = hashEquals;
	}

	public int getLeftExtent() {
		return leftExtent;
	}
	
	public int getRightExtent() {
		return rightExtent;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if (!(obj instanceof NonPackedNode)) 
			return false;
		
		return hashEquals.equals(this, (NonPackedNode) obj);
	}

	@Override
	public int hashCode() {
		return hashEquals.hash(this);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot, getLeftExtent(), getRightExtent());
	}
	
	public abstract List<PackedNode> getChildren();
	
    public static ExternalHashEquals<NonPackedNode> globalHashEquals(HashFunction f) {
    	
    	return new ExternalHashEquals<NonPackedNode>() {

			@Override
			public int hash(NonPackedNode n) {
				return f.hash(n.getGrammarSlot().hashCode(), 
						      n.getLeftExtent(), 
						      n.getRightExtent());
			}

			@Override
			public boolean equals(NonPackedNode n1, NonPackedNode n2) {
				return  n1.getGrammarSlot() == n2.getGrammarSlot() && 
                        n1.getLeftExtent()  == n2.getLeftExtent() && 
                        n1.getRightExtent() == n2.getRightExtent();
			}
		};
    }
    
    public static ExternalHashEquals<NonPackedNode> distributedHashEquals(HashFunction f) {
    	
    	return new ExternalHashEquals<NonPackedNode>() {

			@Override
			public int hash(NonPackedNode n) {
				return f.hash(n.getLeftExtent(), n.getRightExtent());
			}

			@Override
			public boolean equals(NonPackedNode n1, NonPackedNode n2) {
				return  n1.getLeftExtent()  == n2.getLeftExtent() && 
                        n1.getRightExtent() == n2.getRightExtent();
			}
		};
    }    
	
}
