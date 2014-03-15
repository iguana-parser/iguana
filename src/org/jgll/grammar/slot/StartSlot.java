package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;

public class StartSlot implements GrammarSlot {

	private static final long serialVersionUID = 1L;

	@Override
	public void codeParser(Writer writer) throws IOException {
		
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer input) {
		return null;
	}

	@Override
	public int getNodeId() {
		return 0;
	}

	@Override
	public int getId() {
		return -2;
	}

}
