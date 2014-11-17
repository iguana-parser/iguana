package org.jgll.util.generator;

public class GeneratorUtil {
	
	public static String escape(String s) {
		if (s == null) return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
	
	public static <T> String listToString(T[] elements) {
		return listToString(elements, " ");
	}

	public static <T> String listToString(T[] elements, String sep) {
		
		if(elements == null) throw new IllegalArgumentException("Elements cannot be null.");

		if(elements.length == 0) return "";
		
		StringBuilder sb = new StringBuilder();
		for(Object t : elements) {
			sb.append(t.toString()).append(sep);
		}
		sb.delete(sb.length() - sep.length(), sb.length());
		return sb.toString();

	}
	
	public static <T> String listToString(Iterable<T> elements) {
		return listToString(elements, " ");
	}
	
	public static <T> String listToString(Iterable<T> elements, String sep) {
		
		if(elements == null) throw new IllegalArgumentException("Elements cannot be null.");

		if(! elements.iterator().hasNext()) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		for(T t : elements) {
			sb.append(t.toString()).append(sep);
		}
		sb.delete(sb.length() - sep.length(), sb.length());
		return sb.toString();
	}
	
}
