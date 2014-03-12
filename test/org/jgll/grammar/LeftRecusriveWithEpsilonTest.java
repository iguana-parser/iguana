package org.jgll.grammar;

import static org.jgll.util.CollectionsUtil.*;

import org.jgll.grammar.slot.factory.FirstFollowSetGrammarSlotFactory;
import org.jgll.grammar.slot.factory.GrammarSlotFactory;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Rule;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseError;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.jgll.util.ToJavaCode;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * A ::= A A | a | epsilon
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class LeftRecusriveWithEpsilonTest {
	
	private Grammar grammar;

	@Before
	public void init() {
		Nonterminal A = new Nonterminal("A");
		
		Rule r1 = new Rule(A, list(A, A));
		Rule r2 = new Rule(A, list(new Character('a')));
		Rule r3 = new Rule(A);

		GrammarSlotFactory factory = new FirstFollowSetGrammarSlotFactory();
		GrammarBuilder builder = new GrammarBuilder("IndirectRecursion", factory)
													  .addRule(r1)
													  .addRule(r2)
													  .addRule(r3);
		
		grammar = builder.build();
	}
	
	@Test
	public void test() throws ParseError {
		Input input = Input.fromString("");
		GLLParser parser = ParserFactory.newParser(grammar, input);
		NonterminalSymbolNode sppf = parser.parse(input, grammar, "A");
//		System.out.println(ToJavaCode.toJavaCode(sppf, grammar));
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", sppf, grammar, input);
	}

}
