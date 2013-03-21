package org.jgll.grammar;

import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.util.Input;
import org.junit.Test;

/**
 * 
 * S ::= a S b S
 *     | a S
 *     | s
 * 
 * @author Ali Afroozeh
 *
 */
public class DanglingElseGrammar extends AbstractGrammarTest {

	private Rule rule1;
	private Rule rule2;
	private Rule rule3;

	@Override
	protected Grammar initGrammar() {
		rule1 = new Rule.Builder().head(new Nonterminal("S"))
				.body(new Character('a'), new Nonterminal("S"), new Character('b'), new Nonterminal("S")).build();
		rule2 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('a'), new Nonterminal("S")).build();
		rule3 = new Rule.Builder().head(new Nonterminal("S")).body(new Character('s')).build();
		return Grammar.fromRules("DanglingElse", set(rule1, rule2, rule3));
	}
	
	@Test
	public void test() {
		BodyGrammarSlot grammarSlot = grammar.getGrammarSlot(rule3, 1);
		grammar.replace(rule3, 1, WrapperSlot.after(grammarSlot, new SlotAction() {
			
			@Override
			public void execute(GrammarSlot slot) {
				System.out.println("I'm Here!");
			}
		}));
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aasbs"), grammar, "S");
		generateGraph(sppf);
	}

}
