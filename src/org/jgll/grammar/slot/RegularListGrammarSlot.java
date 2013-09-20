package org.jgll.grammar.slot;

import java.io.IOException;
import java.io.Writer;

import org.jgll.grammar.Character;
import org.jgll.grammar.CharacterClass;
import org.jgll.grammar.Range;
import org.jgll.grammar.Symbol;
import org.jgll.grammar.Terminal;
import org.jgll.parser.GLLParserInternals;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.util.Input;


/**
 * A grammar slot whose next immediate symbol is regular expression list 
 * of the form [a-z]+
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularListGrammarSlot extends BodyGrammarSlot {
	
	private static final long serialVersionUID = 1L;
	
	private final CharacterClass characterClass;

	public RegularListGrammarSlot(int position, BodyGrammarSlot previous, CharacterClass characterClass, HeadGrammarSlot head) {
		super(position, previous, head);
		this.characterClass = characterClass;
	}
	
	public RegularListGrammarSlot copy(BodyGrammarSlot previous, HeadGrammarSlot head) {
		RegularListGrammarSlot slot = new RegularListGrammarSlot(this.position, previous, this.characterClass, head);
		slot.preConditions = preConditions;
		slot.popActions = popActions;
		return slot;
	}
		
	@Override
	public GrammarSlot parse(GLLParserInternals parser, Input input) {
		
		int ci = parser.getCurrentInputIndex();
		
		int longestTerminalChain = parser.getGrammar().getLongestTerminalChain();

		for(int i = 0; i < longestTerminalChain; i++) {
			int charAtCi = input.charAt(ci + i);
			if(characterClass.match(charAtCi)) {
				
			}
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
	
	private String checkInput(Terminal terminal) {
		String s = "";
		if(terminal instanceof Range) {
			s += "   if(I[ci] >= " +  ((Range) terminal).getStart() + " + && I[ci] <= " + ((Range) terminal).getEnd() + ") {\n";	
		} else {
			s += "   if(I[ci] == " + ((Character) terminal).get() + ") {\n";
		}
		return s;
	}

	@Override
	public boolean testFirstSet(int index, Input input) {
		return characterClass.match(input.charAt(index));
	}
	
	@Override
	public boolean testFollowSet(int index, Input input) {
		return false;
	}

	@Override
	public void codeIfTestSetCheck(Writer writer) throws IOException {
		writer.append("if (").append(characterClass.getMatchCode()).append(") {\n");
	}

	@Override
	public boolean isNullable() {
		return false;
	}


	@Override
	public Symbol getSymbol() {
		return characterClass;
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
		
		return characterClass.equals(other.characterClass);
	}

}
