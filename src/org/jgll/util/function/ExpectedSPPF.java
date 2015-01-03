package org.jgll.util.function;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.sppf.SPPFNode;


@FunctionalInterface
public interface ExpectedSPPF {
	public SPPFNode get(GrammarSlotRegistry registry); 
}
