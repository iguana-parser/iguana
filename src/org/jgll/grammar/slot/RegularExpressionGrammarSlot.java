package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.BitSet;
import java.util.List;

import org.jgll.grammar.symbol.Alt;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Star;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.RegularExpressionNode;
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
		
		for(Symbol symbol : regexp.getSymbols()) {
			setFirstSet(firstSet, symbol);
		}
	}
	
	private void setFirstSet(BitSet set, Symbol symbol) {
		
		if(symbol instanceof Character) {
			firstSet.or(((Character) symbol).asBitSet());
		} 
		
		else if(symbol instanceof CharacterClass) {
			firstSet.or(((CharacterClass) symbol).asBitSet());
		} 
		
		else if(symbol instanceof Plus) {
			setFirstSet(set, ((Plus) symbol).getSymbol());
		}
		
		else if(symbol instanceof Star) {
			setFirstSet(set, ((Star) symbol).getSymbol());
		}
		
		else if(symbol instanceof Opt) {
			setFirstSet(set, ((Opt) symbol).getSymbol());
		} 
		
		else if(symbol instanceof Group) {
			List<? extends Symbol> list = ((Group) symbol).getSymbols();
			setFirstSet(set, list.get(0));
		}
		
		else if(symbol instanceof Alt) {
			List<? extends Symbol> list = ((Alt) symbol).getSymbols();
			for(Symbol s : list) {
				setFirstSet(set, s);
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

		Object object = parser.getCurrentDescriptor().getObject();
		
		int state;
		
		if(object != null) {
			state = (Integer) object;
		} else {
			state = automaton.getInitialState();
		}
		
		int lastState = state;
		
		int i = 0;
		for(i = 0; i < regularListLength; i++) {
			int charAtCi = input.charAt(ci + i);
			
			lastState = state;
			state = automaton.step(state, (char) charAtCi);
			if(state == -1) {
				break;
			}
		}
		
		// If does not match anything and is not nullable
		if(i == 0 && !isNullable()) {
			return null;
		}
		
		// The regular node that is going to be created as the result of this
		// slot action.
		RegularExpressionNode regularNode = parser.getRegularExpressionNode(this, ci, ci + i);
		
		SPPFNode currentSPPFNode = parser.getCurrentSPPFNode();
		
		// If the current SPPF node is a partially matched list node, merge the nodes
		if(currentSPPFNode instanceof RegularExpressionNode && ((RegularExpressionNode) currentSPPFNode).isPartial()) {
			regularNode = parser.getRegularExpressionNode(this, currentSPPFNode.getLeftExtent(), ci + i);
		}

		// partial match, needs to be rescheduled
		if(i == regularListLength) {
			regularNode.setPartial(true);
			parser.addDescriptor(this, parser.getCurrentGSSNode(), ci + i, regularNode, state);
			
		// Complete match
		} else {
			
			if(!automaton.isAccept(lastState)) {
				return null;
			}
			parser.setCurrentSPPFNode(DummyNode.getInstance());
			parser.getNonterminalNode((LastGrammarSlot) next, regularNode);
			parser.pop();
			return null;
		}
		
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
		RunAutomaton automaton = regexp.getAutomaton();
		return automaton.run("");
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
