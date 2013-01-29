package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

public class TerminalSymbolNode extends NonPackedNode {
	
	public TerminalSymbolNode(int grammarIndex, int leftExtent, int rightExtent, String lexeme) {
		super(grammarIndex, leftExtent, rightExtent);
	}	
	
	public TerminalSymbolNode(int grammarIndex, int leftExtent, int rightExtent) {
		super(grammarIndex, leftExtent, rightExtent);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof TerminalSymbolNode)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String getLabel() {
		return grammarIndex + "";
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", grammarIndex, leftExtent, rightExtent);
	}
	
	@Override
	public String getId() {
		return "t" + grammarIndex + "," + leftExtent + "," + rightExtent;
	}

	@Override
	public void addChild(SPPFNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int sizeChildren() {
		return 0;
	}

	@Override
	public void replaceByChildren(SPPFNode node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SPPFNode> getChildren() {
		return new ArrayList<SPPFNode>();
	}

	@Override
	public SPPFNode firstChild() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeChild(SPPFNode node) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void setChildren(List<SPPFNode> children) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SPPFNode childAt(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeChildren(List<SPPFNode> node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public void addPackedNode(PackedNode newPackedNode, NonPackedNode leftChild, NonPackedNode rightChild) {
		throw new UnsupportedOperationException();
	}

}
