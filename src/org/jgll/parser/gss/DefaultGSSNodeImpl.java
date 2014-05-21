package org.jgll.parser.gss;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;

public class DefaultGSSNodeImpl implements GSSNode {

	@Override
	public void addToPoppedElements(NonPackedNode node) {
	}

	@Override
	public Iterable<NonPackedNode> getPoppedElements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<GSSNode> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(GSSNode node) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int sizeChildren() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HeadGrammarSlot getGrammarSlot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getGSSEdge(GSSNode destination, SPPFNode node, BodyGrammarSlot returnSlot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<GSSEdge> getGSSEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCountGSSEdges() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInputIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean addDescriptor(Descriptor descriptor) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clearDescriptors() {
		// TODO Auto-generated method stub
		
	}

}
