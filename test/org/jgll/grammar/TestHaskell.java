package org.jgll.grammar;

import java.io.IOException;

import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Test;

public class TestHaskell {
	
	static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Haskell.grammar));

	static Configuration config = Configuration.DEFAULT;
	
	static Start startSymbol = Start.from(Nonterminal.withName("Module"));
	
	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/Haskall/src/Main.hs");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, startSymbol);
//		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
		System.out.println(result);
	}
	
}
