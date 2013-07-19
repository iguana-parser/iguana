package org.jgll.grammar;

import java.io.Serializable;

import org.jgll.parser.GSSEdge;
import org.jgll.util.Input;

public interface PopAction extends Serializable {
	
	public boolean execute(GSSEdge edge, int inputIndex, Input input);
	
}
