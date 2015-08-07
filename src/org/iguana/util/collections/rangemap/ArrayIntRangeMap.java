package org.iguana.util.collections.rangemap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.iguana.grammar.symbol.CharacterRange;
import org.iguana.util.collections.rangemap.AVLIntRangeTree.IntNode;

public class ArrayIntRangeMap implements IntRangeTree {

	public static final int ABSENT_VALUE = -2;
	
	private int[] starts;
	private int[] ends;
	private int[] vals;
	
	public ArrayIntRangeMap(IntRangeTree t) {
		this(t, ABSENT_VALUE);
	}

	public ArrayIntRangeMap(IntRangeTree t, int absentValue) {
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
	public boolean contains(CharacterRange range) {
		throw new UnsupportedOperationException();
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
