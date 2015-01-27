package org.jgll.parser.gss;

import java.util.Iterator;

public class GSSNodeData<T> implements Iterable<T> {
	
	private final T[] elements;
	
	public final int size;
	
	public GSSNodeData(T[] elements) {
		this.elements = elements;
		this.size = elements == null? 0 : elements.length;
	}
	
	public T[] getValues() {
		return elements;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		
		if (!(other instanceof GSSNodeData)) {
			return false;
		}
		
		GSSNodeData<?> that = (GSSNodeData<?>) other;
		
		if (this.size != that.size) return false;
		
		Iterator<T> iter1 = iterator();
		Iterator<?> iter2 = that.iterator();
		
		while (iter1.hasNext()) {
			if (!iter1.next().equals(iter2.next())) {
				return false;
			};
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		
		for (T element : elements) {
			result = 31 * result + element.hashCode();
		}
		
		return result;
		
	}

	@Override
	public Iterator<T> iterator() {
		return new GSSNodeDataIterator<T>(this);
	}
	
	static private class GSSNodeDataIterator<T> implements Iterator<T> {
		
		private GSSNodeData<T> data;
		
		private int i = 0;
		
		GSSNodeDataIterator(GSSNodeData<T> data) {
			this.data = data;
		}

		@Override
		public boolean hasNext() {
			return i < data.size;
		}

		@Override
		public T next() {
			return data.elements[i++];
		}
		
	}

}
