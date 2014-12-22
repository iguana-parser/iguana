package org.jgll.grammar.slot;

import org.jgll.grammar.slot.test.TrueFollowSet;
import org.jgll.grammar.slot.test.TruePredictionSet;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.descriptor.Descriptor;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 * 
 * TODO: L0 is not really a grammar slot. Change it!
 *
 */
public class L0 extends HeadGrammarSlot {
	
	private static L0 instance;
	
	public static L0 getInstance() {
		if(instance == null) {
			instance = new L0();
		}
		return instance;
	}
	
	private L0() {
		super(Nonterminal.withName("L0"), 0, false, new TruePredictionSet(0), new TrueFollowSet());
	}
	
	@Override
	public GrammarSlot execute(GLLParser parser, Input input, int i) {
		while(parser.hasNextDescriptor()) {
			Descriptor descriptor = parser.nextDescriptor();
			GrammarSlot slot = descriptor.getGrammarSlot();
			slot = slot.execute(parser, input, descriptor.getInputIndex());
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "L0";
	}

}
