package org.jgll.lookup;

import java.util.Collection;
import java.util.List;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.GrammarSlot;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonPackedNodeWithChildren;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.TerminalSymbolNode;

public interface Lookup {
	
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent, int rightExtent);
	
	public NonPackedNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public NonterminalSymbolNode getStartSymbol();
	
	public boolean getGSSEdge(GSSNode source, NonPackedNode label, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot label, int inputIndex);
	
	public void addToPoppedElements(GSSNode gssNode, NonPackedNode sppfNode);
	
	public List<NonPackedNode> getEdgeLabels(GSSNode gssNode);
	
	public void createPackedNode(BodyGrammarSlot grammarSlot, int pivot, NonPackedNodeWithChildren parent, NonPackedNode leftChild, NonPackedNode rightChild);
	
	public int sizeNonPackedNodes();
	
	public int countGSSNodes();
	
	public int countGSSEdges();
	
	public Collection<GSSNode> getGSSNodes();
	
}
