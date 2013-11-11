package org.jgll.grammar.regularexp;

import static org.jgll.util.CollectionsUtil.list;

import java.util.List;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarBuilder;
import org.jgll.grammar.symbol.AbstractSymbol;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.CharacterClass;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Plus;
import org.jgll.grammar.symbol.Range;
import org.jgll.grammar.symbol.RegularExpression;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Input;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * S ::= Float;
 * Float ::= Num . Num
 * Num ::= [0-9]+ !>> [0-9]
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularExpTest1 {
	
	private Grammar grammar1;
	private Grammar grammar2;
	
	private GLLParser levelParser;
	private GLLParser rdParser;

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
		
		CharacterClass zero_nine = new CharacterClass(list(new Range('0', '9')));
		List<AbstractSymbol> symbols = list(new Plus(zero_nine), new Character('.'), new Plus(zero_nine));
		Rule r6 = new Rule(Float, list(new RegularExpression(symbols)));
		
		grammar1 = new GrammarBuilder().addRule(r1).addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
		grammar2 = new GrammarBuilder().addRule(r1).addRule(r6).build();
	}

	@Test
	public void test1() throws ParseError {
		levelParser = ParserFactory.createLevelParser(grammar2, 10);
		levelParser.parse(Input.fromString("123451234512345.122343535341223435353412234353534"), grammar2, "S");
		
		levelParser = ParserFactory.createLevelParser(grammar1, 10);
		levelParser.parse(Input.fromString("123451234512345.122343535341223435353412234353534"), grammar1, "S");
		
		rdParser = ParserFactory.createRecursiveDescentParser(grammar2);
		rdParser.parse(Input.fromString("12.122343535341223435353412234353534"), grammar2, "S");
		
		rdParser = ParserFactory.createRecursiveDescentParser(grammar1);
		rdParser.parse(Input.fromString("123451234512345.122343535341223435353412234353534"), grammar1, "S");
	}
}
