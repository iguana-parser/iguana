package org.jgll.grammar.slot.test;

import java.io.Serializable;
import java.util.Set;

import org.jgll.util.generator.ConstructorCode;

public interface PredictionTest extends Serializable, ConstructorCode {
	
	public boolean test(int v);
	
	public Set<Integer> get(int v);

}
