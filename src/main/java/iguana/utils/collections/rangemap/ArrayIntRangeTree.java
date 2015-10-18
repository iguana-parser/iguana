package iguana.utils.collections.rangemap;

import iguana.utils.collections.rangemap.AVLIntRangeTree.IntNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class ArrayIntRangeTree implements IntRangeTree {

	private int[] starts;
	private int[] ends;
	private int[] vals;
	
	public ArrayIntRangeTree(IntRangeTree t) {
		this(t, ABSENT_VALUE);
	}

	public ArrayIntRangeTree(IntRangeTree t, int absentValue) {
		int size = (int) Math.pow(2, t.height() + 1);
		starts = new int[size];
		ends   = new int[size];
		vals   = new int[size];
		
		Arrays.fill(starts, absentValue);
		Arrays.fill(ends, absentValue);
		Arrays.fill(vals, absentValue);
		
		t = RangeTrees.makeComplete(t, absentValue);
		
		List<IntNode> nodes = new ArrayList<>();
		t.levelOrder(n -> nodes.add(n));
		for (int i = 0; i < nodes.size(); i++) {
			starts[i] = nodes.get(i).start;
			ends[i]   = nodes.get(i).end;
			vals[i]   = nodes.get(i).val;
		}
	}
	
	@Override
	public IntNode getRoot() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return vals.length;
	}

	@Override
	public boolean contains(Range range) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int get(int key) {
		return get(0, key);
	}
	
	private int get(int i, int key) {
//		if (key < 0) return ABSENT_VALUE; // TODO: choose a better scheme for missing values and EOF.
		
		int val = ABSENT_VALUE;
		while (i < starts.length) {
			if (starts[i] == ABSENT_VALUE) break;
			
			if (key < starts[i]) {
				i = i * 2 + 1;
			}
			else if (key > ends[i]) {
				i = i * 2 + 2;
			} 
			else {
				val = vals[i];
				break;				
			}
		}
		
		return val;
	}

	@Override
	public void insert(Range range, int val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBalanced() {
		return true;
	}

	@Override
	public <T> void inOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public <T> void preOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> void levelOrder(Function<IntNode, ? extends T> action, Consumer<? super T> acc) {
		throw new UnsupportedOperationException();		
	}
	
}
