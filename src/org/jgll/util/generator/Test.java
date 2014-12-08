package org.jgll.util.generator;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.GrammarUtil;


public class Test {
	
	public static void main(String[] args) throws Exception {
		Grammar grammar = GrammarUtil.load(new File("/Users/aliafroozeh/csharp5").toURI());
		
		GrammarGraph grammarGraph = grammar.toGrammarGraphWithoutFirstFollowChecks();
		
		for (int i = 0; i < 1; i++) {
			
			long start = BenchmarkUtil.getUserTime();
			StringWriter writer = new StringWriter();
			grammarGraph.generate("org.jgll.util.generator", "Generated", new PrintWriter(writer));
			System.out.println(writer.toString());
			
			CompilationUtil.getClass("org.jgll.util.generator", "Generated", writer.toString());			
			long end = BenchmarkUtil.getUserTime();
				
			System.out.println((end - start) / 1000_000);
		}
		
	}
	
}