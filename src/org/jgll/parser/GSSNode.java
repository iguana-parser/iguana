package org.jgll.parser;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public class GSSNode {
	
	public static final ExternalHasher<GSSNode> externalHasher = new GSSNodeExternalHasher();
	public static final ExternalHasher<GSSNode> levelBasedExternalHasher = new LevelBasedGSSNodeExternalHasher();

	/**
	 * The initial GSS node
	 */
	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0);

	private final HeadGrammarSlot slot;

	private final int inputIndex;
	
	private List<GSSNode> children;

	private final int hash;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public GSSNode(HeadGrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
		
		children = new ArrayList<>();
		
		this.hash = externalHasher.hash(this, HashFunctions.defaulFunction());
	}
		
	public Iterable<GSSNode> getChildren() {
		return children;
	}
	
	// TODO: child or parent
	public void addChild(GSSNode node) {
		children.add(node);
	}
	
	public int sizeChildren() {
		return children.size();
	}
		
	public GrammarSlot getGrammarSlot() {
		return slot;
	}

	public int getInputIndex() {
		return inputIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}

		if (!(obj instanceof GSSNode)) {
			return false;
		}
		
		GSSNode other = (GSSNode) obj;

		return  slot == other.slot &&
				inputIndex == other.inputIndex;
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + slot + "," + inputIndex + ")";
	}

	public static class GSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.slot.getId(), node.inputIndex);
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.slot.getId() == g2.slot.getId() &&
				   g1.inputIndex == g2.inputIndex;
		}
	}
	
	public static class LevelBasedGSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.slot.getId());
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.slot == g2.slot;
		}
	}


}