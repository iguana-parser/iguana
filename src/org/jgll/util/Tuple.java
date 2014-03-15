package org.jgll.util;

import org.jgll.parser.HashFunctions;


public class Tuple<T, K> {
	
	private T t;
	private K k;

	public Tuple(T t, K k) {
		this.t = t;
		this.k = k;
	}
	
	public T getFirst() {
		return t;
	}
	
	public K getSecond() {
		return k;
	}
	
	public static <T, K> Tuple<T, K> of(T t, K k) {
		return new Tuple<>(t, k);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof Tuple)) {
			return false;
		}
		
		@SuppressWarnings("unchecked")
		Tuple<T, K> other = (Tuple<T, K>) obj;
		
		return t == null ? other.t == null : t.equals(other.t) &&
			   k == null ? other.k == null : k.equals(other.k);
	}
	
	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction().hash(t == null ? 0 : t.hashCode(), 
												   k == null ? 0 : k.hashCode());
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s)", t, k);
	}

}
