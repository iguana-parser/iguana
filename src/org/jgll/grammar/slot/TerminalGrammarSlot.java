package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

public class TerminalGrammarSlot implements GrammarSlot {

	public TerminalGrammarSlot() {
	}
	
	@Override
	public String getConstructorCode() {
		return null;
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		return null;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return null;
	}

	@Override
	public GSSNode hasGSSNode(int inputIndex) {
		return null;
	}

	@Override
	public boolean isInitialized() {
		return false;
	}

	@Override
	public void init(Input input) {
		
	}

	@Override
	public void reset() {
		
	}

	@Override
	public void code(PrintWriter writer) {
		
	}

}
