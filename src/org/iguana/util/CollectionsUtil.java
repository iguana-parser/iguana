package org.iguana.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

	@SafeVarargs
	public static <T> Set<T> set(T...objects) {
		Set<T>  set = new HashSet<>();
		for(T t : objects) {
			set.add(t);
		}
		return set;
	}
	
	@SafeVarargs
	public static <T> List<T> list(T...objects){
		return Arrays.asList(objects);
	}
	
	public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
		Set<T> set = new HashSet<>();
		set.addAll(set1);
		set.addAll(set2);
		return set;
	}
	
	public static <T> List<List<T>> split(List<T> list, int size) {
		List<List<T>> result = new ArrayList<>();
		
		int n = list.size() / size;
		int r = list.size() % size;
		
		for (int i = 0; i < n; i++) {
			List<T> split = new ArrayList<>();
			for (int j = i * size; j < i * size + size; j++) {
				split.add(list.get(j));
			}
			result.add(split);
		}

		if (r > 0) {
			List<T> rest = new ArrayList<>();
			for (int i = n * size; i < list.size(); i++) {
				rest.add(list.get(i));
			}
			result.add(rest);			
		}
		
		return result;
	}

}
