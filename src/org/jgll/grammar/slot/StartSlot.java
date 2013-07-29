package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;

public class StartSlot extends GrammarSlot {

	private static final long serialVersionUID = 1L;

	public StartSlot(String label) {
		super(label);
		id = -2;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		
	}

	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return null;
	}

}
