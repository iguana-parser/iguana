package org.jgll.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.SPPFNode;

public class IguanaInterpreter {

	private static List<ParseResult> run(Grammar grammar, GrammarGraph grammarGraph, Input input, 
			                             String startSymbol, int warmupCount, int runCount) {
		
		List<ParseResult> results = new ArrayList<>();

		GLLParser parser = null;

		for (int i = 0; i < warmupCount; i++) {
			parser = ParserFactory.newParser(grammar, input);
			parser.parse(input, grammarGraph, startSymbol);
//			waitForGC();
		}
		
		if (runCount == 1) {
			parser = ParserFactory.newParser(grammar, input);
			results.add(parser.parse(input, grammarGraph, startSymbol));
//			Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", results.get(0).asParseSuccess().getSPPFNode(), grammarGraph, input);
		} else {
			for (int i = 0; i < runCount; i++) {
				parser = ParserFactory.newParser(grammar, input);
				results.add(parser.parse(input, grammarGraph, startSymbol));
//				waitForGC();
			}			
		}

		return results;
	}

	private void waitForGC() {
		System.gc();
		System.gc();
		System.runFinalization();

		final CountDownLatch latch = new CountDownLatch(1);
		new Object() {
			@Override
			protected void finalize() {
				latch.countDown();
			}
		};
		System.gc();
		System.gc();
		System.runFinalization();
		try {
			latch.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void printResult(List<ParseResult> results, int runCount, GrammarGraph grammarGraph, Input input) {

		long sumNanoTime = 0;
		long sumUserTime = 0;
		int sumMemory = 0;

		for (ParseResult result : results) {
			// TODO: Later add a check to ensure all results are parse errors
			if (result.isParseError()) {
				System.out.println(result.asParseError());
				return;
			}

			ParseStatistics parseStatistics = result.asParseSuccess().getParseStatistics();
			sumNanoTime += parseStatistics.getNanoTime();
			sumUserTime += parseStatistics.getUserTime();
			sumMemory += parseStatistics.getMemoryUsed();
			
			for (SPPFNode ambiguousNode : parseStatistics.getAmbiguousNodes()) {
//				Visualization.generateSPPFGraphWithoutIntermeiateNodes("/Users/aliafroozeh/output", ambiguousNode, grammarGraph, input);
			}
		}
		
		ParseStatistics parseStatistics = results.get(0).asParseSuccess().getParseStatistics();
		System.out.println(String.format("Input size: %s, Nano time: %d ms, User time: %d ms, Memory: %d mb, Ambiguities: %d",
				        parseStatistics.getInput().length(),
						sumNanoTime / (1000_000 * runCount),
						sumUserTime / (1000_000 * runCount),
						sumMemory / runCount,
 						parseStatistics.getCountAmbiguousNodes()));
		
		for (NonPackedNode ambiguousNode : parseStatistics.getAmbiguousNodes()) {
			Nonterminal nt = grammarGraph.getNonterminalById(ambiguousNode.getId());
			int line = input.getLineNumber(ambiguousNode.getLeftExtent());
			int column = input.getColumnNumber(ambiguousNode.getLeftExtent());
			System.out.println(String.format("Node: %s, length: %d, line: %d, column: %d", nt, 
					ambiguousNode.getRightExtent() - ambiguousNode.getLeftExtent(), line, column));
		}
 	}

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		Options options = new Options();
		
		options.addOption( "v", "verbose", false, "run verbosely." );
		
		options.addOption(OptionBuilder.withLongOpt("grammar")
		                  .withDescription("The grammar file")
		                  .hasArg()
		                  .isRequired()
		                  .create("g"));
		
		options.addOption(OptionBuilder.withLongOpt("start")
                .withDescription("The start symbol")
                .hasArg()
                .isRequired()
                .create("s"));

		options.addOption(OptionBuilder.withLongOpt("input")
                .withDescription("The input file")
                .hasArg()
                .create("i"));
		
		options.addOption(OptionBuilder.withLongOpt("dir")
                .withDescription("The directory to search for files")
                .hasArg()
                .create("d"));
		
		options.addOption(OptionBuilder.withLongOpt("ext")
                .withDescription("The file extension name to search")
                .hasArg()
                .create("e"));
		
		options.addOption(OptionBuilder.withLongOpt("warmup")
                .withDescription("The warmup count")
                .hasArg()
                .create("w"));
		
		options.addOption(OptionBuilder.withLongOpt("run")
                .withDescription("The run count")
                .hasArg()
                .create("r"));

		String grammarPath = null;
		String startSymbol = null;
		String inputDir = null;
		
		List<String> inputPaths = new ArrayList<>();
		
		int runCount = 1;
		int warmupCount = 0;
		
		CommandLineParser parser = new BasicParser();
	    try {
	        CommandLine line = parser.parse(options, args);
	        if (line.hasOption("g")) {
	        	grammarPath = line.getOptionValue("g");
	        }
	        if (line.hasOption("s")) {
	        	startSymbol = line.getOptionValue("s");
	        }

	        if (line.hasOption("i")) {
	        	String inputPath = line.getOptionValue("i");
				inputPaths.add(inputPath);
	        } else if (line.hasOption("d")) {
	        	inputDir = line.getOptionValue("d");
	        	
	        	if (!line.hasOption("e")) {
	        		System.out.println("File extension is not specified.");
	        		System.exit(1);
	        	} else {
	        		String ext = line.getOptionValue("e");
	        		@SuppressWarnings("rawtypes")
					Collection files = FileUtils.listFiles(new File(inputDir), new String[] {ext}, true);
	        		@SuppressWarnings("rawtypes")
					Iterator it = files.iterator();
	        		while(it.hasNext()) {
						inputPaths.add(((File) it.next()).getPath());
	        		}
	        	}
	        }
	    }
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		
		try {
			Grammar grammar = GrammarUtil.load(new File(grammarPath).toURI());
			GrammarGraph grammarGraph = grammar.toGrammarGraph();
			for (String inputPath : inputPaths) {
				Input input = Input.fromPath(inputPath);
				System.out.println("Parsing " + input.getURI() + "...");
				List<ParseResult> results = IguanaInterpreter.run(grammar, grammarGraph, input, startSymbol, warmupCount, runCount);
				printResult(results, runCount, grammarGraph, input);
			}		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
