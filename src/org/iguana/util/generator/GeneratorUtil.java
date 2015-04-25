package org.jgll.util.generator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GeneratorUtil {
	
	public static String escape(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	public static <T> String listToString(T[] elements) {
		return listToString(elements, " ");
	}

	public static <T> String listToString(T[] elements, String sep) {
		return listToString(Arrays.asList(elements), sep);
	}
	
	public static <T> String listToString(Iterable<T> elements) {
		return listToString(elements, " ");
	}
	
	public static <T> String listToString(Iterable<T> elements, String sep) {
		
		if(elements == null) throw new IllegalArgumentException("elements cannot be null.");
		
		Stream<T> stream = StreamSupport.stream(elements.spliterator(), false);
		
		return stream.map(a -> a.toString()).collect(Collectors.joining(sep));
	}
	
}
