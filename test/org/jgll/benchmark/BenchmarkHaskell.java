package org.jgll.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.Haskell;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.grammar.symbol.Start;
import org.jgll.grammar.transformation.EBNFToBNF;
import org.jgll.grammar.transformation.LayoutWeaver;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.BenchmarkUtil;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

import com.google.common.collect.ImmutableSet;

public class BenchmarkHaskell extends AbstractBenchmark {
	
	private static String sourceDir = "/Users/aliafroozeh/corpus/ghc-output";

	private static Grammar grammar = new LayoutWeaver().transform(new EBNFToBNF().transform(Haskell.grammar));
	
	private static Nonterminal start = Start.from(Nonterminal.withName("Module"));
	
	private static Set<String> ignoreFiles = ImmutableSet.<String>builder()
			.add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/codeGen/StgCmmPrim.hs")
			.add("/Users/aliafroozeh/corpus/ghc-output/ghc/testsuite/tests/ghci/should_run/ghcirun004.hs")
			.build();
	
	private static Set<String> ignoreDirs = ImmutableSet.<String>builder()
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/basicTypes")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/cmm")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/cmm/Hoopl")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/codeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/codeGen/CodeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/codeGen/CodeGen/Platform")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/coreSyn")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/deSugar")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/ghci")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/hsSyn")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/iface")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/llvmGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/main")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/llvmGen/Llvm")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/llvmGen/LlvmCodeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/Dwarf")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/PPC")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/parser")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Graph")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Linear/PPC")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Linear/SPARC")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Linear")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Linear/X86")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc/Linear/X86_64")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/RegAlloc")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/SPARC")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/SPARC/CodeGen")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/nativeGen/X86")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/prelude")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/profiling")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/rename")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/simplCore")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/simplStg")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/specialise")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/stranal")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/typecheck")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/types")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/stgSyn")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/utils")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise/Monad")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise/Builtins")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise/Generic")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise/Type")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/compiler/vectorise/Vectorise/Utils")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/distrib/compare")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/ghc")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/includes")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/libraries/base/codepages")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/libraries/base/Control")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/libraries/base/Control/Concurrent")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/libraries/base/Control/Exception")
//			  .add("/Users/aliafroozeh/corpus/ghc-output/ghc/libraries/base/Control/Monad")
			  .build();

	private static Configuration config = Configuration.DEFAULT;
	
	private static int warmupCount = 0;
	private static int runCount = 1;
	
	
	public static void main(String[] args) throws IOException {
		
		List<File> files = find(sourceDir, "hs");

		GrammarGraph grammarGraph = grammar.toGrammarGraph(Input.empty(), config);
		
		System.out.println(BenchmarkUtil.header());
		
		for (File f : files) {
			
			if (ignoreFiles.contains(f.getAbsolutePath()))
				continue;
			
			if (ignoreDirs.contains(f.getParent()))
				continue;
			
			Input input = Input.fromFile(f);
			GLLParser parser = ParserFactory.getParser(config, input, grammar);
			
			for (int i = 0; i < warmupCount; i++) {
				parser.reset();
				grammarGraph.reset(input);
				parser.parse(input, grammarGraph, start);
			}
			
			for (int i = 0; i < runCount; i++) {
				parser.reset();
				grammarGraph.reset(input);
				System.out.println(f);
				ParseResult result = parser.parse(input, grammarGraph, start);
				if (result.isParseSuccess()) {
					System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
				} else {
					System.out.println("Parse error " + result.asParseError());
				}
			}
		}
	}
}
