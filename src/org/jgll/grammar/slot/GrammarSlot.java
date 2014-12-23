package org.jgll.grammar.slot;

import java.io.PrintWriter;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.util.Input;
import org.jgll.util.generator.ConstructorCode;

/**
 * A GrammarSlot is a position immediately before or after
 * a symbol in the body of a production rule. 
 * Grammar slots, similar to LR items, are represented by  
 * For example, in the rule X ::= alpha . beta, the grammar 
 * slot, denoted by ., is after
 * alpha and before beta, where alpha are a list of grammar symbols.
 * 
 *
 * @author Ali Afroozeh
 *
 */
public interface GrammarSlot extends ConstructorCode {
	
	public void execute(GLLParser parser, Input input, NonPackedNode node);
	
	default GSSNode getGSSNode(int inputIndex) { return null; }
	
	default GSSNode hasGSSNode(int inputIndex) { return null; }
	
	default boolean isInitialized() { return false; }
	
	default void init(Input input) { }
	
	default void reset() { }

	default void code(PrintWriter writer, GrammarSlotRegistry registry) { }

	default GrammarSlot withId(int id) { return this; }
	
	default int getId() {return 0; }

}
