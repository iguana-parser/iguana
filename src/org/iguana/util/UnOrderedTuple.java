package org.iguana.util;

public class UnOrderedTuple<T, K> extends Tuple<T, K>{

	private static final long serialVersionUID = 1L;

	public UnOrderedTuple(T t, K k) {
		super(t, k);
	}
	
	public static <T, K> UnOrderedTuple<T, K> of(T t, K k) {
		return new UnOrderedTuple<>(t, k);
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
		
		return (t == null ? other.t == null : t.equals(other.t) &&
			    k == null ? other.k == null : k.equals(other.k)) ||
			   (t == null ? other.k == null : t.equals(other.k) &&
			    k == null ? other.t == null : k.equals(other.t)); 

	}


}
