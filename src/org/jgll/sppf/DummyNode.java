package org.jgll.sppf;

import java.util.ArrayList;
import java.util.List;

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
	public List<SPPFNode> getChildren() {
		return new ArrayList<>();
	}

	@Override
	public int getLeftExtent() {
		return -1;
	}

	@Override
	public int getRightExtent() {
		return -1;
	}

}
