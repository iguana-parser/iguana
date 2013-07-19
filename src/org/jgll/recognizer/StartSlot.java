package org.jgll.recognizer;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.GrammarSlot;
import org.jgll.parser.GLLParser;
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
	public GrammarSlot parse(GLLParser parser, Input input) {
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return null;
	}

}
