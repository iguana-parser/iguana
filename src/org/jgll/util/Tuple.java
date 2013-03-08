package org.jgll.util;

public class Tuple<T, V> {

	private T t;
	private V v;
	
	public Tuple(T t, V v) {
		if(t == null || v == null) {
			throw new IllegalArgumentException("Tuple components can't be null.");
		}
		this.t = t;
		this.v = v;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + t.hashCode();
		result = 31 * result + v.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tuple)) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		Tuple other = (Tuple) obj;
		
		return t.equals(other.t) && v.equals(other.v);
	}
	
}
