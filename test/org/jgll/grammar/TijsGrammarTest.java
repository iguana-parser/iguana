package org.jgll.grammar;

import java.util.ArrayList;
import java.util.List;

import org.jgll.sppf.NonterminalSymbolNode;
import org.junit.Test;

public class TijsGrammarTest extends AbstractGrammarTest {

	/**
	 * Pattern ::= "int"
	 *		     | "str"
	 * 		     | "real"
	 *		     | "sym"
	 *		     | "atom"
	 *		     | str
	 *		     | Id
	 *		     | Pattern "*" Sep
	 *			 | Pattern "*"
	 *		     | Pattern "?"
	 *		     | Pattern "+" Sep
	 *			 | Pattern "+"
	 *		     | "."
	 *		     | "/"
	 *		     | ">"
	 *		     | "<"
	 *		     | "(" Alt ")"
	 *
	 *  Sep ::= "@" Pattern
	 *  
	 *  Id ::= [a-z]+
	 */
	
	@Override
	protected Grammar initGrammar() {
		
		List<Rule> rules = new ArrayList<>();
		
		//Pattern
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('i'), new Character('n'), new Character('t')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('s'), new Character('t'), new Character('r')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('r'), new Character('e'), new Character('a'), new Character('l')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('s'), new Character('y'), new Character('m')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('a'), new Character('t'), new Character('o'), new Character('m')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("str")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("Id")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("Pattern"), new Character('*'), new Nonterminal("Sep")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("Pattern"), new Character('*')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("Pattern"), new Character('+'), new Nonterminal("Sep")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Nonterminal("Pattern"), new Character('+')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('.')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('/')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('>')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('<')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Pattern")).body(new Character('('), new Nonterminal("Alt"), new Character(')')).build());
		
		rules.add(new Rule.Builder().head(new Nonterminal("Sep")).body(new Character('@'), new Nonterminal("Pattern")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Id")).body(new Nonterminal("Id"), new Range('a', 'z')).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Id")).body(new Range('a', 'z')).build());
		
		// Alt 
		rules.add(new Rule.Builder().head(new Nonterminal("Alt")).body(new Nonterminal("Alt"), new Nonterminal("Create")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Alt")).body(new Nonterminal("Create")).build());
		
		//Create ::= "[" sym "]" Sequence
		//	    | Sequence
		rules.add(new Rule.Builder().head(new Nonterminal("Create")).body(new Character('['), new Nonterminal("sym"), new Character(']'), new Nonterminal("Sequence")).build());

		// Sequence ::= Sequence Field
		// Sequence ::= 
		rules.add(new Rule.Builder().head(new Nonterminal("Sequence")).body(new Nonterminal("Sequence"), new Nonterminal("Field")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("Sequence")).build());

		
		// Field ::= sym ":" Pattern
		rules.add(new Rule.Builder().head(new Nonterminal("Field")).body(new Nonterminal("sym"), new Character(':'), new Nonterminal("Pattern")).build());
		// Field ::= Pattern
		rules.add(new Rule.Builder().head(new Nonterminal("Field")).body(new Nonterminal("Pattern")).build());
		
		rules.add(new Rule.Builder().head(new Nonterminal("sym")).body(new Nonterminal("Id")).build());
		rules.add(new Rule.Builder().head(new Nonterminal("str")).body(new Character('"'), new Nonterminal("Id"), new Character('"')).build());
		
		return Grammar.fromRules("gamma2", rules);
	}
	
	
	@Test
	public void test() {
		System.out.println(grammar);
		NonterminalSymbolNode sppf = rdParser.parse("abc", grammar, "Pattern");
		generateGraphWithIntermeiateNodes(sppf);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
