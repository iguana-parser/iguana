package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.list;

import org.jgll.grammar.symbols.CharacterClass;
import org.jgll.grammar.symbols.Nonterminal;
import org.jgll.grammar.symbols.Range;
import org.jgll.grammar.symbols.RegularList;
import org.jgll.grammar.symbols.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.recognizer.GLLRecognizer;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Id ::= [a-z]+ !>> [a-z]
 * 
 * @author Ali Afroozeh
 *
 */
public class RegularListTest {
	
	private Grammar grammar1;
	private Grammar grammar2;
	
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("Id"), list(RegularList.plus(new CharacterClass(list(new Range('a', 'z'))))));
		grammar1 = new GrammarBuilder("RegularList").addRule(r1).build();
		levelParser = ParserFactory.levelParser(grammar1, 10);
		
		// The expanded version
		Rule r2 = new Rule(new Nonterminal("Id"), list(new Nonterminal("[a-z]+")));
		Rule r3 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]+"), new Nonterminal("[a-z]")));
		Rule r4 = new Rule(new Nonterminal("[a-z]+"), list(new Nonterminal("[a-z]")));
		Rule r5 = new Rule(new Nonterminal("[a-z]"), list(new CharacterClass(list(new Range('a', 'z')))));
		grammar2 = new GrammarBuilder().addRule(r2).addRule(r3).addRule(r4).addRule(r5).build();
	}

//	@Test
	public void test1() throws ParseError {
		levelParser.parse(Input.fromString("abcdef"), grammar1, "Id");
		levelParser.parse(Input.fromString("abcdef"), grammar2, "Id");
	}
	
	@Test
	public void test2() throws ParseError {
		NonterminalSymbolNode sppf = levelParser.parse(Input.fromString("abcdefghijklm"), grammar1, "Id");
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf);
//		levelParser.parse(Input.fromString("abcdef"), grammar2, "Id");
	}
	

}