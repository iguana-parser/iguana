package org.jgll.grammar;

import java.util.ArrayDeque;
import java.util.Deque;

import org.jgll.parser.Descriptor;
import org.jgll.parser.GLLParser;
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
		return Grammar.fromRules("DanglingElse", list(rule1, rule2, rule3));
	}
	
	@Test
	public void test() {
		
		final Deque<Descriptor> stack = new ArrayDeque<>();
		
		BodyGrammarSlot grammarSlot = grammar.getGrammarSlot(rule1, 0);
		grammar.replace(rule1, 0, WrapperSlot.before(grammarSlot, new SlotAction() {

			@Override
			public void execute(GrammarSlot slot, GLLParser parser, Input input) {
				Descriptor descriptor = new Descriptor(grammar.getGrammarSlot(rule2, 0), parser.getCu(), parser.getCi(), parser.getCn());
				System.out.println("Descriptor recorded: " + descriptor);
				stack.push(descriptor);
			}
			
		}));
		
		grammarSlot = grammar.getGrammarSlot(rule1, 4);
		grammar.replace(rule1, 4, WrapperSlot.after(grammarSlot, new SlotAction() {

			@Override
			public void execute(GrammarSlot slot, GLLParser parser, Input input) {
				System.out.println("Hi");
				parser.removeDescriptor(stack.pop());
			}
			
		}));
		
		NonterminalSymbolNode sppf = rdParser.parse(Input.fromString("aasbs"), grammar, "S");
		generateGraph(sppf);
	}

}
