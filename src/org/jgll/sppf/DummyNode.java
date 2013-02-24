package org.jgll.sppf;

import org.jgll.traversal.SPPFVisitor;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class DummyNode extends SPPFNode {
	
	private static DummyNode instance;
	
	public static DummyNode getInstance() {
		if(instance == null) {
			instance = new DummyNode();
		}
		return instance;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DummyNode;
	}
	
	private DummyNode() {}

	@Override
	public String getId() {
		return "-1";
	}

	@Override
	public String getLabel() {
		return "$";
	}

	@Override
	public int getLeftExtent() {
		return -1;
	}

	@Override
	public int getRightExtent() {
		return -1;
	}

	@Override
	public void accept(SPPFVisitor visitAction) {
		// do nothing
	}

	@Override
	public SPPFNode get(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Iterable<SPPFNode> getChildren() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		return "$";
	}

}
