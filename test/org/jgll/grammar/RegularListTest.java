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
import org.jgll.util.Input;
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
	
	private Grammar grammar;
	private GLLParser levelParser;
	private GLLParser rdParser;
	private GLLRecognizer recognizer;

	@Before
	public void init() {
		Rule r1 = new Rule(new Nonterminal("Id"), list(RegularList.plus(new CharacterClass(list(new Range('a', 'z'))))));
		grammar = new GrammarBuilder("RegularList").addRule(r1).build();
		levelParser = ParserFactory.levelParser(grammar, 10);
	}

	@Test
	public void test() throws ParseError {
		levelParser.parse(Input.fromString("abcdef"), grammar, "Id");
	}

}