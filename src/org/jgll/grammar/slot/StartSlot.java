package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;

public class StartSlot extends GrammarSlot {

	private static final long serialVersionUID = 1L;

	public StartSlot(String label) {
		id = -2;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		
	}

	@Override
	public GrammarSlot parse(GLLParser parser, GLLLexer input) {
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer input) {
		return null;
	}

}
