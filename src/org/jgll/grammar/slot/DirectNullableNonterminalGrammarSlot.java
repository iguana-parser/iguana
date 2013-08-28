package org.jgll.grammar.slot;

import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class DirectNullableNonterminalGrammarSlot extends NonterminalGrammarSlot {

	private static final long serialVersionUID = 1L;

	public DirectNullableNonterminalGrammarSlot(int position, BodyGrammarSlot previous, HeadGrammarSlot nonterminal, HeadGrammarSlot head) {
		super(position, previous, nonterminal, head);
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
			NonPackedNode node = parser.getLookupTable().getNonPackedNode(nonterminal, ci, ci);
			node.addFirstPackedNode(nonterminal.getEpsilonAlternate().getFirstSlot(), ci);
			
			if(next instanceof LastGrammarSlot) {
				parser.getNonterminalNode((LastGrammarSlot) next, node);
				parser.pop();
				return null;
			} else {
				parser.getIntermediateNode(next, node);
			}
			
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
