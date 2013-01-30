package org.jgll.grammar;

import java.io.Serializable;


/**
 * A GrammarSlot is the position immediately before or after
 * any symbol in an alternate. They are denoted by LR(0) items. 
 * In X ::= alpha . beta, the grammar symbol denoted by . is after
 * alpha and before beta.  
 * 
 * @author Ali Afroozeh
 *
 */
public abstract class GrammarSlot implements Codeable, Serializable {
	
	protected final int id;
	protected final String name;
	
	public GrammarSlot(int id, String label) {
		this.id = id;
		this.name = label;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
		
	@Override
	public String toString() {
		return name;
	}
}
