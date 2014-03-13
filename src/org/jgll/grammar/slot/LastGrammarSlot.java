package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	protected int alternateIndex;
	
	public LastGrammarSlot(int slotId, String label, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(slotId, label, previous, head);
	}
	
	public LastGrammarSlot copy(BodyGrammarSlot previous, String label, HeadGrammarSlot head) {
		LastGrammarSlot slot = new LastGrammarSlot(slotId, label, previous, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
		// TODO: check for follow sets at this point
		parser.pop();
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		recognizer.pop(recognizer.getCu(), recognizer.getCi());
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
	
	
	public int getAlternateIndex() {
		return alternateIndex;
	}
	
	public void setAlternateIndex(int alternateIndex) {
		this.alternateIndex = alternateIndex;
	}

	@Override
	public Symbol getSymbol() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNodeId() {
		return alternateIndex;
	}

}
