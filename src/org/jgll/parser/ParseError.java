package org.jgll.parser;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.GrammarSlot;
import org.jgll.util.Input;


/**
 * 
 * @author Ali Afroozeh
 *
 */
@SuppressWarnings("serial")
public class ParseError extends Exception {

	private final GSSNode currentNode;
	private final GrammarSlot slot;
	private final int inputIndex;
	private final Input input;
	
	public ParseError(GrammarSlot slot, Input input, int inputIndex, GSSNode curerntNode) {
		this.slot = slot;
		this.input = input;
		this.inputIndex = inputIndex;
		this.currentNode = curerntNode;
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
		
		return "Parse error at at line:" + lineNumber + " column:" + columnNumber;
	}
	
	public void printGrammarTrace(PrintStream out) {
		out.println(toString());
		out.println(slot);
		
		GSSNode gssNode = currentNode;
		
		int i = 0;
		
		Set<GSSNode> visitedNodes = new HashSet<>();
		
		while(gssNode != GSSNode.U0) {
			
			visitedNodes.add(gssNode);
			
			BodyGrammarSlot grammarSlot = (BodyGrammarSlot) gssNode.getGrammarSlot();
			indent(out, i, grammarSlot.previous().toString());

			if(gssNode.getEdges().size() > 1) {
				
				gssNode = gssNode.getEdges().get(0).getDestination();
			}
			
			for(GSSEdge edge : gssNode.getEdges()) {
				indent(out, i + 1, )
			}
		}
	}
	
	private void indent(PrintStream out, int i, String s) {
		out.println(String.format("%" + i * 2 + "s", s));
	}
 	
}
