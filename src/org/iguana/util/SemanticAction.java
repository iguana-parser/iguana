package org.iguana.util;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SemanticAction extends Serializable {

	public Object execute(Object o);
	
	public static SemanticAction Unit = x -> x;
	
	public static SemanticAction from(Consumer<Object> c) {
		// TODO: check it in Eclipse Mars, trhows Inavid lambda deserialization in Kepler.
		return new SemanticAction() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Object execute(Object o) {
				c.accept(o);
				return null;
			}
		};
		
	}
}
