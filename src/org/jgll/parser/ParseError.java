package org.jgll.parser;

import org.jgll.grammar.GrammarSlot;
import org.jgll.util.LineColumn;
import org.jgll.util.InputUtil;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("serial")
public class ParseError extends RuntimeException {

	private GrammarSlot slot;
	private final int inputIndex;
	
	public ParseError(GrammarSlot slot, int inputIndex) {
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
		
		LineColumn loc = InputUtil.inputUtil.getLineNumber(inputIndex);
		int lineNumber = loc.getLineNumber();
		int columnNumber = loc.getColumnNumber();
		
		StringBuilder sb = new StringBuilder();
		sb.append("Parse error at " + getSlot()  + " at line:" + lineNumber + " column:" + columnNumber).append("\n");
		
		return sb.toString();
	}
	
}
