package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.parser.GLLParser;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.util.Input;



/**
 * A GrammarSlot is the position immediately before or after
 * any symbol in an alternate. They are denoted by LR(0) items. 
 * In X ::= alpha . beta, the grammar symbol denoted by . is after
 * alpha and before beta.
 * 
 * Note: the equality of two grammar slots is based on their
 * referential equality, i.e., if they refer to the same object.
 * Thet's why these classes inherit the implementation from the
 * Object class.
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class GrammarSlot implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * A unique integer specifying this grammar slot.
	 * The id field is used later for looking up grammar slots.
	 */
	protected final int id;
	
	
	public GrammarSlot(int id) {
		this.id = id;
	}
	
	public abstract void code(Writer writer) throws IOException;
	
	public abstract void parse(GLLParser parser, Input input, GSSNode cu, SPPFNode cn, int ci);
	
	public abstract String getName();
	
	public int getId() {
		return id;
	}	
}
