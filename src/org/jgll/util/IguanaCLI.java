package org.jgll.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
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
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.parser.ParserFactory;
import org.jgll.util.benchmark.BenchmarkUtil;

public class IguanaCLI {

	public static void main(String[] args) throws Exception {
		
		Configuration config = Configuration.DEFAULT;
		Grammar grammar = null;
		
		Set<String> inputPaths = new LinkedHashSet<>();
		Set<String> ignorePaths = new LinkedHashSet<>();
		
		Nonterminal startSymbol = null;

		
		CommandLineParser commandLineParser = new BasicParser();
		
	    try {
	        CommandLine line = commandLineParser.parse(getOptions(), args);
	        
	        // Grammar
	        if (line.hasOption("g")) {
	        	String grammarPath = line.getOptionValue("g");
				grammar = GrammarUtil.load(new File(grammarPath).toURI());
	        }
	        
	        // Start symbol
	        if (line.hasOption("s")) {
	        	startSymbol = Nonterminal.withName(line.getOptionValue("s"));
	        }
	        						
	        // Input
	        if (line.hasOption("i")) {
	        	inputPaths.add(line.getOptionValue("i"));
	        } 
	        
	        else if (line.hasOption("d")) {
	    		String inputDir = null;
	    		
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
						if (!ignorePaths.contains(path)) {
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
	    					parse(startSymbol, grammar, Input.fromPath(inputPath), config);
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
					parse(startSymbol, grammar, Input.fromString(commandLineInput), config);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    }
		catch (ParseException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void parse(Nonterminal startSymbol, Grammar grammar, Input input, Configuration config) {
		GLLParser parser = ParserFactory.getParser(config, input, grammar);
		ParseResult result = parser.parse(input, grammar, startSymbol);
		if (result.isParseSuccess()) {
			System.out.println(BenchmarkUtil.format(input, result.asParseSuccess().getStatistics()));
		} else {
			System.out.println("Parse error.");
		}
	}
	
	@SuppressWarnings("static-access")
	public static Options getOptions() {
		
		Options options = new Options();
		
		options.addOption( "v", "verbose", false, "Verbose." );
		
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
		
		return options;
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
