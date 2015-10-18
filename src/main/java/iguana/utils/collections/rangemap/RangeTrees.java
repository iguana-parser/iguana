package iguana.utils.collections.rangemap;

import iguana.utils.collections.rangemap.AVLIntRangeTree.IntNode;

public interface RangeTrees {

    static IntRangeTree makeComplete(IntRangeTree t, int absentValue) {
		
		if (t.getRoot() == null) return t;
		
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
