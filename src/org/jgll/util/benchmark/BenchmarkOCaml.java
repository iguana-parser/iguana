package org.jgll.util.benchmark;

import java.io.File;
import java.io.IOException;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;
import org.jgll.util.Visualization;

import com.google.common.testing.GcFinalization;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class BenchmarkOCaml extends AbstractBenchmark {
	
	private static Grammar grammar = getGrammar("grammars/ocaml/ocaml");
	
	private static Nonterminal startSymbol = Nonterminal.withName("TopLevel"); 
	
	public static void main(String[] args) throws Exception {
//		System.out.println(BenchmarkUtil.header());
//		parseFile(new File("/Users/aliafroozeh/test.ml"));
		for (File f : find("/Users/aliafroozeh/corpus/ocaml", "ml")) {
			System.out.println(f);
			GcFinalization.awaitFullGc();
			parseFile(f);
		}
	}

	private static void parseFile(File f) throws IOException {
		Input input = Input.fromFile(f);
		GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
		ParseResult result = parser.parse(input, grammar, startSymbol);
		if (result.isParseSuccess()) {
			System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
//			Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), parser.getRegistry(), input); 
		} else {
			System.out.println("Parse error: " + result.asParseError());
//			System.exit(0);
		}
		
	}
	
}
