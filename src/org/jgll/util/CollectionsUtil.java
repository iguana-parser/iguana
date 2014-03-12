package org.jgll.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionsUtil {

	public static <T> String listToString(List<T> elements) {
		return listToString(elements, " ");
	}
	
	public static <T> String listToString(List<T> elements, String sep) {
		
		if(elements.size() == 0) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for(T t : elements) {
			sb.append(t.toString()).append(sep);
		}
		sb.delete(sb.length() - sep.length(), sb.length());
		return sb.toString();
	}

		
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

}
