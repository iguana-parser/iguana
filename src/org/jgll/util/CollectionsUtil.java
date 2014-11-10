package org.jgll.util;

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

}
