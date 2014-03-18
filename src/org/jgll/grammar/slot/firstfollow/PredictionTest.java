package org.jgll.grammar.slot.firstfollow;

import java.io.Serializable;
import java.util.Set;

public interface PredictionTest extends Serializable {
	
	public boolean test(int v);
	
	public Set<Integer> get(int v);

}
