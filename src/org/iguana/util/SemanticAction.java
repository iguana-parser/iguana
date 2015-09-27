package org.iguana.util;

import java.io.Serializable;
import java.util.function.Consumer;

@FunctionalInterface
public interface SemanticAction extends Serializable {

	Object execute(Object o);

	static SemanticAction from(Consumer<Object> c) {
        return o -> { c.accept(o); return null; };
	}
}
