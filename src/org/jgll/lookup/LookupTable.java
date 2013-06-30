package org.jgll.lookup;

import java.util.Collection;
import java.util.List;

import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.HeadGrammarSlot;
import org.jgll.parser.Descriptor;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface LookupTable {
	
	public boolean hasNextDescriptor();
	
	public Descriptor nextDescriptor();
	
	public boolean addDescriptor(GrammarSlot slot, int inputIndex, GSSNode gssNode, SPPFNode sppfNode);
	
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent);
	
	public SPPFNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public NonterminalSymbolNode getStartSymbol(HeadGrammarSlot startSymbol);
	
	public boolean getGSSEdge(GSSNode source, SPPFNode label, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot label, int inputIndex);
	
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode);
	
	public List<SPPFNode> getEdgeLabels(GSSNode gssNode);
	
	public int getNonPackedNodesCount();
	
	public int getGSSNodesCount();
	
	public int getGSSEdgesCount();
	
	public int getDescriptorsCount();
	
	public Collection<GSSNode> getGSSNodes();
	
}
