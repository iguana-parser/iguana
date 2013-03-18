package org.jgll.parser;

import org.jgll.grammar.GrammarSlot;
import org.jgll.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("serial")
public class ParseError extends RuntimeException {

	private GrammarSlot slot;
	private final int inputIndex;
	private Input input;
	
	public ParseError(GrammarSlot slot, Input input, int inputIndex) {
		this.slot = slot;
		this.input = input;
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
		
		int lineNumber = input.getLineNumber(inputIndex);
		int columnNumber = input.getColumnNumber(inputIndex);
		
		StringBuilder sb = new StringBuilder();
		sb.append("Parse error at " + getSlot()  + " at line:" + lineNumber + " column:" + columnNumber);
		
		//TODO: fix it! 
//		sb.append(" expected: ");
//		for(Terminal t : slot.getTestSet()) {
//			sb.append(t).append(" ");
//		}
//		sb.append(" but found: " + (char) InputUtil.getInstance().charAt(inputIndex));
		return sb.toString();
	}
	
}
