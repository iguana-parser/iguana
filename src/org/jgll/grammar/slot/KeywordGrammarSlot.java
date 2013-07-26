package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.Character;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.grammar.Keyword;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.Terminal;
import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	private HeadGrammarSlot keywordHead;
	private Keyword keyword;
	
	public KeywordGrammarSlot(String label, int position, HeadGrammarSlot keywordHead, Keyword keyword, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label, position, previous, head);
		this.keywordHead = keywordHead;
		this.keyword = keyword;
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
		// TODO Auto-generated method stub
		return null;
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
	public GrammarSlot parse(GLLParser parser, Input input) {
		int ci = parser.getCurrentInputIndex();
		
		if(input.match(ci, keyword.getChars())) {
				// A::= x1
				if(previous == null && next.next() == null) {
					NonPackedNode sppfNode = (NonPackedNode) parser.getLookupTable().getNonPackedNode(keywordHead, ci, ci + keyword.size());
					
					for(int i = 0; i < keyword.size(); i++) {
						TerminalSymbolNode node = parser.getTerminalNode(input.charAt(ci + i));
						sppfNode.addChild(node);
					}
					
					parser.getNodeP(next, sppfNode);
				}
		}
		
		return next;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

}
