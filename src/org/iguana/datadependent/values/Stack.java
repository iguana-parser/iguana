package org.iguana.datadependent.values;

import org.iguana.util.hashing.hashfunction.MurmurHash3;

public class Stack<T> {
	
	private static MurmurHash3 f = new MurmurHash3();
	
	private final Stack<T> parent;
	private final T top;
	
	private int size;
	private int hash;
	
	Stack(T top) {
		this.parent = null;
		this.top = top;
		this.size = 1;
		this.hash = f.hash(top.hashCode());
	}
	
	Stack(T top, Stack<T> parent) {
		this.parent = parent;
		this.top = top;
		this.size = parent.size + 1;
		
		int[] hs = new int[this.size];
		Stack<T> root = this;
		int i = 0;
		while(root != null) {
			hs[i++] = root.top.hashCode();
			root = root.parent;
		}
		
		this.hash = f.hash(hs);
	}
	
	public static <T> Stack<T> from(T top) {
		return new Stack<>(top);
	}
	
	public T top() {
		return this.top;
	}
	
	public Stack<T> push(T top) {
		return new Stack<>(top, this);
	}
	
	public Stack<T> pop() {
		return parent;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Stack<?>))
			return false;
		
		Stack<?> other = (Stack<?>) obj;
		
		if (top.equals(other.top)) {
			if (parent == null)
				return other.parent == null;
			else
				return parent.equals(other.parent);
			
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return hash;
	}
	
	@Override
	public String toString() {
		if (parent == null)
			return top.toString();
		
		return top.toString() + "," + parent.toString();
	}
}
