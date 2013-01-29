package org.jgll.util;

public class KeyValue<K, V> implements java.util.Map.Entry<K, V> {

	private final K key;
	
	private V value;

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!(obj instanceof KeyValue)) {
			return false;
		}
		
		KeyValue<?,?> other = (KeyValue<?,?>) obj;
		
		return this.key.equals(other.key) && 
			   this.value.equals(other.value);
	}	

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	
	@Override
	public String toString() {
		return "[" + key.toString() + ", " + value.toString() + "]";
	}
	
}
