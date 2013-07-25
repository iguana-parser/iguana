package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	private Nonterminal nonterminal;
	private Keyword keyword;
	
	public KeywordGrammarSlot(String label, int position, Nonterminal nt, Keyword keyword, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label, position, previous, head);
		this.nonterminal = nt;
		this.keyword = keyword;
	}

	@Override
	public boolean checkAgainstTestSet(int index, Input input) {
		return input.match(index, keyword.getChars());
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
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		int ci = parser.getCi();
		NonPackedNode sppfNode = (NonPackedNode) parser.getLookupTable().getNonPackedNode(null, ci, ci + keyword.size());
		
		for(int i = 0; i < keyword.size(); i++) {
			int charAt = input.charAt(ci + i);
			TerminalSymbolNode node = parser.getNodeT(charAt, ci + i);
			sppfNode.addChild(node);
		}
		return next;
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		// TODO Auto-generated method stub
		return null;
	}

}
