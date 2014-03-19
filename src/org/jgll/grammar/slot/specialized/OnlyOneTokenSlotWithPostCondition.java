package org.jgll.grammar.slot.specialized;

import java.util.List;

import org.jgll.grammar.slot.BodyGrammarSlot;
import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.grammar.slot.HeadGrammarSlot;
import org.jgll.grammar.slot.PreCondition;
import org.jgll.grammar.slotaction.SlotAction;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.regex.RegularExpression;

public class OnlyOneTokenSlotWithPostCondition extends OnlyOneTokenSlot implements PreCondition {
	
	private List<SlotAction<Boolean>> preConditions;
	
	public OnlyOneTokenSlotWithPostCondition(int id, int nodeId, String label,
			BodyGrammarSlot previous, RegularExpression regularExpression,
			HeadGrammarSlot head, int tokenID) {
		super(id, nodeId, label, previous, regularExpression, head, tokenID);
	}

	private static final long serialVersionUID = 1L;

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		
		if(execute(parser, lexer)) {
			return null;
		}
		
		return super.parse(parser, lexer);
	}

	@Override
	public boolean execute(GLLParser parser, GLLLexer lexer) {
		for(SlotAction<Boolean> preCondition : preConditions) {
			if(preCondition.execute(parser, lexer, parser.getCurrentInputIndex())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void addPreCondition(SlotAction<Boolean> preCondition) {
		preConditions.add(preCondition);
	}

}
