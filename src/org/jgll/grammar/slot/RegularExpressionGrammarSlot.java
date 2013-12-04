package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.symbol.Alt;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Group;
import org.jgll.grammar.symbol.Opt;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Star;
import org.jgll.grammar.symbol.Symbol;
import org.jgll.grammar.symbol.Terminal;
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
	
	private transient Set<Terminal> firstSet;
	
	public RegularExpressionGrammarSlot(int position, RegularExpression regexp, BodyGrammarSlot previous, HeadGrammarSlot head) {
		super(position, previous, head);
		this.regexp = regexp;
		setFirstSet();
	}

	private void setFirstSet() {
		
		firstSet = new HashSet<>();
		
		for(Symbol symbol : regexp.getSymbols()) {
			setFirstSet(firstSet, symbol);
			if(!RegularExpression.isRegexpNullable(symbol)) {
				break;
			}
		}
	}
	
	private void setFirstSet(Set<Terminal> set, Symbol symbol) {
		
		if(symbol instanceof Character) {
			set.add((Terminal) symbol);
		} 
		
		else if(symbol instanceof CharacterClass) {
			set.add((Terminal) symbol);
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
			for(Symbol s : list) {
				setFirstSet(set, s);
				if(!RegularExpression.isRegexpNullable(s)) {
					break;
				}
			}
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
		
			if(executePreConditions(parser, input)) {
				return null;
			}
			
			int ci = parser.getCurrentInputIndex();
			
			RunAutomaton automaton = regexp.getAutomaton();

			int state = automaton.getInitialState();
			
			int lastState = state;
			
			int i = 0;
			while(true) {
				lastState = state;

				int charAtCi = input.charAt(ci + i);
				
				state = automaton.step(state, (char) charAtCi);
				if(state == -1) {
					break;
				}
				
				if(ci + i + 1 >= input.size()) {
					break;
				}
				
				i++;
			}
			
			// If does not match anything and is not nullable
			if(i == 0 && !isNullable()) {
				return null;
			}
			
			// The regular node that is going to be created as the result of this slot action.
			RegularExpressionNode regularNode = parser.getRegularExpressionNode(this, ci, ci + i);
			
			if(!automaton.isAccept(lastState)) {
				return null;
			}
			
			if(checkPopActions(parser, input)) {
				return null;
			}
			
			parser.setCurrentSPPFNode(DummyNode.getInstance());
			parser.getNonterminalNode((LastGrammarSlot) next, regularNode);
			
			parser.pop();
			
			return null;
			
	}
	
	@Override
	public SPPFNode parseLL1(GLLParserInternals parser, Input input) {
		
		if(executePreConditions(parser, input)) {
			return null;
		}
		
		int ci = parser.getCurrentInputIndex();
		
		RunAutomaton automaton = regexp.getAutomaton();

		int state = automaton.getInitialState();
		
		int lastState = state;
		
		int i = 0;
		while(true) {
			lastState = state;

			int charAtCi = input.charAt(ci + i);
			
			state = automaton.step(state, (char) charAtCi);
			
			if(state == -1) {
				break;
			}
			
			if(ci + i + 1 >= input.size()) {
				break;
			}
			
			i++;
		}
		
		// If does not match anything and is not nullable
		if(i == 0 && !isNullable()) {
			return null;
		}
		
		// The regular node that is going to be created as the result of this slot action.
		RegularExpressionNode regularNode = parser.getRegularExpressionNode(this, ci, ci + i);
		
		if(!automaton.isAccept(lastState)) {
			return null;
		}
		
		if(checkPopActions(parser, input)) {
			return null;
		}
				
		return regularNode;
	}
	
	public Set<Terminal> getFirstSet() {
		return firstSet;
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
