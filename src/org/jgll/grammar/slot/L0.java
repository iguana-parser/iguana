package org.jgll.grammar.slot;

import org.jgll.grammar.slot.test.TrueFollowSet;
import org.jgll.grammar.slot.test.TruePredictionSet;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;

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
		super(-1, Nonterminal.withName("L0"), 0, false, new TruePredictionSet(0), new TrueFollowSet());
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		while(parser.hasNextDescriptor()) {
			GrammarSlot slot = parser.nextDescriptor().getGrammarSlot();
			slot = slot.parse(parser, lexer);
			while(slot != null) {
				slot = slot.parse(parser, lexer);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "L0";
	}

}
