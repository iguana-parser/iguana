package org.jgll.sppf;

public class NonterminalSymbolNode extends NonPackedNodeWithChildren {
	
	public NonterminalSymbolNode(int grammarIndex, int leftExtent, int rightExtent) {
		super(grammarIndex, leftExtent, rightExtent);
	}
	
	@Override
	public void addChild(SPPFNode node) {
		super.addChild(node);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof NonterminalSymbolNode)) {
			return false;
		}
		
		return super.equals(obj);
	}

	@Override
	public String getLabel() {
		return grammarIndex + "";
	}

}
