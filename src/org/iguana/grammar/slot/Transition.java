package org.iguana.grammar.slot;

import org.iguana.datadependent.env.Environment;
import org.iguana.parser.GLLParser;
import org.iguana.parser.gss.GSSNode;
import org.iguana.sppf.NonPackedNode;
import org.iguana.util.generator.ConstructorCode;


public interface Transition extends ConstructorCode {
	
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node);
	
	public GrammarSlot destination();
	
	public GrammarSlot origin();

	public String getLabel();
	
	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public void execute(GLLParser parser, GSSNode u, int i, NonPackedNode node, Environment env);
	
}
