package org.jgll.util;

public class Tuple<T, V> {

	private T t;
	private V v;
	
	public Tuple(T t, V v) {
		this.t = t;
		this.v = v;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (t == null ? 0 : t.hashCode());
		result = 31 * result + (v == null ? 0 : v.hashCode());
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
		
		if(t == null && other.t == null && v == null && other.v == null) {
			return true;
		}
		
		if(t == null && other.t == null) {
			return v.equals(other.v);
		}
		
		if(v == null && other.v == null) {
			return t.equals(other.t);
		}
		
		return t.equals(other.t) && v.equals(other.v);
	}
	
	public T getFirst() {
		return t;
	}
	
	public V getSecond() {
		return v;
	}
	
	
	@Override
	public String toString() {
		return "(" + (t == null ? "" : t.toString()) + ", " + (v == null ? v : v.toString()) + ")";
	}
}
