package org.jgll.parser;

import org.jgll.exception.ParsingFailedException;
import org.jgll.grammar.Grammar;
import org.jgll.lookup.Lookup;
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
public class ParserInterpreter extends GLLParser {
	
	private static final Logger log = LoggerFactory.getLogger(ParserInterpreter.class);
	
	@Override
	public NonterminalSymbolNode parse(String input, Grammar grammar) throws ParsingFailedException {
		log.info("Input size: {}", input.length());

		this.grammar = grammar;
		init(input.length());
		
		return (NonterminalSymbolNode) grammar.getStartSymbol().execute(this);
	}

	@Override
	protected void init(int inputSize) {
		lookup = new org.jgll.lookup.MapLevelledLookup(grammar, inputSize);
		descriptorSet = new org.jgll.parser.LevelledDescritorSet(inputSize + 1, (org.jgll.lookup.LevelledLookup) lookup);

		ci = 0;
		cu = u0 = GSSNode.DUMMY;
		cn = DUMMY;
	}
	
	public DescriptorSet getDescriptorSet() {
		return descriptorSet;
	}
	
	public Lookup getLookup() {
		return lookup;
	}
	
	public GSSNode getCurrentGSSNode() {
		return cu;
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
