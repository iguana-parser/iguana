package org.jgll.util.hashing;

import java.util.ArrayList;
import java.util.List;

import org.jgll.util.hashing.hashfunction.HashFunction;

public class HashFunctionBuilder {
	
	private HashFunction f;
	
	private List<Integer> list;

	public HashFunctionBuilder(HashFunction f) {
		this.f = f;
		this.list = new ArrayList<>();
	}
	
	public HashFunctionBuilder addInt(int i) {
		list.add(i);
		return this;
	}
	
	public int hash() {
		int[] array = new int[list.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = list.get(i);
		}
		return f.hash(array);
	}

}
