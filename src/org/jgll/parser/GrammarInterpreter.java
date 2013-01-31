package org.jgll.parser;

import org.jgll.exception.ParsingFailedException;
import org.jgll.grammar.Grammar;
import org.jgll.grammar.L0;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Ali Afroozeh		<afroozeh@gmail.com>
 *
 */
public class GrammarInterpreter extends GLLParser {
	
	private static final Logger log = LoggerFactory.getLogger(GrammarInterpreter.class);
	
	@Override
	public NonterminalSymbolNode parse(int[] input, Grammar grammar) throws ParsingFailedException {
		log.info("Input size: {}", input.length);

		this.grammar = grammar;
		init();
		
		long start = System.nanoTime();
		
		grammar.getStartSymbol().execute(this);
		
		L0.getInstance().execute(this);
		
		long end = System.nanoTime();
		
		NonterminalSymbolNode root = lookup.getStartSymbol();
		if (root == null) {
			// TODO put ParsingFailedException back
			throw new RuntimeException("Parsing Failed");
		}
		
		logParseStatistics(end - start);
		
		return root;
	}

	@Override
	protected void init() {
		lookup = new org.jgll.lookup.MapLevelledLookup(grammar, I.length);
		descriptorSet = new org.jgll.parser.LevelledDescritorSet(I.length, (org.jgll.lookup.LevelledLookup) lookup);

		ci = 0;
		cu = u0 = GSSNode.DUMMY;
		cn = DUMMY;
	}
	
	private void logParseStatistics(long duration) {
		log.info("Parsing Time: {} ms", duration/1000000);
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.info("Memory used: {} mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.info("Descriptors: {}", descriptorSet.sizeAll());
		log.info("GSSNodes: {}", lookup.getGSSNodes().size());
		log.info("Non-packed nodes: {}", lookup.sizeNonPackedNodes());
	}

	
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}
	
	public GSSNode getCU() {
		return cu;
	}
	
	public void setCU(GSSNode gssNode) {
		cu = gssNode;
	}
	
	public void setInputIndex(int index) {
		ci = index;
	}
	
	public NonPackedNode getCN() {
		return cn;
	}
	
	public void setCN(NonPackedNode node) {
		cn = node;
	}

	public void setCR(NonPackedNode node) {
		cr = node;
	}
	
	public NonPackedNode getCR() {
		return cr;
	}
	
	public int getCurrentInpuIndex() {
		return ci;
	}
	
	public int getCurrentInputValue() {
		return I[ci];
	}
	
	public void moveInputPointer() {
		ci = ci + 1;
	}
}
