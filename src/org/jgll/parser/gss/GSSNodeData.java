package org.jgll.parser.gss;

import java.util.Iterator;

public class GSSNodeData<T> implements Iterable<T> {
	
	private final T[] data;
	
	public GSSNodeData(T[] data) {
		this.data = data;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof GSSNodeData)) {
			return false;
		}
		
		GSSNodeData<?> that = (GSSNodeData<?>) other;
		
		
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new GSSNodeDataIterator<T>(this);
	}
	
	static private class GSSNodeDataIterator<T> implements Iterator<T> {
		
		private GSSNodeData<T> data;
		
		private int i = 0;
		private final int length;
		
		GSSNodeDataIterator(GSSNodeData<T> data) {
			this.data = data;
			this.length = data.data.length;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			return null;
		}
		
	}

}
