package org.iguana.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface SemanticAction {
	
	public Object execute(Object o);
	
	public static SemanticAction Unit = x -> x;
	
	public static SemanticAction from(Consumer<Object> c) {
		return t -> { c.accept(t); return null; };
	}
}
