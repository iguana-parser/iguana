package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbols.RegularExpression;
import org.jgll.grammar.symbols.Symbol;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;


public class RegularExpressionGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	private RegularExpression regexp;

	public RegularExpressionGrammarSlot(int position, RegularExpression regexp, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(position, previous, head);
		this.regexp = regexp;
	}

	@Override
	public boolean testFirstSet(int index, Input input) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean testFollowSet(int index, Input input) {
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
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

}
