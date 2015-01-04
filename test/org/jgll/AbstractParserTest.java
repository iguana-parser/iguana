package org.jgll;

import java.util.function.Function;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.GrammarRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.ParserFactory;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Configuration;
import org.jgll.util.Input;

public class AbstractParserTest {

	protected Grammar grammar;
	
	protected GLLParser parser;
	
	protected Input input;
	
	protected Function<GrammarRegistry, SPPFNode> expectedSPPF;
	
	public AbstractParserTest(Configuration config, Input input, Grammar grammar, Function<GrammarRegistry, SPPFNode> expectedSPPF) {
		this.parser = ParserFactory.getParser(config, input, grammar);
		this.input = input;
		this.grammar = grammar;
		this.expectedSPPF = expectedSPPF;
	}
	
}
