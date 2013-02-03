package org.jgll.grammar;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import org.jgll.parser.GrammarInterpreter;


/**
 * A GrammarSlot is the position immediately before or after
 * any symbol in an alternate. They are denoted by LR(0) items. 
 * In X ::= alpha . beta, the grammar symbol denoted by . is after
 * alpha and before beta.  
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
	
	public abstract void execute(GrammarInterpreter parser);
	
	public int getId() {
		return id;
	}	
}
