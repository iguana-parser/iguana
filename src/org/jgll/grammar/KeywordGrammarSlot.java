package org.jgll.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.jgll.parser.GLLParser;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.TerminalSymbolNode;
import org.jgll.util.Input;

public class KeywordGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	private Nonterminal nonterminal;
	private List<Terminal> terminals;
	
	public KeywordGrammarSlot(String label, int position, Nonterminal nt, List<Terminal> terminals, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(label, position, previous, head);
		if(terminals.size() == 0) {
			throw new IllegalArgumentException("There should be at least one terminal.");
		}
		this.nonterminal = nt;
		this.terminals = terminals;
	}

	@Override
	public boolean checkAgainstTestSet(int i) {
		return terminals.get(0).match(i);
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
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public GrammarSlot parse(GLLParser parser, Input input) {
		int ci = parser.getCi();
		NonPackedNode sppfNode = (NonPackedNode) parser.getLookupTable().getNonPackedNode(null, ci, ci + terminals.size());
		
		int i = 0;
		for(Terminal t : terminals) {
			int charAt = input.charAt(ci + i);
			if(t.match(charAt)) {
				TerminalSymbolNode node = parser.getNodeT(charAt, ci + i);
				i++;
				sppfNode.addChild(node);
			} else {
				parser.newParseError(this, ci, parser.getCu());
				return null;
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
