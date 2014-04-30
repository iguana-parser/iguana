package org.jgll.parser.gss;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.Descriptor;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.hashing.ExternalHasher;
import org.jgll.util.hashing.hashfunction.HashFunction;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public interface GSSNode {
	
	public static final ExternalHasher<GSSNode> externalHasher = new GSSNodeExternalHasher();
	
	public static final GSSNode U0 = new DummyGSSNode();
	
	public void addToPoppedElements(NonPackedNode node);
	
	public Iterable<NonPackedNode> getPoppedElements();
		
	public Iterable<GSSNode> getChildren();
	
	public void addChild(GSSNode node);
	
	public int sizeChildren();
		
	public HeadGrammarSlot getGrammarSlot();
	
	public boolean getGSSEdge(GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot);
	
	public Iterable<GSSEdge> getGSSEdges();
	
	public int getCountGSSEdges();

	public int getInputIndex();
	
	public boolean addDescriptor(Descriptor descriptor);
	
	public void clearDescriptors();

	public static class GSSNodeExternalHasher implements ExternalHasher<GSSNode> {
		
		private static final long serialVersionUID = 1L;

		@Override
		public int hash(GSSNode node, HashFunction f) {
			return f.hash(node.getGrammarSlot().getId(), node.getInputIndex());
		}

		@Override
		public boolean equals(GSSNode g1, GSSNode g2) {
			return g1.getGrammarSlot() == g2.getGrammarSlot() &&
				   g1.getInputIndex() == g2.getInputIndex();
		}
	}
	
	static class DummyGSSNode implements GSSNode {
		
		private Set<Descriptor> descriptors;
		
		public DummyGSSNode() {
			descriptors = new HashSet<>();
		}
		
		@Override
		public void addToPoppedElements(NonPackedNode node) {}

		@Override
		public Iterable<NonPackedNode> getPoppedElements() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterable<GSSNode> getChildren() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int sizeChildren() {
			return 0;
		}

		@Override
		public HeadGrammarSlot getGrammarSlot() {
			return L0.getInstance();
		}

		@Override
		public int getInputIndex() {
			return 0;
		}

		@Override
		public void addChild(GSSNode node) {}
		
		@Override
		public String toString() {
			return "U0";
		}

		@Override
		public boolean getGSSEdge(GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterable<GSSEdge> getGSSEdges() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getCountGSSEdges() {
			return 0;
		}

		@Override
		public boolean addDescriptor(Descriptor descriptor) {
			return descriptors.add(descriptor);
		}

		@Override
		public void clearDescriptors() {
			descriptors.clear();
		}
	}

}