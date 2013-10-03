package org.jgll.grammar.regularexp;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbols.Character;
import org.jgll.grammar.symbols.CharacterClass;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Range;
import org.jgll.grammar.symbols.RegularExpression;
import org.jgll.grammar.symbols.RegularList;
import org.jgll.grammar.symbols.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * S ::= Float;
 * Float ::= Num . Num
 * Num ::= [0-9]+ !>> [0-9]
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularExpTest1 {
	
	private Grammar grammar1;
	private Grammar grammar2;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Nonterminal S = new Nonterminal("S");
		Nonterminal Float = new Nonterminal("Float");
		Nonterminal Num = new Nonterminal("Num");
		
		Rule r1 = new Rule(S, list(Float));
		Rule r2 = new Rule(Float, list(Num, new Character('.'), Num));
		
		Rule r3 = new Rule(Num, list(new Nonterminal("[0-9]+")));
		Rule r4 = new Rule(new Nonterminal("[0-9]+"), list(new Nonterminal("[0-9]+"), new CharacterClass(list(new Range('0', '9')))));
		Rule r5 = new Rule(new Nonterminal("[0-9]+"), list(new CharacterClass(list(new Range('0', '9')))));
		
		Rule r6 = new Rule(Float, list(new RegularExpression("[0-9]+[.][0-9]+")));

		grammar1 = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
		grammar2 = new GrammarBuilder().addRule(r1).addRule(r6).build();
	}
	

	@Test
	public void test1() throws ParseError {
		levelParser = ParserFactory.levelParser(grammar1, 10);
		levelParser.parse(Input.fromString("12345.12345"), grammar1, "S");
	}
}
