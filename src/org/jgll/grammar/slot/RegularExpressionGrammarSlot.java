package org.jgll.grammar.slot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;

import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Star;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.RegularListNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;

import dk.brics.automaton.RunAutomaton;


public class RegularExpressionGrammarSlot extends BodyGrammarSlot {

	private static final long serialVersionUID = 1L;
	
	private RegularExpression regexp;
	
	private BitSet firstSet;
	
	public RegularExpressionGrammarSlot(int position, RegularExpression regexp, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(position, previous, head);
		this.regexp = regexp;
		setFirstSet();
	}

	private void setFirstSet() {
		
		firstSet = new BitSet();
		
		for(Symbol symbol : regexp.getSymbol().getSymbols()) {
			if(symbol instanceof Character) {
				firstSet.or(((Character) symbol).asBitSet());
				break;
			} 
			
			if(symbol instanceof Plus) {
				if(! (((Plus) symbol).getSymbol() instanceof Terminal)) {
					throw new IllegalArgumentException("Can only be a terminal");
				}
				firstSet.or(((Terminal)((Plus) symbol).getSymbol()).asBitSet());
				break;
			}
			
			if(symbol instanceof Star) {
				if(! (((Star) symbol).getSymbol() instanceof Terminal)) {
					throw new IllegalArgumentException("Can only be a terminal");
				}
				firstSet.or(((Terminal)((Star) symbol).getSymbol()).asBitSet());				
			}
			
			if(symbol instanceof Opt) {
				if(! (((Opt) symbol).getSymbol() instanceof Terminal)) {
					throw new IllegalArgumentException("Can only be a terminal");
				}
				firstSet.or(((Terminal)((Opt) symbol).getSymbol()).asBitSet());
			}
		}
	}
	
	public RegularExpressionGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot head) {
		RegularExpressionGrammarSlot slot = new RegularExpressionGrammarSlot(this.position, regexp, previous, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}
		
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		int ci = parser.getCurrentInputIndex();
		
		int regularListLength = parser.getRegularListLength();
		
		RunAutomaton automaton = regexp.getAutomaton();

		// TODO: restore the state
		int state = 0;
		
		int i = 0;
		for(i = 0; i < regularListLength; i++) {
			int charAtCi = input.charAt(ci + i);
			
			state = automaton.step(state, (char) charAtCi);
			if(state == -1) {
				break;
			}
		}

		// The regular node that is going to be created as the result of this
		// slot action.
		RegularListNode regularNode = parser.getRegularNode(this, ci, ci + i);
		
		SPPFNode currentSPPFNode = parser.getCurrentSPPFNode();
		
		// If the current SPPF node is a partially matched list node, merge the nodes
		if(currentSPPFNode instanceof RegularListNode && ((RegularListNode) currentSPPFNode).isPartial()) {
			regularNode = parser.getRegularNode(this, currentSPPFNode.getLeftExtent(), ci + i);
		}

		// partial match, needs to be rescheduled
		if(i == regularListLength) {
			regularNode.setPartial(true);
			parser.addDescriptor(this, parser.getCurrentGSSNode(), ci + i, regularNode);
		} else {
			parser.setCurrentSPPFNode(DummyNode.getInstance());
			parser.getNonterminalNode((LastGrammarSlot) next, regularNode);
			parser.pop();
			return null;
		}
		
//		ByteArrayOutputStream output = new ByteArrayOutputStream();
//		try {
//			automaton.store(output);
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}

		// If the whole list is matched
//		if(l <= regularListLength && automaton.step(state, (char) input.charAt(ci + l)) != -1) {
//			parser.setCurrentSPPFNode(DummyNode.getInstance());
//			parser.getNonterminalNode((LastGrammarSlot) next, regularNode);
//			parser.pop();
//			return null;
//		} 
//		else {
//			try {
//				automaton =  RunAutomaton.load(new ByteArrayInputStream(output.toByteArray()));
//			}
//			catch (ClassCastException | ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
//			regularNode.setPartial(true);
//			parser.addDescriptor(this, parser.getCurrentGSSNode(), ci + l, regularNode);
//		}
		
		return null;
	}


	@Override
	public boolean testFirstSet(int index, Input input) {
		return firstSet.get(input.charAt(index));
	}

	@Override
	public boolean testFollowSet(int index, Input input) {
		return true;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RegularExpression getSymbol() {
		return regexp;
	}

	@Override
	public boolean isNullable() {
		for(Symbol symbol : regexp.getSymbol().getSymbols()) {
			if(symbol instanceof Terminal) {
				return false;
			}
			
			if(symbol instanceof Plus) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		return false;
	}

	@Override
	public void codeParser(Writer writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
