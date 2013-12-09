package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Keyword;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	protected HeadGrammarSlot keywordHead;
	protected Keyword keyword;
	
	private BitSet firstSet;
	
	public KeywordGrammarSlot(int position, HeadGrammarSlot keywordHead, Keyword keyword, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(position, previous, head);
		if(keywordHead == null) {
			throw new IllegalArgumentException("Keyword head cannot be null.");
		}
		this.keywordHead = keywordHead;
		this.keyword = keyword;
		
		firstSet = new BitSet();
		firstSet.set(keyword.getChars()[0]);
	}
	
	public KeywordGrammarSlot copy(HeadGrammarSlot keywordHead, BodyGrammarSlot previous, HeadGrammarSlot head) {
		KeywordGrammarSlot slot = new KeywordGrammarSlot(this.position, keywordHead, this.keyword, previous, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
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
	public GrammarSlot parse(GLLParserInternals parser, GLLLexer lexer) {
		
		int ci = parser.getCurrentInputIndex();
		
		if(lexer.getInput().match(ci, keyword.getChars())) {
			
			if(executePreConditions(parser, lexer)) {
				return null;
			}
			
			NonPackedNode sppfNode = parser.getKeywordStub(keyword, keywordHead, ci);
			
			if(next instanceof LastGrammarSlot) {
				parser.getNonterminalNode((LastGrammarSlot) next, sppfNode);
				
				if(checkPopActions(parser, lexer)) {
					return null;
				}
				
				parser.pop();
				return null;
			} else {
				parser.getIntermediateNode(next, sppfNode);
				
				// TODO: check if this condition is necessary here!
				if(checkPopActions(parser, lexer)) {
					return null;
				}				
			}
		} else {
			parser.recordParseError(this);
			return null;
		}
		
		return next;
	}
	
	@Override
	public SPPFNode parseLL1(GLLParserInternals parser, GLLLexer lexer) {
		int ci = parser.getCurrentInputIndex();
		
		if(lexer.getInput().match(ci, keyword.getChars())) {
			
			if(executePreConditions(parser, lexer)) {
				return null;
			}
			
			return parser.getKeywordStub(keyword, keywordHead, ci);
			
		} else {
			parser.recordParseError(this);
			return null;
		}
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, GLLLexer lexer) {
		int ci = recognizer.getCi();
		if(!lexer.getInput().match(ci, keyword.getChars())) {
			return null;			
		}
		
		recognizer.update(recognizer.getCu(), ci + keyword.size());
		return next;
	}

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		
		if(this == slot) {
			return true;
		}
		
		if(!(slot instanceof KeywordGrammarSlot)) {
			return false;
		}
		
		KeywordGrammarSlot other = (KeywordGrammarSlot) slot;
		
		return keyword.equals(other.keyword);
	}
	
	public HeadGrammarSlot getKeywordHead() {
		return keywordHead;
	}
}
