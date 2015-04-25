package org.iguana.traversal;

public class Result<T> {
	
	private static final Result<Object> filter = new Result<Object>(null);
	
	private static final Result<Object> skip = new Result<Object>(null);

	@SuppressWarnings("unchecked")
	public static <K> Result<K> filter() {
		return (Result<K>) filter;
	}
	
	@SuppressWarnings("unchecked")
	public static <K> Result<K> skip() {
		return (Result<K>) skip;
	}	
	
	public static <K> Result<K> accept(K k) {
		if(k == null) {
			throw new IllegalArgumentException(k + "should not be null.");
		}
		return new Result<K>(k);
	}
	
	private T object;
	
	private Result(T object) {
		this.object = object;
	}
	
	public T getObject() {
		return object;
	}	
}

