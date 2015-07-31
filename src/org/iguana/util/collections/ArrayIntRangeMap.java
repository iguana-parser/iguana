package org.iguana.util.collections;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.IntRangeTree.IntNode;

public class ArrayIntRangeMap implements RangeIntMap {

	private int[] starts;
	private int[] ends;
	private int[] vals;

	public ArrayIntRangeMap(int[] starts, int[] ends, int[] vals) {
		this.starts = starts;
		this.ends = ends;
		this.vals = vals;
	}
	
	@Override
	public IntNode getRoot() {
		return null;
	}

	@Override
	public int size() {
		return vals.length;
	}

	@Override
	public boolean contains(CharacterRange range) {
		return false;
	}

	@Override
	public int get(int key) {
		return get(0, key);
	}
	
	private int get(int i, int key) {
		if (key < starts[i]) {
			return get(i * 2 + 1, key);
		}
		
		if (key > ends[i]) {
			return get(i * 2 + 2, key);
		}
		
		return vals[i];
	}

	@Override
	public void insert(CharacterRange range, int val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBalanced() {
		return false;
	}
	
}
