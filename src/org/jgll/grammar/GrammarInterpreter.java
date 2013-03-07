package org.jgll.grammar;

import org.jgll.lookup.LookupTable;
import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.SPPFNode;

/**
 * 
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class GrammarInterpreter extends GLLParser {
	
	@Override
	protected void parse() {
		startSymbol.execute(this);
		L0.getInstance().execute(this);
	}

	@Override
	protected void init() {
		ci = 0;
		cu = u0 = GSSNode.DUMMY;
		cn = DummyNode.getInstance();
	}
	
	LookupTable getLookupTable() {
		return lookupTable;
	}
	
	GSSNode getCU() {
		return cu;
	}
	
	void setCU(GSSNode gssNode) {
		cu = gssNode;
	}
	
	void setInputIndex(int index) {
		ci = index;
	}
	
	SPPFNode getCN() {
		return cn;
	}
	
	void setCN(SPPFNode node) {
		cn = node;
	}

	void setCR(SPPFNode node) {
		cr = node;
	}
	
	SPPFNode getCR() {
		return cr;
	}
	
	int getCurrentInpuIndex() {
		return ci;
	}
	
	int getCurrentInputValue() {
		return I[ci];
	}
	
	void moveInputPointer() {
		ci = ci + 1;
	}
}
