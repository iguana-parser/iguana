package org.jgll.parser;

import java.util.ArrayList;
import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.sppf.NonPackedNode;
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
	public static final GSSNode U0 = new GSSNode(L0.getInstance(), 0, 0);

	private final HeadGrammarSlot head;

	private final int inputIndex;
	
	private List<GSSNode> children;
	
	private NonPackedNode[] poppedElements;

	private final int hash;
	
	/**
	 * Creates a new {@code GSSNode} with the given {@code label},
	 * {@code position} and {@code index}
	 * 
	 * @param slot
	 * @param inputIndex
	 */
	public GSSNode(HeadGrammarSlot head, int inputIndex, int inputSize) {
		this.head = head;
		this.inputIndex = inputIndex;
		
		children = new ArrayList<>();
		
		// Each GSS nodes can be popped from the GSS node's input index to the
		// the length of input index.
		poppedElements = new NonPackedNode[inputSize - inputIndex];
		
		this.hash = externalHasher.hash(this, HashFunctions.defaulFunction());
	}
	
	public void addToPoppedElements(int i, NonPackedNode node) {
		poppedElements[i - inputIndex] = node;
	}
	
	public NonPackedNode[] getPoppedElements() {
		return poppedElements;
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
		return head;
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

		return  head == other.head &&
				inputIndex == other.inputIndex;
	}

	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		return "(" + head + "," + inputIndex + ")";
	}

	public static class GSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.head.getId(), node.inputIndex);
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.head.getId() == g2.head.getId() &&
				   g1.inputIndex == g2.inputIndex;
		}
	}
	
	public static class LevelBasedGSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.head.getId());
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.head == g2.head;
		}
	}


}