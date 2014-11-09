package org.jgll.grammar.slot;

import org.jgll.lexer.GLLLexer;
import org.jgll.parser.GLLParser;
import org.jgll.parser.gss.GSSNode;
import org.jgll.util.Input;

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
public interface GrammarSlot {
	
	public abstract GrammarSlot parse(GLLParser parser, GLLLexer lexer);
	
	public int getId();
	
	public GSSNode getGSSNode(int inputIndex);
	
	public GSSNode hasGSSNode(int inputIndex);
	
	public boolean isInitialized();
	
	public void init(Input input);
	
	public void reset();

	public void code(StringBuilder sb);
	
}
