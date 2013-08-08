package org.jgll.grammar.slot;

import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class DirectNullableNonterminalGrammarSlot extends NonterminalGrammarSlot {

	private static final long serialVersionUID = 1L;

	public DirectNullableNonterminalGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(label, position, previous, nonterminal, head);
	}

	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		int ci = parser.getCurrentInputIndex();

		if(executePreConditions(parser, input)) {
			return null;
		}
		
		if(testFirstSet(ci, input)) {
			parser.createGSSNode(next);
			return nonterminal;
			
		} else if (testFollowSet(ci, input)) {
			parser.getLookupTable().getNonPackedNode(nonterminal.getAlternateAt(0).getLastBodySlot().next, ci, ci);
			return next;
		} else {
			parser.recordParseError(this);
			return null;			
		}

	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return super.recognize(recognizer, input);
	}
	
	
}
