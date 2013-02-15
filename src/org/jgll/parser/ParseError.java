package org.jgll.parser;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Terminal;
import org.jgll.util.LineColumn;
import org.jgll.util.InputUtil;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("serial")
public class ParseError extends RuntimeException {

	private BodyGrammarSlot slot;
	private final int inputIndex;
	
	public ParseError(BodyGrammarSlot slot, int inputIndex) {
		this.slot = slot;
		this.inputIndex = inputIndex;
	}
	
	public int getInputIndex() {
		return inputIndex;
	}

	public GrammarSlot getSlot() {
		return slot;
	}
	
	@Override
	public String toString() {
		
		LineColumn loc = InputUtil.getInstance().getLineNumber(inputIndex);
		int lineNumber = loc.getLineNumber();
		int columnNumber = loc.getColumnNumber();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Parse error at " + getSlot()  + " at line:" + lineNumber + " column:" + columnNumber);
		sb.append(" Expected: ");
		for(Terminal t : slot.getTestSet()) {
			sb.append(t).append(" ");
		}
		sb.append(" But found: " + (char) InputUtil.getInstance().charAt(inputIndex));
		return sb.toString();
	}
	
}
