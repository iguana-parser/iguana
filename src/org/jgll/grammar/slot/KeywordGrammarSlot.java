package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.Character;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.Keyword;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.Terminal;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	protected HeadGrammarSlot keywordHead;
	protected Keyword keyword;
	
	public KeywordGrammarSlot(String label, int position, HeadGrammarSlot keywordHead, Keyword keyword, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label, position, previous, head);
		this.keywordHead = keywordHead;
		this.keyword = keyword;
	}
	
	public KeywordGrammarSlot copy(HeadGrammarSlot keywordHead, BodyGrammarSlot previous, HeadGrammarSlot head) {
		KeywordGrammarSlot slot = new KeywordGrammarSlot(this.label, this.position, keywordHead, this.keyword, previous, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}

	@Override
	public boolean testFirstSet(int index, Input input) {
		return input.match(index, keyword.getChars());
	}
	
	@Override
	public boolean testFollowSet(int index, Input input) {
		return false;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		// TODO Auto-generated method stub
	}
	
	@Override
	public Symbol getSymbol() {
		return keyword;
	}
	
	public Terminal getFirstTerminal() {
		return new Character(keyword.getChars()[0]);
	}
	
	public Keyword getKeyword() {
		return keyword;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		if(executePreConditions(parser, input)) {
			return null;
		}
		
		int ci = parser.getCurrentInputIndex();
		
		if(input.match(ci, keyword.getChars())) {
			
			NonPackedNode sppfNode = parser.getKeywordStub(keyword, keywordHead, ci);
			
			if(next instanceof LastGrammarSlot) {
				parser.getNonterminalNode((LastGrammarSlot) next, sppfNode);
				parser.pop();
				return null;
			} else {
				parser.getIntermediateNode(next, sppfNode);
			}
		} else {
			parser.recordParseError(this);
			return null;
		}
		
		return next;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		int ci = recognizer.getCi();
		if(!input.match(ci, keyword.getChars())) {
			return null;			
		}
		
		recognizer.update(recognizer.getCu(), ci + keyword.size());
		return next;
	}

}
