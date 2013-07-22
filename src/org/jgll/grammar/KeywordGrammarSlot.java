package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	public KeywordGrammarSlot(String label, int position, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label, position, previous, head);
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Symbol getSymbol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTerminalSlot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNonterminalSlot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLastSlot() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

}
