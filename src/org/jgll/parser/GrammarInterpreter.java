package org.jgll.parser;

import org.jgll.grammar.Grammar;
import org.jgll.grammar.L0;
import org.jgll.grammar.Nonterminal;
import org.jgll.lookup.LookupTable;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.InputUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public class GrammarInterpreter extends GLLParser {
	
	private static final Logger log = LoggerFactory.getLogger(GrammarInterpreter.class);
	
	@Override
	public NonterminalSymbolNode parse(int[] input, Grammar grammar, Nonterminal startSymbol) throws ParseError {
		
		InputUtil.getInstance().setInput(input);
		log.info("Input size: {}", input.length);
		
		this.I = input;

		this.grammar = grammar;
		init();
		
		long start = System.nanoTime();
		
		startSymbol.execute(this);
		
		L0.getInstance().execute(this);
		
		long end = System.nanoTime();
		
		NonterminalSymbolNode root = lookupTable.getStartSymbol(startSymbol);
		if (root == null) {
			throw new ParseError(errorSlot, errorIndex);
		}
		
		logParseStatistics(end - start);
		
		return root;
	}

	@Override
	protected void init() {
		lookupTable = new org.jgll.lookup.LevelSynchronizedLookupTable(grammar, I.length);
		ci = 0;
		cu = u0 = GSSNode.DUMMY;
		cn = DummyNode.getInstance();
	}
	
	private void logParseStatistics(long duration) {
		log.info("Parsing Time: {} ms", duration/1000000);
		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		log.info("Memory used: {} mb", (runtime.totalMemory() - runtime.freeMemory()) / mb);
		log.info("Descriptors: {}", lookupTable.getDescriptorsCount());
		log.info("GSSNodes: {}", lookupTable.getGSSNodes().size());
		log.info("Non-packed nodes: {}", lookupTable.getDescriptorsCount());
		log.info("GSS Nodes: {}", lookupTable.getGSSNodesCount());
		log.info("GSS Edges: {}", lookupTable.getGSSEdgesCount());
	}

	public LookupTable getLookupTable() {
		return lookupTable;
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
	
	public SPPFNode getCN() {
		return cn;
	}
	
	public void setCN(SPPFNode node) {
		cn = node;
	}

	public void setCR(SPPFNode node) {
		cr = node;
	}
	
	public SPPFNode getCR() {
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
