package org.iguana.util.collections.rangemap;

import org.iguana.util.collections.rangemap.AVLIntRangeTree.IntNode;

public interface RangeTrees {

	public static IntRangeTree makeComplete(IntRangeTree t) {
		t.preOrder(n -> {
			if (n.left == null)  n.left = new IntNode(-1, -1, -1);
			if (n.right == null) n.right = new IntNode(-1, -1, -1);
		});
		return t;
	}

}
