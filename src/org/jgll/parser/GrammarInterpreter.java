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
		
		grammar.getStartSymbol().execute(this);
		
		L0.getInstance().execute(this);
		
		NonterminalSymbolNode root = lookup.getStartSymbol();
		if (root == null) {
			throw new RuntimeException("Parsing Failed");
		}
		
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
