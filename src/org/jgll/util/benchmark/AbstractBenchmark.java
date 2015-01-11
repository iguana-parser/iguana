package org.jgll.util.benchmark;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarGraph;
import org.jgll.grammar.symbol.Nonterminal;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParseResult;
import org.jgll.util.Configuration;
import org.jgll.util.GrammarUtil;
import org.jgll.util.Input;
import org.openjdk.jmh.annotations.Param;

public class AbstractBenchmark {

	@Param({""})
	protected String inputString;
	
	protected Configuration config;
	
	protected Grammar grammar;
	
	protected Nonterminal startSymbol;
	
	protected GrammarGraph grammarGraph;
	
	protected GLLParser parser;
	
	protected ParseResult result;
	
	protected static List<Input> find(String dir, String ext) throws IOException {
		List<Input> inputs = new ArrayList<>();
		Collection<?> files = FileUtils.listFiles(new File(dir), new String[] {ext}, true);
		Iterator<?> it = files.iterator();
		while(it.hasNext()) {
			String path = ((File) it.next()).getPath();
			inputs.add(Input.fromPath(path));							
		}
		return inputs;
	}
	
	protected static Grammar getGrammar(String path) {
		try {
			return GrammarUtil.load(new File(path).toURI());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
