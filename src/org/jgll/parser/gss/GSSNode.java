package org.jgll.parser.gss;

import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.L0;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.NonPackedNode;

/**
 *
 * @author Ali Afroozeh
 * 
 */
public interface GSSNode {
	
	public static final GSSNode U0 = new DummyGSSNode();
	
	public void addToPoppedElements(NonPackedNode node);
	
	public Iterable<NonPackedNode> getPoppedElements();
		
	public Iterable<GSSNode> getChildren();
	
	public void addChild(GSSNode node);
	
	public int sizeChildren();
		
	public GrammarSlot getGrammarSlot();
	
	public boolean getGSSEdge(GSSEdge edge);
	
	public Iterable<GSSEdge> getGSSEdges();
	
	public int getCountGSSEdges();

	public int getInputIndex();
	
	public boolean addDescriptor(Descriptor descriptor);
	
	public void clearDescriptors();
	
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
		public boolean getGSSEdge(GSSEdge edge) {
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