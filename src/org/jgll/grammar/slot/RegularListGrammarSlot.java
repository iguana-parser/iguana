package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.symbols.CharacterClass;
import org.jgll.grammar.symbols.RegularList;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;


/**
 * A grammar slot whose next immediate symbol is regular expression list 
 * of the form [a-z]+.
 *
 *
 * @author Ali Afroozeh
 *
 */
public class RegularListGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	private final RegularList regularList;

	private final HeadGrammarSlot regularHead;
	
	public RegularListGrammarSlot(int position, BodyGrammarSlot previous, RegularList regularList, 
								  HeadGrammarSlot regularHead, HeadGrammarSlot head) {
		super(position, previous, head);
		this.regularList = regularList;
		this.regularHead = regularHead;
	}
	
	public RegularListGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot head) {
		RegularListGrammarSlot slot = new RegularListGrammarSlot(this.position, previous, this.regularList, this.regularHead, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}
		
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		int ci = parser.getCurrentInputIndex();
		
		int longestTerminalChain = parser.getGrammar().getLongestTerminalChain();
		
		CharacterClass characterClass = regularList.getCharacterClass();
		
		int minimum = regularList.getMinimum();

		int i;
		for(i = 0; i < longestTerminalChain; i++) {
			int charAtCi = input.charAt(ci + i);
			if(!characterClass.match(charAtCi)) {
				break;
			}
		}
		
		NonPackedNode sppfNode = null;

		// The whole list is matched
		if(i < longestTerminalChain) {
			
			sppfNode = parser.getRegularNode(this, ci, ci + i);
			
			if(next instanceof LastGrammarSlot) {
				parser.getNonterminalNode((LastGrammarSlot) next, sppfNode);
				parser.pop();
				return null;
			} else {
				parser.getIntermediateNode(next, sppfNode);
				return next;
			}
		} 
		else {
			// sppfNode = create new node 
			parser.addDescriptor(this);
		}
		
		return null;
	}
	
	@Override
	public GrammarSlot recognize(GLLRecognizer recognizer, Input input) {
		return next;
	}
	
	@Override
	public void codeParser(Writer writer) throws IOException {
		
	}
	
	@Override
	public boolean testFirstSet(int index, Input input) {
		return regularList.getCharacterClass().match(input.charAt(index));
	}
	
	@Override
	public boolean testFollowSet(int index, Input input) {
		return false;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (").append(regularList.getCharacterClass().getMatchCode()).append(") {\n");
	}

	@Override
	public boolean isNullable() {
		return false;
	}


	@Override
	public RegularList getSymbol() {
		return regularList;
	}

	@Override
	public boolean isNameEqual(BodyGrammarSlot slot) {
		if(this == slot) {
			return true;
		}
		
		if(!(slot instanceof RegularListGrammarSlot)) {
			return false;
		}
		
		RegularListGrammarSlot other = (RegularListGrammarSlot) slot;
		
		return regularList.equals(other.regularList);
	}

}
