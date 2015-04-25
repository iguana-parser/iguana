package org.iguana.grammar;

import java.io.File;
import java.io.IOException;

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Start;
import org.iguana.grammar.transformation.EBNFToBNF;
import org.iguana.grammar.transformation.LayoutWeaver;
import org.iguana.parser.GLLParser;
import org.iguana.parser.ParseResult;
import org.iguana.parser.ParserFactory;
import org.iguana.util.Configuration;
import org.iguana.util.Input;
import org.junit.Test;

public class TestCSharp {
	
	private static Grammar originalGrammar = Grammar.load(new File("grammars/csharp/csharp"));
	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(originalGrammar));
	private static Start start = Start.from(Nonterminal.withName("CompilationUnit"));

	@Test
	public void test() throws IOException {
		Input input = Input.fromPath("/Users/aliafroozeh/test.cs");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, start);
		System.out.println(result);
	}
}
