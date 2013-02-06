package org.jgll.lookup;

import java.util.Collection;
import java.util.List;

import org.jgll.grammar.BodyGrammarSlot;
import org.jgll.grammar.GrammarSlot;
import org.jgll.grammar.Nonterminal;
import org.jgll.parser.GSSNode;
import org.jgll.sppf.NonPackedNode;
import org.jgll.sppf.NonterminalSymbolNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalSymbolNode;

/**
 * 
 * @author Ali Afroozeh
 *
 */
public interface Lookup {
	
	public TerminalSymbolNode getTerminalNode(int terminalIndex, int leftExtent);
	
	public SPPFNode getNonPackedNode(GrammarSlot grammarSlot, int leftExtent, int rightExtent);
	
	public NonterminalSymbolNode getStartSymbol(Nonterminal startSymbol);
	
	public boolean getGSSEdge(GSSNode source, SPPFNode label, GSSNode destination);

	public GSSNode getGSSNode(GrammarSlot label, int inputIndex);
	
	public void addToPoppedElements(GSSNode gssNode, SPPFNode sppfNode);
	
	public List<SPPFNode> getEdgeLabels(GSSNode gssNode);
	
	public void createPackedNode(BodyGrammarSlot grammarSlot, int pivot, NonPackedNode parent, SPPFNode leftChild, SPPFNode rightChild);
	
	public int sizeNonPackedNodes();
	
	public int countGSSNodes();
	
	public int countGSSEdges();
	
	public Collection<GSSNode> getGSSNodes();
	
}
