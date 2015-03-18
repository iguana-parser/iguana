package org.jgll.parser.datadependent.preprocess;

import org.jgll.benchmark.IguanaBenchmark;
import org.jgll.grammar.Grammar;
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

public class TestPreprocessor {

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(CSharp.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("A"));
//	private static Nonterminal start = Nonterminal.withName("DPpGarbage");
	
	@Test
	public void test() throws Exception {
//		IguanaBenchmark.builder(grammar, start).addDirectory("/Users/aliafroozeh/corpus/CSharp/roslyn", "cs", true).build().run();
//		IguanaBenchmark.builder(grammar, start).addFile("/Users/aliafroozeh/input.cs").build().run();
		

		Input input = Input.fromPath("/Users/aliafroozeh/input.cs");
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, start);
		System.out.println(result);
		Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), input);
	}
}
