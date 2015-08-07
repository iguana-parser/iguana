package org.iguana.util.collections.rangemap;

import org.iguana.util.collections.rangemap.AVLIntRangeTree.IntNode;

public interface RangeTrees {

	public static IntRangeTree makeComplete(IntRangeTree t, int absentValue) {
		
		int height = t.height();
		t.preOrder(n -> { n.height = n.parent == null ? 0 : n.parent.height + 1; });
		
		t.preOrder(n -> {
			if (n.height != height) {
				if (n.left == null)  {
					n.left = new IntNode(absentValue, 0, 0);
					n.left.height = n.height + 1;
				}
				if (n.right == null) {
					n.right = new IntNode(absentValue, 0, 0);
					n.right.height = n.height + 1;
				}
			}
		});
		return t;
	}

}
