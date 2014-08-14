package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.slot.nodecreator.NodeCreator;
import org.jgll.grammar.slot.test.ConditionTest;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	protected HeadGrammarSlot head;
	
	public LastGrammarSlot(int id, String label, BodyGrammarSlot previous, HeadGrammarSlot head, 
						   ConditionTest popConditions, NodeCreator nodeCreatorFromPop) {
		super(id, label, previous, null, null, popConditions, null, nodeCreatorFromPop);
		this.head = head;
	}
	
	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		if(head.testFollowSet(lexer.getInput().charAt(parser.getCurrentInputIndex()))) {
			return parser.pop();			
		}
		return null;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		writer.append("   pop(cu, ci, cn);\n");
		writer.append("   label = L0;\n}\n");
	}
		
	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNullable() {
		return false;
	}
	
	public HeadGrammarSlot getHead() {
		return head;
	}

	@Override
	public Symbol getSymbol() {
		throw new UnsupportedOperationException();
	}

}
