package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbol.Symbol;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.SPPFNode;

/**
 * Corresponds to the last grammar slot in an alternate, e.g., X ::= alpha .
 * 
 * @author Ali Afroozeh
 *
 */
public class LastGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * An arbitrary data object that can be put in this grammar slot and
	 * retrieved later when traversing the parse tree.
	 * This object can be accessed via the getObject() method of a nonterminal symbol node.
	 */
	private transient Object object;
	
	public LastGrammarSlot(int position, BodyGrammarSlot previous, HeadGrammarSlot head, Object object) {
		super(position, previous, head);
		this.object = object;
	}
	
	public LastGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot head) {
		LastGrammarSlot slot = new LastGrammarSlot(this.position, previous, head, object);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer lexer) {
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
	
	public Object getObject() {
		return object;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}

	@Override
	public boolean isNullable() {
		return false;
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
	public SPPFNode parseLL1(GLLParser parser, GLLLexer lexer) {
		throw new UnsupportedOperationException();
	}

}
