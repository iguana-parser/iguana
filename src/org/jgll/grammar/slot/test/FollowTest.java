package org.jgll.grammar.slot.test;

import java.io.Serializable;

import org.jgll.util.generator.ConstructorCode;

public interface FollowTest extends Serializable, ConstructorCode {
	
	public boolean test(int v);

}
