package org.jgll.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;

import com.google.common.testing.GcFinalization;

public class IguanaBenchmark {

	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		
		System.out.println(BenchmarkUtil.header());

		
		Set<String> skipList = new HashSet<>();
//		try (Scanner scanner = new Scanner(new File("/Users/aliafroozeh/list.txt"))){
//			while(scanner.hasNextLine()) {
//				skipList.add(scanner.nextLine());
//			}
//		} catch (FileNotFoundException e2) {
//			e2.printStackTrace();
//		}
		
		Options options = new Options();
		
		options.addOption( "v", "verbose", false, "run verbosely." );
		
		options.addOption(OptionBuilder.withLongOpt("grammar")
		                  .withDescription("The grammar file")
		                  .hasArg()
		                  .isRequired()
		                  .create("g"));
		
		options.addOption(OptionBuilder.withLongOpt("no-first-follow")
                .withDescription("Enable first/follow checks")
                .create("n"));
		
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
		
		options.addOption(OptionBuilder.withLongOpt("ignore")
                .withDescription("The files to be ignored")
                .hasArgs()
                .create());
		
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

		Grammar grammar = null;
		Class<?> clazz = null;
		
		
		CommandLineParser commandLineParser = new BasicParser();
		
	    try {
	        CommandLine line = commandLineParser.parse(options, args);
	        
	        // Grammar
	        if (line.hasOption("g")) {
	        	grammarPath = line.getOptionValue("g");
				try {
					grammar = GrammarUtil.load(new File(grammarPath).toURI());
//					System.out.println(grammar);
					
//					if (line.hasOption("n")) {
//						grammarGraph = grammar.toGrammarGraphWithoutFirstFollowChecks();
//					} else {
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        
	        // Start symbol
	        if (line.hasOption("s")) {
	        	startSymbol = line.getOptionValue("s");
	        }
	        
			int runCount = 1;
			int warmupCount = 1;

			if (line.hasOption("r")) {
				try {
					runCount = Integer.parseInt(line.getOptionValue("r"));
				} catch(Exception e) {
					System.out.println("Run count should be a positive number.");
					System.exit(1);
				}
			}
			
			if (line.hasOption("w")) {
				try {
					warmupCount = Integer.parseInt(line.getOptionValue("w"));
				} catch(Exception e) {
					System.out.println("Warm up count should be a positive number.");
					System.exit(1);
				}
			}
			
			try {
				warmup(startSymbol, grammar, warmupCount);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	        
	        // Input
	        if (line.hasOption("i")) {
	        	String inputPath = line.getOptionValue("i");
	        	try {
					parse(startSymbol, runCount, grammar, Input.fromPath(inputPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
	        } else if (line.hasOption("d")) {
	    		String inputDir = null;
	    		
	    		Set<String> inputPaths = new LinkedHashSet<>();
	    		Set<String> ignorePaths = new LinkedHashSet<>();
//	    		ignorePaths.add("mcs/class/corlib/System/Console.cs");
	        	
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
						String path = ((File) it.next()).getPath();
						if (!skipList.contains(path)) {
							inputPaths.add(path);							
						}
	        		}
	        	}
	        	
	        	if (line.hasOption("ignore")) {
	        		for (String option : line.getOptionValues("ignore")) {
	        			ignorePaths.add(option);
	        		}
	        	}
	        	
	    		try {
	    			for (String inputPath : inputPaths) {								
	    				if (!ignore(ignorePaths, inputPath)) {
	    					parse(startSymbol, runCount, grammar, Input.fromPath(inputPath));
	    				}
	    			}
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	        	
	        } else {
	        	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        	try {
					String commandLineInput = in.readLine();
					System.out.println(commandLineInput);
					parse(startSymbol, runCount, grammar, Input.fromString(commandLineInput));
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		
	}

	private static void warmup(String startSymbol, Grammar grammar, int warmupCount) throws IOException{
		for (int i = 0; i < warmupCount; i++) {
			Input input = Input.fromPath("/Users/aliafroozeh/test.cs");
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, startSymbol);
			System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getParseStatistics()));
		}
	}
	
	private static void parse(String startSymbol, int runCount, Grammar grammar, Input input) throws IOException {
		System.out.println(input.getURI());
		for (int i = 0; i < runCount; i++) {
			GLLParser parser = ParserFactory.getParser(Configuration.DEFAULT, input, grammar);
			ParseResult result = parser.parse(input, grammar, startSymbol);
			if (result.isParseSuccess()) {
				System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getParseStatistics()));
//				Visualization.generateSPPFGraph("/Users/aliafroozeh/output", result.asParseSuccess().getRoot(), grammarGraph, input);
//				System.exit(0);
			} else {
//				System.out.println("Parse error.");
				System.out.println(result.asParseError());
				break;
//				System.exit(0);
			}
			parser.reset();
			parser = null;
			result = null;
			GcFinalization.awaitFullGc();
		}
		
		GcFinalization.awaitFullGc();
	}
	
	private static boolean ignore(Set<String> ignorePaths, String inputPath) {
		if (ignorePaths.isEmpty()) {
			return false;
		}
		for (String s : ignorePaths) {
			if (!inputPath.endsWith(s)) {
				return true;
			}
		}
		return false;
	}

}
