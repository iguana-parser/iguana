package org.jgll.parser.gss.lookup;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.gss.GSSNode;
import org.jgll.parser.gss.GSSNodeData;
import org.jgll.util.Input;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface GSSNodeLookup {
	
	public GSSNode getOrElseCreate(GrammarSlot slot, int i);
	
	public GSSNode get(int i);
	
	public void reset(Input input);
	
	public Iterable<GSSNode> getNodes();
	
	public GSSNodeLookup init();
	
	public boolean isInitialized();

	/**
	 * 
	 * Data-dependent GLL parsing
	 * 
	 */
	public <T> GSSNode getOrElseCreate(GrammarSlot slot, int i, GSSNodeData<T> data);
	
	public <T> GSSNode get(int i, GSSNodeData<T> data);

}
