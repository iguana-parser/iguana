package org.jgll.grammar;

import java.io.Serializable;

import org.jgll.parser.GSSEdge;
import org.jgll.util.Input;

public interface PopAction extends Serializable {
	
	/**
	 * 
	 * Does not execute the popAction if the action returns true.
	 * 
	 * @param edge
	 * @param inputIndex
	 * @param input
	 * @return
	 */
	public boolean execute(GSSEdge edge, int inputIndex, Input input);
	
}
