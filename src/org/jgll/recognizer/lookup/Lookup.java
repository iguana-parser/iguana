package org.jgll.recognizer.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.recognizer.Descriptor;
import org.jgll.recognizer.GSSNode;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface Lookup {
	
	public boolean hasDescriptor();
	
	public Descriptor nextDescriptor();
	
	public boolean addDescriptor(Descriptor descriptor);
	
	public boolean hasGSSEdge(GSSNode source, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot slot, int inputIndex);
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public int getDescriptorsCount();
	
	public Iterable<GSSNode> getGSSNodes();
	
	public void init(Input input);
	
}	
