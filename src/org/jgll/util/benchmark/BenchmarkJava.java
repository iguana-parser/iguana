package org.jgll.util.benchmark;

import java.io.File;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public class BenchmarkJava extends AbstractBenchmark {
	
	private static Grammar grammar = getGrammar("grammars/java/specification/java");
	
	private static Nonterminal startSymbol = Nonterminal.withName("start[CompilationUnit]"); 
	
	public static void main(String[] args) throws Exception {
		for (File f : find("/Users/aliafroozeh/corpus/Java/jdk1.7.0_60-b19", "java")) {
			Input input = Input.fromFile(f);
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, startSymbol);
			if (result.isParseSuccess()) {
				System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
			} else {
				System.out.println("Parse error: " + result.asParseError());
				System.exit(0);
			}
		}
	}
	
}
