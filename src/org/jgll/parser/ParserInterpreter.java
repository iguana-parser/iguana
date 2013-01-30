package org.jgll.parser;

import org.jgll.exception.ParsingFailedException;
import org.jgll.grammar.Grammar;
import org.jgll.sppf.SPPFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Ali Afroozeh		<afroozeh@gmail.com>
 *
 */
public class ParserInterpreter extends GLLParser {
	
	private int label;
	
	private static final Logger log = LoggerFactory.getLogger(ParserInterpreter.class);
	
	private static final int L0 = -1;

	@Override
	public SPPFNode parse(String input, Grammar grammar) throws ParsingFailedException {
		log.info("Input size: {}", input.length());

		long start = System.nanoTime();

		init(input.length());

		long end = System.nanoTime();
		log.info("Initialization time: {} ms", (end - start) / 1000000);

		start = System.nanoTime();

		while (true) {
			// to be filled in!
		}
	}

	@Override
	protected void init(int inputSize) {
		
	}

}
