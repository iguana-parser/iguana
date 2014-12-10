package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.lexer.Lexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.regex.RegularExpression;
import org.jgll.util.Input;

public class TerminalGrammarSlot implements GrammarSlot {

	private RegularExpression regex;

	public TerminalGrammarSlot(RegularExpression regex) {
		this.regex = regex;
	}
	
	@Override
	public String getConstructorCode(GrammarSlotRegistry registry) {
		StringBuilder sb = new StringBuilder();
		sb.append("new TerminalGrammarSlot(")
		  .append(regex.getConstructorCode(registry))
		  .append(")");
		return sb.toString();
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Lexer lexer) {
		return null;
	}

	@Override
	public GSSNode getGSSNode(int inputIndex) {
		return null;
	}

	@Override
	public GSSNode hasGSSNode(int inputIndex) {
		return null;
	}
	
	public RegularExpression getRegularExpression() {
		return regex;
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
	public void code(PrintWriter writer, GrammarSlotRegistry registry) {
		
	}

}
