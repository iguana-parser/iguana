package org.iguana.util;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SemanticAction extends Serializable {
	
	public Object execute(Object o);
	
	public static SemanticAction Unit = x -> x;
	
	public static SemanticAction from(Consumer<Object> c) {
		return t -> { c.accept(t); return null; };
	}
}
