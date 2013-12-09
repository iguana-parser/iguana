package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.SPPFNode;

public class StartSlot extends GrammarSlot {

	private static final long serialVersionUID = 1L;

	public StartSlot(String label) {
		id = -2;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		
	}

	@Override
	public GrammarSlot parse(GLLParserInternals parser, GLLLexer input) {
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer input) {
		return null;
	}

	@Override
	public SPPFNode parseLL1(GLLParserInternals parser, GLLLexer input) {
		throw new UnsupportedOperationException();
	}

}
