package org.jgll.sppf;

public class IntermediateNode extends NonPackedNodeWithChildren {
		
	public IntermediateNode(int position, int leftExtent, int rightExtent) {
		super(position, leftExtent, rightExtent);
	}
	
	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof IntermediateNode)) {
			return false;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String getLabel() {
		return grammarIndex + "";
	}

}