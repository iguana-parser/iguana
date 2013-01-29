package org.jgll.parser;

import org.jgll.exception.ParsingFailedException;
import org.jgll.grammar.Grammar;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParserInterpreter extends GLLParser {
	
	private int label;
	
	private static final Logger log = LoggerFactory.getLogger(ParserInterpreter.class);
	
	private static final int L0 = -1;

	@Override
	public SPPFNode parse(String input, Grammar grammar) throws ParsingFailedException {
		log.info("Input size: {}", input.length());

		long start = System.nanoTime();

		init(input.length());

		label = 0;

		long end = System.nanoTime();
		log.info("Initialization time: {} ms", (end - start) / 1000000);

		start = System.nanoTime();

		while (true) {
			switch (label) {
			case L0:
				if (!descriptorSet.isEmpty()) {
					Descriptor descriptor = descriptorSet.nextDescriptor();
					cu = descriptor.getGSSNode();
					ci = descriptor.getInputIndex();
					cn = descriptor.getSPPFNode();
					label = descriptor.getLabel();
					break;
				} else {
					end = System.nanoTime();

					NonterminalSymbolNode root = lookup.getStartSymbol();

					if (root == null) {
						log.info("Parsing failed.");
						throw new ParsingFailedException(errorNonterminal, errorIndex, input);
					}

					log.info("Parsing Time: {} ms", (end - start) / 1000000);

					int mb = 1024 * 1024;
					Runtime runtime = Runtime.getRuntime();
					log.info("Memory used: {} mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
					log.info("Descriptors: {}", descriptorSet.sizeAll());
					log.info("Non-packed nodes: {}", lookup.sizeNonPackedNodes());

					return root;
				}
			}
		}
	}

	@Override
	protected void init(int inputSize) {
		
	}

}
